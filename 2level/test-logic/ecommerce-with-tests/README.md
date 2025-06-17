# E-commerce Project với Auto Testing

Đây là một project e-commerce hoàn chỉnh được xây dựng với Next.js và có hệ thống auto testing độc lập sử dụng Playwright.

## 🚀 Tính năng chính

### Luồng bán hàng hoàn chỉnh:
- **Trang chủ**: Hiển thị danh sách sản phẩm
- **Chi tiết sản phẩm**: Xem thông tin chi tiết, chọn số lượng
- **Giỏ hàng**: Quản lý sản phẩm (thêm, xóa, sửa số lượng)
- **Thanh toán**: Điền thông tin khách hàng và đặt hàng
- **Xác nhận đơn hàng**: Hiển thị thông tin đơn hàng đã đặt

### Hệ thống Auto Testing:
- **Test Cases**: File JSON chứa các test case chi tiết
- **Test Runner**: Engine chạy test độc lập với code chính
- **Playwright Integration**: Test thực tế trên browser
- **Báo cáo chi tiết**: Kết quả test với thống kê

## 📦 Cài đặt

\`\`\`bash
# Clone project
git clone <repository-url>
cd ecommerce-with-tests

# Cài đặt dependencies
npm install

# Cài đặt Playwright browsers
npm run test:install
\`\`\`

## 🏃‍♂️ Chạy ứng dụng

\`\`\`bash
# Development mode
npm run dev

# Production build
npm run build
npm start
\`\`\`

Truy cập: http://localhost:3000

## 🧪 Chạy Tests

### 1. Chạy Test Simulation (Demo)
\`\`\`bash
npm test
\`\`\`
Chạy simulation của tất cả test cases để demo luồng testing.

### 2. Chạy Test thực tế với Playwright
\`\`\`bash
# Chạy tất cả tests
npm run test:playwright

# Chạy test với UI mode
npx playwright test --ui

# Chạy test trên browser cụ thể
npx playwright test --project=chromium
\`\`\`

### 3. Chạy Test cụ thể
\`\`\`bash
# Chạy test theo ID
npm run test:single TC007

# Chạy test theo tag
node -e "import('./tests/test-runner.js').then(({TestRunner}) => new TestRunner().runTestsByTag('checkout'))"
\`\`\`

## 📋 Test Cases

Project bao gồm 10 test cases chính:

1. **TC001**: Kiểm tra trang chủ hiển thị đúng
2. **TC002**: Thêm sản phẩm vào giỏ hàng từ trang chủ  
3. **TC003**: Xem chi tiết sản phẩm
4. **TC004**: Thêm sản phẩm từ trang chi tiết
5. **TC005**: Quản lý giỏ hàng (tăng/giảm số lượng)
6. **TC006**: Xóa sản phẩm khỏi giỏ hàng
7. **TC007**: Luồng thanh toán hoàn chỉnh
8. **TC008**: Kiểm tra thông tin đơn hàng
9. **TC009**: Kiểm tra navigation giữa các trang
10. **TC010**: Kiểm tra responsive design

## 🏗️ Cấu trúc Project

\`\`\`
├── app/                          # Next.js App Router
│   ├── page.tsx                 # Trang chủ
│   ├── product/[id]/page.tsx    # Chi tiết sản phẩm
│   ├── cart/page.tsx            # Giỏ hàng
│   ├── checkout/page.tsx        # Thanh toán
│   └── order-confirmation/page.tsx # Xác nhận đơn hàng
├── tests/
│   ├── test-cases.js            # Định nghĩa test cases
│   ├── test-runner.js           # Test runner engine
│   └── playwright/
│       └── ecommerce.spec.js    # Playwright tests
├── playwright.config.js         # Cấu hình Playwright
└── package.json
\`\`\`

## 🎯 Đặc điểm nổi bật

### 1. Tách biệt hoàn toàn
- Code test hoàn toàn độc lập với code ứng dụng
- Không ảnh hưởng đến performance của app chính
- Có thể chạy test mà không cần build app

### 2. Test Cases linh hoạt
- Định nghĩa test cases trong file JSON
- Dễ dàng thêm/sửa/xóa test cases
- Hỗ trợ nhiều loại action khác nhau

### 3. Báo cáo chi tiết
- Thống kê tỷ lệ pass/fail
- Chi tiết lỗi cho từng test case
- Thời gian thực thi từng test

### 4. Đa platform
- Test trên nhiều browser (Chrome, Firefox, Safari)
- Test responsive trên mobile
- CI/CD ready

## 🔧 Tùy chỉnh Test Cases

Để thêm test case mới, chỉnh sửa file `tests/test-cases.js`:

\`\`\`javascript
{
  id: 'TC011',
  name: 'Tên test case',
  description: 'Mô tả test case',
  steps: [
    {
      action: 'navigate',
      url: '/path',
      description: 'Mô tả bước'
    },
    // ... thêm các bước khác
  ]
}
\`\`\`

## 📊 Các loại Action hỗ trợ

- `navigate`: Điều hướng đến URL
- `click`: Click element
- `check_element`: Kiểm tra nội dung element
- `check_element_count`: Kiểm tra số lượng element
- `check_element_visible`: Kiểm tra element hiển thị
- `fill_form`: Điền form
- `select_option`: Chọn option trong select
- `wait`: Đợi một khoảng thời gian
- `wait_for_url`: Đợi URL thay đổi
- `set_viewport`: Thay đổi kích thước viewport

## 🚀 Triển khai

Project có thể được triển khai trên Vercel, Netlify hoặc bất kỳ platform nào hỗ trợ Next.js.

Tests có thể được tích hợp vào CI/CD pipeline để chạy tự động khi có code mới.

---

**Lưu ý**: Đây là project demo hoàn chỉnh với luồng bán hàng thực tế và hệ thống testing chuyên nghiệp. Code test hoàn toàn độc lập và không ảnh hưởng đến ứng dụng chính.
