# Cài đặt các thư viện cần thiết

pip install selenium

# Tải ChromeDriver

# 1. Truy cập https://chromedriver.chromium.org/

# 2. Tải phiên bản phù hợp với Chrome browser

# 3. Giải nén và đặt vào PATH hoặc thư mục project

# Cấu trúc thư mục

project/
├── test_cases.csv
├── facebook_signup_test.py
├── sign_up.html
└── chromedriver (nếu không có trong PATH)

# Chạy test

python facebook_signup_test.py
\`\`\`

## Các tính năng chính của script:

1. **Đọc test cases từ CSV**: Script tự động đọc và parse file CSV
2. **Validation toàn diện**: Kiểm tra required fields, format email, độ dài password
3. **Clear form**: Xóa dữ liệu trước mỗi test case
4. **Báo cáo chi tiết**: Hiển thị kết quả từng test case và tổng quan
5. **Error handling**: Xử lý lỗi và ghi log chi tiết
6. **Headless mode**: Chạy test không hiển thị browser
7. \*\*Tạo báo cáo report

Script này sẽ test tất cả các trường hợp trong file CSV và tạo báo cáo chi tiết về kết quả test [^1].
