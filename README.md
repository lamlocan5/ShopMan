# ShopMan – Mô đun 1 tìm kiếm mặt hàng

**Chạy bằng IntelliJ:** xem [HUONG_DAN_INTELLIJ.md](HUONG_DAN_INTELLIJ.md). Đã có cấu hình Run `ShopMan - IntelliJ`, WAR artifact và script chuẩn bị Tomcat riêng. URL: `http://localhost:8081/ShopMan/`.

Dự án làm theo `34B22DCCN476_final.docx`, các mục 3.2.1, 3.2.3, 3.2.4 và 4.3.1–4.3.3.
Chỉ có ba phần **View – DAO – Model**, không có Controller, Servlet tự viết, framework MVC hoặc mô đun 2.
Phạm vi: tìm theo tên, phân trang, xem mặt hàng, xem biến thể, giá bán hiện tại và tồn khả dụng tại các kho hoạt động. Giao diện JSP dùng HTML cơ bản, không CSS. Cấu hình kết nối cá nhân đặt trong `src/shopman.properties`, được Git bỏ qua; bản mẫu cấu hình có sẵn trong repository.

## 1. Cấu trúc dự án

```text
ShopMan/
├── src/
│   ├── model/                   Các thực thể đúng tên và thuộc tính trong final
│   ├── dao/                     DAO6 và các DAO dùng JDBC
│   ├── view/
│   │   ├── ViewSupport6.java     Escape HTML, URL, định dạng tiền, ảnh thay thế
│   │   └── khachhang/           Bốn lớp View của mô đun 1
│   └── shopman.properties.example
├── WebContent/
│   ├── thanhvien/               Login6.jsp, Logout6.jsp
│   ├── khachhang/               Bốn JSP cùng tên lớp View
│   ├── assets/images/           Ảnh mẫu và ảnh thay thế
│   └── WEB-INF/
│       ├── lib/                 MySQL Connector/J
│       ├── jspf/request6.jspf    Chuẩn bị yêu cầu tra cứu công khai
│       └── web.xml
├── database/                    Schema và dữ liệu mẫu
├── test/unittest/               Kiểm thử MySQL và JSP trên Tomcat thật
├── scripts/                     Biên dịch, đóng WAR và chạy kiểm thử
└── dist/ShopMan.war             WAR được tạo khi build
```

Các JSP nhận dữ liệu biểu mẫu, tạo đối tượng View cho từng yêu cầu, gọi phương thức View và hiển thị kết quả. View gọi DAO. Chỉ DAO chứa câu SQL. Model là các JavaBean thực thể, không chứa SQL. Không dùng trạng thái View hoặc DAO dùng chung giữa các request.

Các nút và liên kết `subSearchProduct`, `subSearch`, `subSelectProduct`, `subSelectVariant`, `subBack`, `subClose` được thể hiện bằng thành phần HTML trong JSP; không dùng Button của Swing/AWT trong ứng dụng web.
Giao diện dùng HTML cơ bản, không CSS, không thư viện giao diện. JavaScript duy nhất là xử lý ảnh lỗi để chuyển sang ảnh thay thế.

## 2. Đối chiếu tên với báo cáo

| View | Phương thức giữ đúng tên | JSP |
|---|---|---|
| CustomerHomeUI6 | openSearch() | khachhang/CustomerHomeUI6.jsp |
| ProductSearchUI6 | showResults(), showMessage() | khachhang/ProductSearchUI6.jsp |
| ProductDetailUI6 | showProduct() | khachhang/ProductDetailUI6.jsp |
| VariantDetailUI6 | showDetail() | khachhang/VariantDetailUI6.jsp |

| DAO | Phương thức của mô đun 1 |
|---|---|
| ProductDAO6 | searchByName(String keyword, int page): List<Product6>; findActiveById(String productId): Product6; getTotalResultCount(): long |
| CategoryDAO6 | findById(String categoryId): Category6 |
| ProductVariantDAO6 | findActiveByProduct(String productId): List<ProductVariant6>; findActiveById(String variantId): ProductVariant6 |
| InventoryDAO6 | getAvailableQty(String variantId): int |

`DAO6` là lớp trừu tượng, chứa `con: Connection` và ngữ cảnh tùy chọn `sessionAccount: Account6 [0..1]`; constructor các DAO nhận `(Connection con, Account6 sessionAccount)`. Các DAO tra cứu của mô đun 1 chấp nhận `sessionAccount = null`, chỉ kiểm tra kết nối và không kiểm tra đăng nhập hay vai trò.
Bốn View dùng các thuộc tính `in...`, `out...`, `keyword`, `page`, `productId`, `variantId`, `sessionAccount` theo thiết kế. Các map hiển thị giá từ, tồn và các getter là dữ liệu tính để hiển thị, không thêm cột vào Model hay CSDL.

Model gồm `Account6`, `Customer6`, `Category6`, `Product6`, `ProductVariant6`, `Warehouse6`, `Inventory6`. Các quan hệ đối tượng như `Product6.category`, `Product6.variants`, `ProductVariant6.product`, `Inventory6.warehouse`, `Inventory6.variant` được giữ đúng thiết kế.

`AccountDAO6.checkLogin()`, `PasswordHash6`, `Login6.jsp`, `Logout6.jsp` được giữ riêng làm hỗ trợ tài khoản tùy chọn. Chúng không được gọi bởi luồng tra cứu công khai và không phải tiền điều kiện của mô đun 1. Không triển khai đăng ký, đổi mật khẩu hay các nghiệp vụ nhân viên.

## 3. Tạo CSDL và cấu hình

Máy đã kiểm thử có JDK 21, Tomcat 9.0.112 và MySQL 8. Mã Java biên dịch với `--release 8`; dự án Eclipse đặt Java facet 1.8 và Dynamic Web Module 3.1, dùng API `javax` của Tomcat 9.

1. Trong MySQL Workbench, chạy `database/module1-schema.sql`.
2. Chạy `database/module1-seed.sql` một lần trên các bảng mô đun 1 còn trống.
3. Sao chép `src/shopman.properties.example` thành `src/shopman.properties`.
4. Sửa URL, user và password theo MySQL của bạn; sau đó build hoặc publish lại ứng dụng.

```properties
shopman.jdbc.url=jdbc:mysql://localhost:3306/ShopMan?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
shopman.jdbc.user=root
shopman.jdbc.password=mat_khau_MySQL_cua_ban
```

Có thể dùng biến môi trường `SHOPMAN_JDBC_URL`, `SHOPMAN_JDBC_USER`, `SHOPMAN_JDBC_PASSWORD` thay cho file. Thứ tự ưu tiên: Java system property → biến môi trường → file cấu hình → giá trị mặc định. Mật khẩu MySQL thực tế không có trong mã hoặc WAR bàn giao.

Các giá trị lưu thống nhất:

- `Account6.role = CUSTOMER`: Khách hàng.
- `status = ACTIVE`: tài khoản/loại/kho đang hoạt động.
- `businessStatus = ACTIVE`: mặt hàng hoặc biến thể đang kinh doanh.
- `INACTIVE`: trạng thái không hoạt động tương ứng.

Đơn vị tiền tệ dùng `BigDecimal` và `DECIMAL(18,2)`. `id` là khóa nội bộ BIGINT; `productId`, `variantId`, `categoryId` là mã nghiệp vụ String, không dùng lẫn với khóa nội bộ. Chỉ tạo bảy bảng cần cho mô đun 1 và đăng nhập.

**Tra cứu mô đun 1 không cần đăng nhập.** Tài khoản dữ liệu mẫu **kh01 / Kh01@123** chỉ dùng cho trang đăng nhập tùy chọn, không cần dùng để tìm kiếm. CSDL lưu `passwordHash` dạng PBKDF2, không lưu mật khẩu rõ. Đối tượng tài khoản trong session không chứa hash.

## 4. Mở và chạy trong Eclipse

1. File → Import → General → Existing Projects into Workspace; chọn thư mục `ShopMan`.
2. Properties → Targeted Runtimes: chọn Tomcat 9 đã cấu hình trên máy. Đặt JRE của server là JDK đã cài.
3. Kiểm tra Project Facets: Java 1.8; Dynamic Web Module 3.1. Source là `src`; thư mục web là `WebContent`.
4. Sau khi tạo CSDL và sửa cấu hình, Run As → Run on Server.
5. Mở `http://localhost:8080/ShopMan/` hoặc `/ShopMan/khachhang/CustomerHomeUI6.jsp` theo cổng server của bạn.

Nếu chỉ cần WAR:

```powershell
.\scripts\build-module1.ps1
```

Script tự tìm JDK từ `java.home`, không yêu cầu Maven. Có thể truyền `-JdkHome 'C:\duong_dan_JDK'`. WAR nằm ở `dist/ShopMan.war`; có thể triển khai WAR này lên Tomcat 9 sau khi cấu hình biến môi trường cho kết nối MySQL.

## 5. Chức năng đã làm

- Cả bốn màn hình và các DAO tra cứu được dùng công khai, không cần đăng nhập, vai trò hay cookie phiên.
- Nhập một phần tên, bỏ khoảng trắng đầu/cuối, kiểm tra không rỗng và tối đa 100 ký tự.
- Tìm mặt hàng đang kinh doanh; dấu `%`, `_`, `!` và dấu nháy được tìm như ký tự thông thường.
- Kết quả có mã, tên, ảnh, loại, giá từ, tình trạng còn hàng và phân trang. Kích thước trang là 10; trang cũ không còn tồn tại được đưa về trang hợp lệ gần nhất. Từ khóa mới về trang 1.
- Giá từ lấy giá nhỏ nhất trong các biến thể đang kinh doanh. Còn hàng khi ít nhất một biến thể này có tồn khả dụng lớn hơn 0.
- Xem chi tiết mặt hàng và bảng biến thể; đọc lại giá và tồn ở mỗi lần mở.
- Xem chi tiết biến thể, kiểm tra thuộc đúng mặt hàng được chọn; ảnh lấy từ mặt hàng chứa biến thể.
- Tồn khả dụng chỉ cộng tồn của các kho đang hoạt động; không có bản ghi tồn được hiểu là 0.
- Quay lại giữ từ khóa/trang; Đóng trở về trang chính.
- Có thông báo dữ liệu rỗng, từ khóa không hợp lệ, dữ liệu ngừng kinh doanh, lỗi tải và liên kết Thử lại. Ảnh lỗi có ảnh thay thế.
- Không có lời gọi ghi hoặc trừ tồn trong luồng tra cứu. Mỗi lần tải dùng một Connection và ảnh chụp dữ liệu nhất quán của giao dịch chỉ đọc.

Không viết lớp xuất kho, hóa đơn, bàn giao, DAO của mô đun 2 hoặc các phương thức ghi tồn dùng cho mô đun 2.

## 6. Kiểm thử

Đã chạy thành công **96 kiểm tra** trên MySQL và một Tomcat cục bộ độc lập, bao gồm biên dịch JSP thật và luồng HTTP truy cập ẩn danh → tìm kiếm → chi tiết mặt hàng → chi tiết biến thể; vẫn truy cập được khi tắt cookie hoặc đăng xuất.
Có kiểm tra phân trang, ký tự đặc biệt, SQL injection, escape HTML, truy cập công khai không phiên đăng nhập, trạng thái kinh doanh, kho ngừng hoạt động, biến thể sai mặt hàng, giá/tồn thay đổi và dữ liệu không bị sửa sau tra cứu.

Để chạy lại, cung cấp tài khoản MySQL có quyền tạo/xóa CSDL kiểm thử riêng:

```powershell
$env:SHOPMAN_TEST_JDBC_URL='jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC'
$env:SHOPMAN_TEST_JDBC_USER='root'
$env:SHOPMAN_TEST_JDBC_PASSWORD='mat_khau_MySQL_cua_ban'
.\scripts\test-module1.ps1 -TomcatHome 'C:\duong_dan\apache-tomcat-9.0.112'
```

Bộ kiểm thử tạo CSDL tên ngẫu nhiên `shopman_m1_test_...`, tự xóa CSDL đó và dừng Tomcat thử nghiệm khi kết thúc. Không dùng hay sửa dữ liệu FootballLeagueManagement hoặc CSDL ShopMan thật.

## Sơ đồ triển khai đối chiếu mã nguồn

Xem [DEPLOYMENT_MODULE1.md](DEPLOYMENT_MODULE1.md) và `docs/deployment/` để biết node, phần mềm, thư mục, thư viện, artifact WAR và vị trí bằng chứng trong code. Sơ đồ mô tả cấu hình chạy thử cục bộ hiện tại.
