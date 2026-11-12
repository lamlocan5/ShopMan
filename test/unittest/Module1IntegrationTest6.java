package unittest;

import dao.*;
import model.*;
import view.ViewSupport6;
import view.khachhang.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

/** Exercises real MySQL and a private Tomcat instance; never deploys to the user's running server. */
public final class Module1IntegrationTest6 {
    private static int checks;
    private static Path projectRoot;
    private static String testDatabase;
    private static String serverUrl;
    private static String username;
    private static String password;
    private static Process tomcat;
    private static Account6 customer;

    public static void main(String[] args) throws Exception {
        projectRoot=Paths.get(args[0]).toAbsolutePath();
        Path tomcatHome=Paths.get(args[1]).toAbsolutePath();
        serverUrl=System.getenv("SHOPMAN_TEST_JDBC_URL");
        username=System.getenv("SHOPMAN_TEST_JDBC_USER");
        password=System.getenv("SHOPMAN_TEST_JDBC_PASSWORD");
        if(serverUrl==null||username==null||password==null)
            throw new IllegalArgumentException("Set SHOPMAN_TEST_JDBC_URL / USER / PASSWORD before running.");
        Class.forName("com.mysql.cj.jdbc.Driver");
        testDatabase="shopman_m1_test_"+UUID.randomUUID().toString().replace("-","");
        URI databaseUri=URI.create(serverUrl.substring(5));
        String jdbcUrl="jdbc:mysql://"+databaseUri.getRawAuthority()+"/"+testDatabase
                +(databaseUri.getRawQuery()==null?"":"?"+databaseUri.getRawQuery());
        boolean created=false;
        try(Connection master=DriverManager.getConnection(serverUrl,username,password)){
            try(Statement statement=master.createStatement()){
                statement.executeUpdate("CREATE DATABASE `"+testDatabase+"` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");created=true;
            }
            try(Connection con=DriverManager.getConnection(jdbcUrl,username,password)){
                executeScript(con,projectRoot.resolve("database/module1-schema.sql"));
                executeScript(con,projectRoot.resolve("database/module1-seed.sql"));
                verifyDAOAndView(con);
                String before=fingerprint(con);
                startTomcat(tomcatHome,jdbcUrl);
                verifyHttp();
                equal(before,fingerprint(con),"HTTP browsing preserves every table");
            }
        }finally{
            if(tomcat!=null){tomcat.destroy();if(!tomcat.waitFor(10,TimeUnit.SECONDS)){tomcat.destroyForcibly();tomcat.waitFor(10,TimeUnit.SECONDS);}}
            if(created){
                if(!testDatabase.matches("shopman_m1_test_[a-f0-9]{32}"))throw new SecurityException("Refusing to drop an unowned database.");
                try(Connection master=DriverManager.getConnection(serverUrl,username,password);Statement statement=master.createStatement()){
                    statement.executeUpdate("DROP DATABASE `"+testDatabase+"`");
                }
            }
        }
        String report="PASS: "+checks+" checks; Java, live MySQL DAO/View and real Tomcat/JSP flow. Temporary database removed; private Tomcat stopped.";
        Files.write(projectRoot.resolve(".verification/test-result.txt"),report.getBytes(StandardCharsets.UTF_8));
        System.out.println(report);
    }

    private static void executeScript(Connection con,Path path)throws Exception{
        String sql=new String(Files.readAllBytes(path),StandardCharsets.UTF_8).replaceAll("(?m)^\\s*--.*$","");
        try(Statement statement=con.createStatement()){
            for(String part:sql.split(";")){
                String text=part.trim();
                if(text.isEmpty()||text.toUpperCase(Locale.ROOT).startsWith("CREATE DATABASE")||text.toUpperCase(Locale.ROOT).startsWith("USE "))continue;
                statement.execute(text);
            }
        }
    }
    private static void verifyDAOAndView(Connection con)throws Exception{
        AccountDAO6 accounts=new AccountDAO6(con,null);
        Account6 signedIn=accounts.checkLogin("kh01","Kh01@123");
        customer=null; // All search tests must succeed without authentication.
        check(signedIn!=null,"optional sample customer login");
        check(signedIn.getPasswordHash()==null,"session does not expose passwordHash");
        check(accounts.checkLogin("kh01","wrong")==null,"wrong password");
        check(accounts.checkLogin("' OR 1=1 --","anything")==null,"login SQL literal");
        String h=PasswordHash6.hash("test-value");
        check(PasswordHash6.verify("test-value",h),"password hash verification");
        check(!PasswordHash6.verify("other",h),"password mismatch");
        check(!PasswordHash6.verify("test-value","invalid"),"malformed hash");
        check(!PasswordHash6.hash("test-value").equals(h),"random password salt");
        ProductDAO6 products=new ProductDAO6(con,customer);
        List<Product6> result=products.searchByName("  áo thun  ",1);
        equal(3,result.size(),"trimmed name search");equal(3L,products.getTotalResultCount(),"result count");
        check(products.findActiveById("P99")==null,"inactive product hidden");
        check(products.findActiveById("missing")==null,"missing product");
        equal("C01",products.findActiveById("P01").getCategory().getCategoryId(),"business category code mapping");
        equal("Quần áo",new CategoryDAO6(con,customer).findById("C01").getName(),"category details");
        ProductVariantDAO6 variants=new ProductVariantDAO6(con,customer);
        equal(3,variants.findActiveByProduct("P01").size(),"inactive variants excluded");
        check(variants.findActiveById("AT-BAS-OLD")==null,"inactive variant hidden");
        check(variants.findActiveById("AT-STOP")==null,"active variant of inactive parent hidden");
        InventoryDAO6 inventory=new InventoryDAO6(con,customer);
        equal(8,inventory.getAvailableQty("AT-BAS-W-L"),"inactive warehouse excluded");
        equal(0,inventory.getAvailableQty("missing"),"missing inventory is zero");
        try(Statement statement=con.createStatement()){
            statement.executeUpdate("INSERT INTO tblWarehouse6 VALUES(3,'W03','Test','Test','ACTIVE')");
            statement.executeUpdate("INSERT INTO tblInventory6 VALUES(20,4,1,3,2)");
        }
        equal(12,inventory.getAvailableQty("AT-BAS-W-L"),"sum across active warehouses");
        try(Statement statement=con.createStatement()){
            statement.executeUpdate("DELETE FROM tblInventory6 WHERE id=20");statement.executeUpdate("DELETE FROM tblWarehouse6 WHERE id=3");
        }
        Account6 wrongRole=new Account6();wrongRole.setId(1L);wrongRole.setRole("MANAGER");wrongRole.setStatus("ACTIVE");
        equal(3,new ProductDAO6(con,wrongRole).searchByName("áo",1).size(),"public DAO does not impose account role");
        equal(3,new ProductDAO6(con,null).searchByName("áo",1).size(),"anonymous DAO search allowed");
        check(new CustomerHomeUI6(null)!=null,"anonymous home View allowed");
        for(String invalid:new String[]{""," \t ",new String(new char[101]).replace('\0','a')}){
            boolean rejected=false;try{products.searchByName(invalid,1);}catch(IllegalArgumentException e){rejected=true;}check(rejected,"invalid keyword rejected");
        }
        equal("áo",ProductDAO6.normalizeKeyword("\u00a0áo\u00a0"),"Unicode edge spaces");
        addProduct(con,201,"SPECIAL01","Sản phẩm 100%!",true);
        addProduct(con,202,"SPECIAL02","Mã_A",true);
        addProduct(con,203,"SPECIAL03","O'Reilly",true);
        equal(1,products.searchByName("%",1).size(),"percent searched literally");
        equal(1,products.searchByName("_",1).size(),"underscore searched literally");
        equal(1,products.searchByName("!",1).size(),"escape character searched literally");
        equal(1,products.searchByName("O'Reilly",1).size(),"apostrophe searched literally");
        equal(0,products.searchByName("' OR 1=1 --",1).size(),"SQL injection does not return all rows");
        equal(0L,products.getTotalResultCount(),"metadata reset after empty results");
        for(int i=1;i<=12;i++)addProduct(con,300+i,String.format(Locale.ROOT,"PG%02d",i),"Phân trang "+i,true);
        equal(10,products.searchByName("Phân trang",1).size(),"first page limit");
        equal(2,products.searchByName("Phân trang",9999).size(),"obsolete page clamped to last page");
        ProductSearchUI6 search=new ProductSearchUI6(con,customer);search.setInKeyword("áo thun");search.showResults();
        equal(3,search.getOutProductList().size(),"search View results");
        equal(0,search.getMinimumPrice("P01").compareTo(new java.math.BigDecimal("120000")),"minimum active variant price");
        check(search.isInStock("P01"),"in-stock derived from active variants");
        search.setInKeyword("Phân trang");search.setInPage(9999);search.showResults();equal(2,search.getPage(),"View displays clamped page");
        search.setInKeyword("   ");search.showResults();check(search.isMessageVisible()&&search.getOutProductList().isEmpty(),"invalid View clears stale results");
        ProductDetailUI6 detail=new ProductDetailUI6(con,customer);detail.setProductId("P01");detail.showProduct();
        equal(3,detail.getOutVariantList().size(),"product View variant list");equal(8,detail.getAvailableQty("AT-BAS-W-L"),"product View current stock");
        VariantDetailUI6 variant=new VariantDetailUI6(con,customer);variant.setProductId("P01");variant.setVariantId("AT-BAS-W-L");variant.showDetail();
        equal(8,variant.getAvailableQty(),"variant View stock");
        equal("P01",variant.getOutVariantDetail().getProduct().getProductId(),"variant View image parent");
        variant.setProductId("P02");variant.showDetail();check(variant.getOutVariantDetail()==null,"tampered parent rejected");
        variant.setProductId("P01");
        try(Statement statement=con.createStatement()){statement.executeUpdate("UPDATE tblProductVariant6 SET currentPrice=127000 WHERE variantId='AT-BAS-W-L'");statement.executeUpdate("UPDATE tblInventory6 SET onHandQuantity=0 WHERE warehouseRefId=1 AND variantRefId=2");}
        variant.showDetail();equal(0,variant.getAvailableQty(),"variant reads changed stock");
        equal(0,variant.getOutVariantDetail().getCurrentPrice().compareTo(new java.math.BigDecimal("127000")),"variant reads changed price");
        try(Statement statement=con.createStatement()){statement.executeUpdate("UPDATE tblProductVariant6 SET currentPrice=125000 WHERE variantId='AT-BAS-W-L'");statement.executeUpdate("UPDATE tblInventory6 SET onHandQuantity=8 WHERE warehouseRefId=1 AND variantRefId=2");}
        equal("&lt;script&gt;&quot;&#39;&amp;",ViewSupport6.h("<script>\"'&"),"HTML escaping");
        equal("/ShopMan/assets/images/no-image.svg",ViewSupport6.imageUrl("/ShopMan","javascript:alert(1)"),"unsafe image URL rejected");
        equal("/ShopMan/assets/images/no-image.svg",ViewSupport6.imageUrl("/ShopMan","//evil.example/a.png"),"protocol-relative image rejected");
        String before=fingerprint(con);
        search.setInKeyword("áo thun");search.setInPage(1);search.showResults();detail.showProduct();variant.showDetail();
        equal(before,fingerprint(con),"DAO and View browsing preserves all table data");
    }
    private static void addProduct(Connection con,int id,String code,String name,boolean active)throws SQLException{
        try(PreparedStatement statement=con.prepareStatement("INSERT INTO tblProduct6 VALUES(?,?,?,?,?,?,1)")){
            statement.setInt(1,id);statement.setString(2,code);statement.setString(3,name);statement.setString(4,"Test");
            statement.setString(5,"assets/images/no-image.svg");statement.setString(6,active?"ACTIVE":"INACTIVE");statement.executeUpdate();
        }
        try(PreparedStatement statement=con.prepareStatement("INSERT INTO tblProductVariant6 VALUES(?,?,?,'Cái',100000,'ACTIVE',?)")){
            statement.setInt(1,id);statement.setString(2,"V-"+code);statement.setString(3,"Test");statement.setInt(4,id);statement.executeUpdate();
        }
    }
    private static String fingerprint(Connection con)throws Exception{
        MessageDigest digest=MessageDigest.getInstance("SHA-256");
        for(String table:new String[]{"tblAccount6","tblCustomer6","tblCategory6","tblProduct6","tblProductVariant6","tblWarehouse6","tblInventory6"}){
            try(Statement statement=con.createStatement();ResultSet rs=statement.executeQuery("SELECT * FROM "+table+" ORDER BY id")){
                int columns=rs.getMetaData().getColumnCount();while(rs.next())for(int i=1;i<=columns;i++)digest.update((String.valueOf(rs.getObject(i))+"\u0000").getBytes(StandardCharsets.UTF_8));
            }
        }
        return Base64.getEncoder().encodeToString(digest.digest());
    }
    private static int port;
    private static void startTomcat(Path home,String jdbcUrl)throws Exception{
        Path base=projectRoot.resolve(".verification/tomcat-"+UUID.randomUUID().toString().replace("-",""));
        for(String directory:new String[]{"conf","logs","temp","webapps","work"})Files.createDirectories(base.resolve(directory));
        try(java.util.stream.Stream<Path> paths=Files.walk(home.resolve("conf"))){
            for(Iterator<Path> iterator=paths.iterator();iterator.hasNext();){Path source=iterator.next();Path target=base.resolve("conf").resolve(home.resolve("conf").relativize(source));if(Files.isDirectory(source))Files.createDirectories(target);else Files.copy(source,target,StandardCopyOption.REPLACE_EXISTING);}
        }
        try(ServerSocket socket=new ServerSocket(0,0,InetAddress.getByName("127.0.0.1"))){port=socket.getLocalPort();}
        String server="<Server port=\"-1\"><Service name=\"Catalina\"><Connector address=\"127.0.0.1\" port=\""+port+"\" protocol=\"HTTP/1.1\" URIEncoding=\"UTF-8\"/><Engine name=\"Catalina\" defaultHost=\"localhost\"><Host name=\"localhost\" appBase=\"webapps\" unpackWARs=\"true\" autoDeploy=\"false\" deployOnStartup=\"true\"/></Engine></Service></Server>";
        Files.write(base.resolve("conf/server.xml"),server.getBytes(StandardCharsets.UTF_8));
        Files.copy(projectRoot.resolve("dist/ShopMan.war"),base.resolve("webapps/ShopMan.war"));
        if("1".equals(System.getenv("SHOPMAN_TEST_INTELLIJ"))){
            Process setup=new ProcessBuilder("powershell.exe","-NoProfile","-ExecutionPolicy","Bypass","-File",
                    projectRoot.resolve("scripts/prepare-intellij.ps1").toString(),"-TomcatHome",home.toString(),
                    "-RuntimeBase",base.toString(),"-HttpPort",Integer.toString(port),"-SkipBuild")
                    .redirectErrorStream(true).redirectOutput(projectRoot.resolve(".verification/intellij-prepare-test.log").toFile()).start();
            if(!setup.waitFor(30,TimeUnit.SECONDS)||setup.exitValue()!=0)throw new IllegalStateException("IntelliJ preparation failed.");
        }
        String java=Paths.get(System.getProperty("java.home"),"bin","java.exe").toString();
        String classpath=home.resolve("bin/bootstrap.jar")+File.pathSeparator+home.resolve("bin/tomcat-juli.jar");
        if("1".equals(System.getenv("SHOPMAN_TEST_INTELLIJ"))){
            classpath=System.getProperty("java.class.path")+File.pathSeparator+classpath;
        }
        ProcessBuilder builder=new ProcessBuilder(java,"-Dcatalina.home="+home,"-Dcatalina.base="+base,
                "-Djava.util.logging.config.file="+base.resolve("conf/logging.properties"),
                "-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager","-cp",classpath,"org.apache.catalina.startup.Bootstrap","start");
        builder.environment().put("SHOPMAN_JDBC_URL",jdbcUrl);builder.environment().put("SHOPMAN_JDBC_USER",username);builder.environment().put("SHOPMAN_JDBC_PASSWORD",password);
        builder.redirectErrorStream(true);builder.redirectOutput(projectRoot.resolve(".verification/tomcat-test.log").toFile());
        tomcat=builder.start();
        long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(45);
        while(System.nanoTime()<deadline){
            if(!tomcat.isAlive())throw new IllegalStateException("Private Tomcat stopped; see .verification/tomcat-test.log");
            try{if(http("GET","/khachhang/CustomerHomeUI6.jsp",null).status==200)return;}catch(IOException ignored){ }
            Thread.sleep(250);
        }
        throw new IllegalStateException("Tomcat did not become ready; see .verification/tomcat-test.log");
    }
    private static void verifyHttp()throws Exception{
        CookieManager cookies=new CookieManager(null,CookiePolicy.ACCEPT_ALL);CookieHandler.setDefault(cookies);
        for(String page:new String[]{"CustomerHomeUI6.jsp","ProductSearchUI6.jsp","ProductDetailUI6.jsp?productId=P01","VariantDetailUI6.jsp?productId=P01&variantId=AT-BAS-W-L"})
            equal(200,http("GET","/khachhang/"+page,null).status,"anonymous page accessible: "+page);
        Reply entry=http("GET","/",null);
        equal(302,entry.status,"root redirects to the public home");
        check(entry.location!=null&&entry.location.endsWith("/khachhang/CustomerHomeUI6.jsp"),"root does not redirect to login");
        Reply home=http("GET","/khachhang/CustomerHomeUI6.jsp",null);equal(200,home.status,"home JSP compiles");
        check(home.body.contains("không cần đăng nhập"),"public home message");
        check(!home.body.contains("csrfToken6")&&!home.body.contains("Logout6.jsp"),"public home has no authentication controls");
        check(!home.body.contains("pbkdf2"),"hash absent from HTML");
        equal(302,http("GET","/khachhang/CustomerHomeUI6.jsp?subSearchProduct=1",null).status,"openSearch navigation");
        Reply search=http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q("áo thun"),null);equal(200,search.status,"search JSP compiles");
        for(String name:new String[]{"Áo thun Cơ bản","Áo thun Thể thao","Áo thun Trẻ em","3 kết quả, trang 1/1"})check(search.body.contains(name),"search HTML: "+name);
        check(!search.body.contains("P99"),"inactive product not rendered");
        check(search.body.contains("120.000"),"minimum price shown");
        Reply detail=http("GET","/khachhang/ProductDetailUI6.jsp?productId=P01&keyword="+q("áo thun")+"&page=1",null);equal(200,detail.status,"product JSP compiles");
        check(detail.body.contains("AT-BAS-W-L")&&!detail.body.contains("AT-BAS-OLD"),"active variants rendered");
        check(detail.body.contains("keyword="+q("áo thun")),"back link preserves keyword");
        Reply variant=http("GET","/khachhang/VariantDetailUI6.jsp?productId=P01&variantId=AT-BAS-W-L&keyword="+q("áo thun")+"&page=1",null);equal(200,variant.status,"variant JSP compiles");
        check(variant.body.contains("125.000")&&variant.body.contains("Tồn khả dụng: 8"),"variant price and stock rendered");
        equal(404,http("GET","/khachhang/VariantDetailUI6.jsp?productId=P02&variantId=AT-BAS-W-L",null).status,"wrong parent rejected over HTTP");
        equal(404,http("GET","/khachhang/ProductDetailUI6.jsp?productId=P99",null).status,"inactive product rejected over HTTP");
        check(http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q("   "),null).body.contains("Vui lòng nhập tên mặt hàng"),"empty keyword form error");
        check(http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q(new String(new char[101]).replace('\0','a')),null).body.contains("100 ký tự"),"long keyword form error");
        Reply escaped=http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q("<script>alert(1)</script>"),null);check(!escaped.body.contains("<script>")&&escaped.body.contains("&lt;script&gt;"),"search HTML escapes input");
        check(http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q("Phân trang")+"&page=9999",null).body.contains("trang 1/2"),"new keyword resets page");
        check(http("GET","/khachhang/ProductSearchUI6.jsp?keyword="+q("Phân trang")+"&page=9999",null).body.contains("trang 2/2"),"pagination URL clamped");
        equal(200,http("GET","/assets/images/product-P01.svg",null).status,"product image packaged");
        equal(200,http("GET","/assets/images/no-image.svg",null).status,"fallback image packaged");

        check(cookies.getCookieStore().getCookies().isEmpty(),"entire public flow works without a session cookie");
        CookieHandler.setDefault(null);
        Reply stateless=http("GET","/khachhang/ProductSearchUI6.jsp?inKeyword="+q("áo thun"),null);
        check(stateless.status==200&&stateless.body.contains("3 kết quả, trang 1/1"),"search also works when cookies are disabled");
        CookieHandler.setDefault(cookies);
        // Optional account pages do not gate the catalog, before or after logout.
        Reply login=http("GET","/thanhvien/Login6.jsp",null);equal(200,login.status,"optional login JSP compiles");
        String token=token(login.body),oldSession=cookies.getCookieStore().getCookies().toString();
        Reply wrong=http("POST","/thanhvien/Login6.jsp","csrfToken6="+q(token)+"&username=kh01&password=wrong");check(wrong.body.contains("không đúng"),"optional wrong credentials message");
        Reply accepted=http("POST","/thanhvien/Login6.jsp","csrfToken6="+q(token)+"&username=kh01&password="+q("Kh01@123"));equal(302,accepted.status,"optional login redirect");
        check(!oldSession.equals(cookies.getCookieStore().getCookies().toString()),"optional login rotates session");
        Reply accountPage=http("GET","/thanhvien/Login6.jsp",null);
        equal(200,accountPage.status,"optional account page accessible independently of catalog");
        String logoutToken=token(accountPage.body);
        equal(405,http("GET","/thanhvien/Logout6.jsp",null).status,"optional logout requires POST");
        equal(403,http("POST","/thanhvien/Logout6.jsp","csrfToken6=wrong").status,"optional logout CSRF checked");
        equal(302,http("POST","/thanhvien/Logout6.jsp","csrfToken6="+q(logoutToken)).status,"optional logout succeeds");
        equal(200,http("GET","/khachhang/ProductSearchUI6.jsp",null).status,"logout does not block public search");
        cookies.getCookieStore().removeAll();
        equal(200,http("GET","/khachhang/ProductSearchUI6.jsp",null).status,"catalog remains public after clearing login cookies");
    }
    private static String token(String html){Matcher m=Pattern.compile("name=\"csrfToken6\" value=\"([^\"]+)\"").matcher(html);if(!m.find())throw new AssertionError("CSRF token missing");return m.group(1);}
    private static String q(String value)throws Exception{return URLEncoder.encode(value,"UTF-8");}
    private static Reply http(String method,String path,String form)throws IOException{
        HttpURLConnection connection=(HttpURLConnection)new URL("http://127.0.0.1:"+port+"/ShopMan"+path).openConnection();
        connection.setInstanceFollowRedirects(false);connection.setConnectTimeout(3000);connection.setReadTimeout(15000);connection.setRequestMethod(method);
        if(form!=null){connection.setDoOutput(true);connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");try(OutputStream output=connection.getOutputStream()){output.write(form.getBytes(StandardCharsets.UTF_8));}}
        int status=connection.getResponseCode();InputStream input=status>=400?connection.getErrorStream():connection.getInputStream();ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        if(input!=null)try(InputStream stream=input){byte[] buffer=new byte[4096];for(int read;(read=stream.read(buffer))!=-1;)bytes.write(buffer,0,read);}
        Reply reply=new Reply(status,new String(bytes.toByteArray(),StandardCharsets.UTF_8),connection.getHeaderField("Location"));connection.disconnect();return reply;
    }
    private static final class Reply{final int status;final String body;final String location;Reply(int status,String body,String location){this.status=status;this.body=body;this.location=location;}}
    private static void check(boolean value,String label){if(!value)throw new AssertionError(label);checks++;}
    private static void equal(Object expected,Object actual,String label){check(Objects.equals(expected,actual),label+" expected="+expected+" actual="+actual);}
}
