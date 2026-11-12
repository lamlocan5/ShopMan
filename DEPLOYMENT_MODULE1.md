# Triển khai ShopMan mô đun 1 theo mã nguồn

Bản đối chiếu này mô tả cấu hình chạy cục bộ đã có trong project. Trình duyệt, môi trường phát triển, JVM/Tomcat và MySQL là bốn node môi trường **cùng nằm trên một máy Windows**. Ba tầng trình duyệt–ứng dụng–dữ liệu là phân chia logic. Connector hiện gắn `127.0.0.1`, HTTP 8081; kết nối MySQL dùng JDBC/TCP 3306. Không mô tả HTTPS hoặc máy chủ từ xa khi cấu hình hiện tại chưa thiết lập chúng.

Mô đun 1 tra cứu công khai, không cần đăng nhập. Kiến trúc code là View–DAO–Model.

Xem [sơ đồ SVG](docs/deployment/Hinh_4_12_deployment_module1.svg), [ảnh PNG](docs/deployment/Hinh_4_12_deployment_module1.png) và [nguồn PlantUML](docs/deployment/Hinh_4_12_deployment_module1.puml).

## Các thư mục dùng trong sơ đồ

| Ký hiệu | Đường dẫn trên máy hiện tại |
| --- | --- |
| P | `C:/Users/Admin/Desktop/PTIT/PTTK-HTTT-NgocDTB/Bai8_final/ShopMan` |
| CATALINA_HOME | `C:/Users/Admin/Downloads/apache-tomcat-9.0.112` |
| CATALINA_BASE | `P/.runtime/intellij-tomcat` |
| MYSQL_HOME | `C:/Program Files/MySQL/MySQL Server 8.0` |
| MYSQL_DATA | `C:/ProgramData/MySQL/MySQL Server 8.0/Data` |

## Cài gì ở node nào

| Node | Phần mềm cần có | Mã, artifact và thư mục ở node |
| --- | --- | --- |
| N1 Trình duyệt | Chrome hoặc Edge | Mở `http://localhost:8081/ShopMan/`. Nhận HTML và ảnh do Tomcat trả về. |
| N2 Phát triển/đóng gói | IntelliJ IDEA và JDK 21; project dùng SDK `ms-21`, language level 8 | `P/src`, `P/WebContent`, `.idea`, `.run`, `ShopMan.iml`, `scripts`, `database`, `test`. Biên dịch `javac --release 8`; script tạo `P/dist/ShopMan.war`. |
| N3 Ứng dụng | JDK 21 + Tomcat 9.0.112 | Bộ cài ở CATALINA_HOME. Cấu hình/nhật ký/webapp riêng ở CATALINA_BASE. Đặt WAR tại `webapps/ShopMan.war`, Tomcat giải nén vào `webapps/ShopMan/`, JSP biên dịch ở `work/`. |
| N4 Dữ liệu | MySQL Server 8.0; công cụ chạy SQL như MySQL Workbench hoặc mysql CLI | Dịch vụ `MySQL80`, cổng 3306, schema `ShopMan`. Chạy hai script trong `P/database`; dữ liệu thật do MySQL lưu ở MYSQL_DATA. Workbench/CLI là công cụ quản trị, không phải phụ thuộc chạy của ứng dụng. |

JDK 21 có thể dùng chung cho IDE và Tomcat trên máy hiện tại. IntelliJ phục vụ phát triển, đóng gói và khởi chạy; ứng dụng chạy trong JVM/Tomcat. Cấu hình Application dùng `org.apache.catalina.startup.Bootstrap` có sẵn trong Tomcat.

## Mã nguồn nằm đâu sau khi triển khai

| Nguồn | Trong WAR / nơi sử dụng |
| --- | --- |
| `src/model/*.java` | `WEB-INF/classes/model/*.class` |
| `src/dao/*.java` | `WEB-INF/classes/dao/*.class` |
| `src/view/khachhang/*.java` | `WEB-INF/classes/view/khachhang/*.class` |
| `src/view/ViewSupport6.java` | `WEB-INF/classes/view/ViewSupport6.class` |
| `WebContent/index.jsp` | `/index.jsp`, chuyển tới trang tra cứu công khai |
| `WebContent/khachhang/*.jsp` | `/khachhang/*.jsp`, bốn màn hình của mô đun 1 |
| `WebContent/WEB-INF/jspf/request6.jspf` | `WEB-INF/jspf/request6.jspf`, UTF-8, kiểm tra GET, `sessionAccount=null` |
| `WebContent/WEB-INF/web.xml` | `WEB-INF/web.xml`, welcome page và cấu hình JSP UTF-8 |
| `WebContent/assets/images/*.svg` | `assets/images/*.svg`, ảnh mẫu và ảnh thay thế |
| `src/shopman.properties` sau khi tạo từ example | `WEB-INF/classes/shopman.properties`, cấu hình JDBC trên classpath |
| `WebContent/thanhvien/*.jsp` | `/thanhvien/*.jsp`, phần tài khoản tùy chọn, tách khỏi luồng tra cứu |
| `database/*.sql` | Chạy vào MySQL để tạo schema/dữ liệu; build không chép vào WAR |
| `.idea`, `.run`, `scripts`, `test`, `*.iml` | Phục vụ phát triển, đóng gói và kiểm thử; không nằm trong WAR |

### Danh sách lớp đối chiếu từ project

- `src/model`: `Account6`, `Category6`, `Customer6`, `Inventory6`, `Product6`, `ProductVariant6`, `Warehouse6`.
- `src/dao`: `AccountDAO6`, `CategoryDAO6`, `DAO6`, `InventoryDAO6`, `PasswordHash6`, `ProductDAO6`, `ProductVariantDAO6`.
- `src/view/khachhang`: `CustomerHomeUI6`, `ProductDetailUI6`, `ProductSearchUI6`, `VariantDetailUI6`.
- `src/view/ViewSupport6.java`: mã hỗ trợ escape, định dạng và tạo URL cho giao diện.

## Thư viện cụ thể và nơi đặt

| Thư viện | Nơi đặt | Đối chiếu và vai trò |
| --- | --- | --- |
| `mysql-connector-j-9.5.0.jar` | Nguồn: `WebContent/WEB-INF/lib`; chạy: `WEB-INF/lib` trong WAR | DAO6 tải `com.mysql.cj.jdbc.Driver`; driver JDBC kết nối MySQL. |
| `bootstrap.jar`, `tomcat-juli.jar` | `CATALINA_HOME/bin` | Thư viện `Tomcat 9 Bootstrap` trong IntelliJ; khởi động Tomcat từ cấu hình Application. |
| `servlet-api.jar`, `jsp-api.jar`, `el-api.jar` | `CATALINA_HOME/lib` | Thư viện `Tomcat 9 JSP API`, scope PROVIDED trong module; Tomcat cung cấp khi chạy. |
| `jasper.jar`, `jasper-el.jar`, `ecj-4.20.jar` | `CATALINA_HOME/lib`, kèm bộ cài được kiểm tra trên máy | Xử lý và biên dịch JSP. Giữ tại bộ cài Tomcat. |
| API `java.sql`, `java.util`, `java.math`, `java.security`, `javax.crypto` | JDK 21 | API chuẩn được code sử dụng; không có JAR ứng dụng riêng cho các API này. |

Không chép các JAR API/bộ máy Tomcat vào `WEB-INF/lib` của ứng dụng. WAR hiện có Connector/J trong thư mục này. Source `.example` không được tự đóng gói thành cấu hình thật; người chạy tạo `src/shopman.properties`, điền tài khoản MySQL rồi build lại, hoặc cấp biến môi trường `SHOPMAN_JDBC_URL`, `SHOPMAN_JDBC_USER`, `SHOPMAN_JDBC_PASSWORD`. Độ ưu tiên trong DAO6: Java system property → biến môi trường → file properties → mặc định. Sơ đồ và tài liệu không chứa mật khẩu thực.

## Dữ liệu và cách chạy

1. Khởi động MySQL80. Tạo schema/tables bằng `database/module1-schema.sql`; chạy seed một lần khi bảng mới còn trống.
2. Tạo cấu hình JDBC từ `src/shopman.properties.example` và điền thông tin kết nối.
3. Chạy `scripts/prepare-intellij.ps1`. Script build WAR, tạo `conf`, `logs`, `temp`, `webapps`, `work` ở CATALINA_BASE; đặt `server.xml` với HTTP/1.1, UTF-8 và cổng 8081.
4. Trong IntelliJ chạy **ShopMan - IntelliJ**. Before launch: Build + Build Artifacts `ShopMan:war`. Artifact xuất vào `CATALINA_BASE/webapps/ShopMan.war`.
5. Truy cập trang tra cứu. Dừng rồi chạy lại khi thay đổi mã hoặc properties để nhận bản build mới.

Các bảng tra cứu: `tblCategory6`, `tblProduct6`, `tblProductVariant6`, `tblWarehouse6`, `tblInventory6`. Hai bảng `tblAccount6`, `tblCustomer6` hỗ trợ tài khoản tùy chọn. Module 1 không ghi thay đổi dữ liệu khi tra cứu; không có code xử lý xuất kho, hóa đơn hoặc bàn giao trong WAR.

## Vị trí bằng chứng trong mã

- `src/dao/DAO6.java:28` — Đọc properties, driver JDBC và mở kết nối; phần setting() quy định độ ưu tiên cấu hình.
- `scripts/build-module1.ps1:20` — Biên dịch Java, copy WebContent/properties, đóng gói dist/ShopMan.war.
- `scripts/prepare-intellij.ps1:20` — HTTP 8081, CATALINA_BASE, webapps, giải nén WAR và triển khai lúc khởi động.
- `.run/ShopMan_IntelliJ.run.xml:3` — Main Bootstrap, tham số catalina.home/base và các bước trước khi chạy.
- `.idea/artifacts/ShopMan_war.xml:3` — Đích WAR, copy WebContent và module output vào WEB-INF/classes.
- `ShopMan.iml:10` — Phạm vi thư viện API Tomcat và Connector/J trong project.
- `.idea/libraries/Tomcat_9_Bootstrap.xml:1` — Hai JAR khởi chạy trong bộ cài Tomcat.
- `.idea/libraries/Tomcat_9_JSP_API.xml:1` — Ba JAR API do Tomcat cung cấp.
- `WebContent/WEB-INF/jspf/request6.jspf:7` — Chuẩn bị yêu cầu tra cứu công khai, không yêu cầu phiên đăng nhập.
- `database/module1-schema.sql:3` — Schema ShopMan và bảy bảng trong phạm vi gói code.

Đường dẫn MySQL/3306 được đối chiếu thêm từ dịch vụ MySQL80 và `C:/ProgramData/MySQL/MySQL Server 8.0/my.ini`; các JAR của Tomcat được kiểm tra trực tiếp tại CATALINA_HOME.
