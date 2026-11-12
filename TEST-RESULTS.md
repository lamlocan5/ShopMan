# Kết quả kiểm thử mô đun 1

- Môi trường: JDK 21.0.7; Tomcat 9.0.112; MySQL 8.
- Kết quả: **PASS 96 kiểm tra**.
- Java: biên dịch thành công với `--release 8`.
- JSP: biên dịch và thực thi bằng Tomcat thật qua HTTP.
- DAO/View: tìm theo tên, phân trang, giá hiện tại, tồn ở các kho hoạt động, trạng thái kinh doanh và quan hệ biến thể–mặt hàng.
- Truy cập công khai: cả bốn màn hình mở được khi chưa đăng nhập; DAO/View nhận `sessionAccount = null`; tìm kiếm vẫn chạy khi tắt cookie và sau đăng xuất. Luồng tra cứu không tạo phiên đăng nhập.
- Tài khoản tùy chọn: đăng nhập, sai mật khẩu, thay phiên và đăng xuất được kiểm tra riêng; không tham gia tiền điều kiện của tra cứu.
- Dữ liệu: so sánh cả bảy bảng trước/sau tra cứu; không có thay đổi.
- IntelliJ: đã chạy bộ kiểm thử qua script chuẩn bị Tomcat và classpath dùng cho cấu hình Application. Các XML project/module/libraries/artifact/run hợp lệ.
- Giới hạn xác minh: đã kiểm tra cấu hình chạy IntelliJ bằng script và tiến trình Java tương ứng; chưa bấm nút Run trực tiếp trong IDE.
- Dọn kiểm thử: đã xóa CSDL tạm do bộ kiểm thử tạo và dừng Tomcat thử nghiệm.

Lệnh chạy lại ở README.md; cách mở và chạy dự án ở HUONG_DAN_INTELLIJ.md.

## Bổ sung kiểm tra Windows PowerShell 5.1

- Tái hiện đúng NativeCommandError ở bước dò JDK của build-module1.ps1.
- Sau sửa: powershell.exe biên dịch Java và đóng gói WAR thành công.
- Chạy trọn run-intellij.ps1 trong bản sao kiểm thử: chuẩn bị runtime, khởi động Tomcat và trang chính công khai HTTP 200 thành công; không còn lỗi xử lý stderr khi dò JDK.
- Kiểm tra tìm kiếm với cấu hình CSDL hiện tại bị từ chối xác thực MySQL cho root@localhost. Cần người chạy kiểm tra lại tài khoản/mật khẩu; chưa xác nhận luồng tìm kiếm bằng cấu hình cá nhân này.
- Đã dừng cây tiến trình PowerShell/Tomcat kiểm thử. Không sửa cấu hình mật khẩu của người dùng.
