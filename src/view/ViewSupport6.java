package view;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.Locale;

/** Rendering helpers only; no SQL and no servlet/controller state. */
public final class ViewSupport6 {
    private ViewSupport6() { }
    public static String h(Object value) {
        if(value==null)return "";
        return value.toString().replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
    public static String q(String value) {
        try{return URLEncoder.encode(value==null?"":value,"UTF-8");}
        catch(UnsupportedEncodingException e){throw new IllegalStateException(e);}
    }
    public static String money(BigDecimal value) {
        return value==null?"Chưa có giá":NumberFormat.getIntegerInstance(new Locale("vi","VN")).format(value);
    }
    public static int page(String value) {
        if(value==null)return 1;
        try{return Math.max(1,Integer.parseInt(value));}catch(NumberFormatException e){return 1;}
    }
    public static String imageUrl(String contextPath,String value) {
        String fallback=contextPath+"/assets/images/no-image.svg";
        if(value==null||value.trim().isEmpty())return fallback;
        try{
            URI uri=new URI(value);
            String scheme=uri.getScheme();
            if(scheme!=null)return ("https".equalsIgnoreCase(scheme)||"http".equalsIgnoreCase(scheme))
                    && uri.getHost()!=null && uri.getUserInfo()==null?uri.toASCIIString():fallback;
            if(value.startsWith("/")||value.contains("..")||value.contains("\\")||uri.getAuthority()!=null)return fallback;
            return contextPath+"/"+uri.toASCIIString();
        }catch(Exception e){return fallback;}
    }
    public static String newToken() {
        byte[] bytes=new byte[24];new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static boolean validToken(String expected,String submitted) {
        return expected!=null&&submitted!=null&&MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),submitted.getBytes(StandardCharsets.UTF_8));
    }
}
