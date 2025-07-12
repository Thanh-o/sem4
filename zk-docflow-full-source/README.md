# 📘 ZK com.example.zkdoc.model.Document Workflow (ZK 7.0.5)

Ứng dụng web quản lý văn bản đi – đến với luồng xử lý động.

---

## 🚀 Yêu cầu hệ thống

- Java JDK 8 trở lên
- Maven 3.x
- MySQL 5.7+ hoặc 8.x
- IDE: IntelliJ IDEA / Eclipse

---

## 📦 Hướng dẫn cài đặt

### 1. Clone source code
```bash
git clone https://your-repo-url/zk-docflow-full
cd zk-docflow-full
```

### 2. Tạo database
```sql
CREATE DATABASE zk_docflow DEFAULT CHARACTER SET utf8mb4;
USE zk_docflow;
```

### 3. Import dữ liệu mẫu
Chạy file `db/schema.sql` trong MySQL Workbench hoặc bằng dòng lệnh:
```bash
mysql -u root -p zk_docflow < db/schema.sql
```

### 4. Cấu hình kết nối DB trong file `DBUtil.java`
```java
String url = "jdbc:mysql://localhost:3306/zk_docflow?useSSL=false";
String user = "root";
String pass = "123456";
```

### 5. Build & chạy project
```bash
mvn clean install
```

Sau đó deploy lên Tomcat hoặc dùng Jetty Plugin:
```bash
mvn jetty:run
```

### 6. Truy cập ứng dụng
Mở trình duyệt:
```
http://localhost:8080/index.zul
```

---

## 🔐 Tài khoản mẫu
| Vai trò     | Username     | Password |
|-------------|--------------|----------|
| Nhân viên   | nhanvien1    | 123456   |
| Lãnh đạo    | lanhdao1     | 123456   |

---

## ✅ Chức năng chính

- Đăng nhập theo vai trò
- Tạo, xem, tra cứu văn bản đi và đến
- Xử lý động: phê duyệt, chuyển tiếp, từ chối
- Lưu lịch sử xử lý dạng timeline
- Dashboard thống kê theo loại & trạng thái văn bản
- Notification khi xử lý văn bản


## 🧭 Tài liệu mô tả kiến trúc & luồng xử lý

### 1. Kiến trúc tổng thể
- **Pattern**: MVVM (Model - View - ViewModel)
- **View (.zul)**: Giao diện XML ZK
- **ViewModel (.java)**: Xử lý logic và binding dữ liệu
- **Model (.java)**: Entity phản ánh bảng DB
- **Service (.java)**: Xử lý JDBC hoặc mock data

### 2. Luồng xử lý văn bản
- Nhân viên tạo văn bản mới (Đi hoặc Đến)
- Văn bản ở trạng thái **Chờ xử lý**
- Lãnh đạo đăng nhập → nhận danh sách văn bản → chọn văn bản xử lý
    - **Phê duyệt** → chuyển trạng thái Hoàn thành
    - **Chuyển tiếp** → chọn người dùng tiếp theo (runtime)
    - **Từ chối** → cập nhật trạng thái Từ chối
- Mỗi thao tác ghi log vào bảng `document_history`

### 3. Phân quyền người dùng
- `NHANVIEN`: chỉ được tạo & xem văn bản
- `LANHDAO`: được phép xử lý (phê duyệt/chuyển tiếp/từ chối)

### 4. Timeline xử lý (custom component)
- Hiển thị dạng thời gian:
```
[08:00] Nguyễn Văn A - Tạo văn bản
[09:00] Trần Văn B - Chuyển tiếp
[10:00] Lê Thị C - Phê duyệt
```
- Lấy dữ liệu từ bảng `document_history` theo `document_id`

### 5. Thống kê dashboard
- Tổng số văn bản theo loại (Đi / Đến)
- Tổng số văn bản theo trạng thái (Chờ xử lý / Đang xử lý / Hoàn thành)
- Tùy chọn hiển thị bảng hoặc biểu đồ (ZK Charts)


