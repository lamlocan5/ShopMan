# Chạy ShopMan mô đun 1 bằng IntelliJ IDEA

Dự án ở `C:\Users\Admin\Desktop\PTIT\PTTK-HTTT-NgocDTB\Bai8_final\ShopMan`.
Máy đã có IntelliJ IDEA 2025.2, JDK `ms-21` và Tomcat `C:\Users\Admin\Downloads\apache-tomcat-9.0.112`.

Ứng dụng vẫn chỉ có **View – DAO – Model**. Cấu hình Run khởi động Tomcat bằng lớp có sẵn `org.apache.catalina.startup.Bootstrap` của Tomcat; đây không phải Controller của ứng dụng. Không thêm mô đun 2.

## Bước 1 Mở đúng dự án

Trong IntelliJ chọn **File → Open**, chọn thư mục **ShopMan**, không chọn thư mục FootballLeagueManagement tham khảo. Nếu IntelliJ hỏi về tin cậy dự án vừa mở, chọn **Trust Project** khi bạn muốn chạy bộ mã này.

Đợi IntelliJ đọc cấu hình. Dự án đã có `ShopMan.iml`, `.idea` và cấu hình `.run/ShopMan_IntelliJ.run.xml`.

## Bước 2 Chọn JDK

Vào **File → Project Structure → Project**:

- Project SDK: chọn **ms-21**, là JDK 21 đã có trong IntelliJ trên máy bạn.
- Nếu SDK chưa hiện, chọn **Add SDK → JDK** rồi chọn JDK đã cài, chẳng hạn `C:\Program Files\Java\jdk-21`.
- Language level của mã nguồn là **8**; bạn vẫn chạy bằng JDK 21.

Trong **Modules → Sources**, `src` là Sources và `test` là Test Sources. Các thư mục build, dist, .runtime, .verification đã được loại khỏi source.

## Bước 3 Tạo dữ liệu MySQL

Mở MySQL Workbench, kết nối MySQL đang chạy trên máy:

1. Chạy `database/module1-schema.sql`.
2. Với các bảng mới, còn trống, chạy `database/module1-seed.sql` một lần.

Nếu đã nạp dữ liệu mẫu trước đó thì bỏ qua bước nạp seed lại. Không cần dùng CSDL FootballLeagueManagement.

Mô đun 1 được truy cập công khai; không cần tài khoản hoặc đăng nhập.

## Bước 4 Cấu hình tài khoản MySQL

Mở Terminal trong IntelliJ tại thư mục ShopMan. Chỉ tạo file cấu hình khi file chưa có:

```powershell
if (!(Test-Path .\src\shopman.properties)) {
    Copy-Item .\src\shopman.properties.example .\src\shopman.properties
}
```

Mở `src/shopman.properties`, sửa ba dòng theo MySQL của bạn:

```properties
shopman.jdbc.url=jdbc:mysql://localhost:3306/ShopMan?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
shopman.jdbc.user=root
shopman.jdbc.password=mat_khau_MySQL_cua_ban
```

`password` ở đây là mật khẩu kết nối MySQL, không phải mật khẩu đăng nhập kh01. Không cần đưa mật khẩu MySQL lên cuộc trò chuyện.

## Bước 5 Chuẩn bị Tomcat riêng cho dự án

Chạy lệnh này trong Terminal của IntelliJ:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\prepare-intellij.ps1
```

Script biên dịch, đóng gói WAR và tạo cấu hình chạy riêng tại `.runtime/intellij-tomcat`. Không chỉnh thư mục Tomcat đã cài. Thông báo cuối phải có:

```text
URL: http://localhost:8081/ShopMan/
```

Thư mục làm việc hiện tại trên máy đã được chuẩn bị; lệnh trên cũng dùng được sau khi giải nén lại mã nguồn.

## Bước 6 Chạy bằng nút Run

1. Trên thanh cấu hình chạy, chọn **ShopMan - IntelliJ**.
2. Bấm nút tam giác xanh **Run** hoặc **Shift+F10**.
3. Đợi console hiện thông báo khởi động server hoàn tất.
4. Mở **http://localhost:8081/ShopMan/**.
5. Trang chính tra cứu mở trực tiếp, **không yêu cầu đăng nhập**.
6. Nhấn **Tìm mặt hàng**, nhập **áo thun**. Kết quả mẫu có P01, P02, P03.
7. Chọn **P01**, rồi chọn **AT-BAS-W-L**: giá **125.000 đồng**, tồn khả dụng **8**.

Before launch đã có **Build** và **Build Artifacts → ShopMan:war**. Khi sửa Java hoặc cấu hình kết nối, dừng rồi Run lại để ứng dụng nhận bản mới.

Dừng ứng dụng bằng nút vuông đỏ **Stop** của cấu hình Run trong IntelliJ.

## Cách chạy bằng Terminal trong IntelliJ

Nếu chưa thấy cấu hình Run hoặc không dùng tính năng WAR artifact của IDE, chạy trực tiếp:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\run-intellij.ps1
```

Sau đó mở cùng URL **http://localhost:8081/ShopMan/**. Nhấn **Ctrl+C** trong Terminal đó để dừng. Chỉ chọn một cách chạy tại một thời điểm để tránh trùng cổng.

## Khi gặp lỗi

- **Không kết nối được CSDL**: kiểm tra MySQL đang chạy, schema ShopMan đã được tạo, user/password trong `src/shopman.properties` đúng. Sau khi sửa, dừng rồi chạy lại.
- **Access denied for user**: sửa mật khẩu MySQL ở file cấu hình; tài khoản web kh01 không phải tài khoản kết nối CSDL.
- **Port 8081 already in use**: dừng lần chạy ShopMan trước. Nếu cần cổng khác, dùng cách Terminal với `-HttpPort 8082` và truy cập cổng 8082.
- **Cannot find class Bootstrap**: kiểm tra hai đường dẫn jar trong **Project Structure → Libraries → Tomcat 9 Bootstrap** trỏ tới `bin/bootstrap.jar` và `bin/tomcat-juli.jar` của Tomcat 9 đã cài.
- **Project SDK is not defined**: làm lại Bước 2.
- **Run không hiện**: mở đúng thư mục ShopMan có `.idea` và `.run`; cũng có thể dùng cách Terminal ở trên.
- **Sai context path hoặc cổng**: URL của cấu hình mới là `/ShopMan/` trên **8081**, không phải đường dẫn FootballLeagueManagement hoặc cổng 8080.

## Đã kiểm thử

Đã chạy lại **96 kiểm tra** với MySQL và Tomcat thật, bao gồm script chuẩn bị và classpath dùng cho IntelliJ. Luồng truy cập ẩn danh, tìm kiếm, phân trang, xem mặt hàng và xem biến thể đều đạt; dữ liệu tra cứu không thay đổi. Kiểm thử dùng CSDL tạm đã tự xóa và Tomcat thử nghiệm đã được dừng.

Đã kiểm thử bộ khởi chạy và ứng dụng; bạn cần điền tài khoản MySQL thực tế và nạp dữ liệu của bạn để chạy thường xuyên từ IDE.

Tham khảo cơ chế chính thức: [Application Run và Before launch trong IntelliJ](https://www.jetbrains.com/help/idea/run-debug-configuration-java-application.html), [Bootstrap và class loader Tomcat](https://tomcat.apache.org/tomcat-9.0-doc/class-loader-howto.html).

## Lỗi NativeCommandError khi dò Java trên Windows PowerShell 5.1

Bản scripts/build-module1.ps1 đã được sửa để thu thập thông tin Java ghi trên stderr mà không dừng nhầm một lệnh thành công. Các bước build, đóng gói và lỗi thực vẫn dừng như bình thường. Chạy lại lệnh run-intellij.ps1 sau khi cập nhật script.

Nếu trang chính đã mở nhưng tra cứu báo không tải được dữ liệu, kiểm tra logs/localhost*.log dưới .runtime/intellij-tomcat. Access denied for user root@localhost nghĩa là cần kiểm tra shopman.jdbc.user/password trong src/shopman.properties cho khớp với kết nối LocalServer ở Workbench; sau khi sửa, lưu và chạy lại để đóng gói cấu hình mới.
