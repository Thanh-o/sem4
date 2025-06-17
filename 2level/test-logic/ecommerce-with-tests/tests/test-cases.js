// Test cases cho luồng bán hàng e-commerce
export const testCases = [
  {
    id: "TC001",
    name: "Kiểm tra trang chủ hiển thị đúng",
    description: "Verify homepage displays correctly with products",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "check_element",
        selector: '[data-testid="site-title"]',
        expected: "TechStore",
        description: "Kiểm tra tiêu đề trang",
      },
      {
        action: "check_element_count",
        selector: '[data-testid^="product-"]',
        expected: 4,
        description: "Kiểm tra số lượng sản phẩm hiển thị",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="cart-button"]',
        description: "Kiểm tra nút giỏ hàng hiển thị",
      },
    ],
  },
  {
    id: "TC002",
    name: "Thêm sản phẩm vào giỏ hàng từ trang chủ",
    description: "Add product to cart from homepage",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-1"]',
        description: "Click thêm iPhone vào giỏ hàng",
      },
      {
        action: "wait",
        duration: 1000,
        description: "Đợi cập nhật giỏ hàng",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="cart-button"] .absolute',
        expected: "1",
        description: "Kiểm tra số lượng trong giỏ hàng",
      },
    ],
  },
  {
    id: "TC003",
    name: "Xem chi tiết sản phẩm",
    description: "View product details page",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="view-product-1"]',
        description: "Click xem chi tiết iPhone",
      },
      {
        action: "wait_for_url",
        url: "/product/1",
        description: "Đợi chuyển đến trang chi tiết",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="product-name"]',
        expected: "iPhone 15 Pro",
        description: "Kiểm tra tên sản phẩm",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="product-price"]',
        expected: "$999",
        description: "Kiểm tra giá sản phẩm",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="quantity-select"]',
        description: "Kiểm tra dropdown số lượng",
      },
    ],
  },
  {
    id: "TC004",
    name: "Thêm sản phẩm vào giỏ hàng từ trang chi tiết",
    description: "Add product to cart from product detail page",
    steps: [
      {
        action: "navigate",
        url: "/product/2",
        description: "Truy cập trang chi tiết MacBook",
      },
      {
        action: "select_option",
        selector: '[data-testid="quantity-select"]',
        value: "2",
        description: "Chọn số lượng 2",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-button"]',
        description: "Click thêm vào giỏ hàng",
      },
      {
        action: "wait_for_url",
        url: "/cart",
        description: "Đợi chuyển đến trang giỏ hàng",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="quantity-2"]',
        expected: "2",
        description: "Kiểm tra số lượng trong giỏ hàng",
      },
    ],
  },
  {
    id: "TC005",
    name: "Quản lý giỏ hàng - tăng/giảm số lượng",
    description: "Manage cart - increase/decrease quantity",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-1"]',
        description: "Thêm iPhone vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="cart-button"]',
        description: "Vào trang giỏ hàng",
      },
      {
        action: "wait_for_url",
        url: "/cart",
        description: "Đợi tải trang giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="increase-1"]',
        description: "Tăng số lượng iPhone",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="quantity-1"]',
        expected: "2",
        description: "Kiểm tra số lượng đã tăng",
      },
      {
        action: "click",
        selector: '[data-testid="decrease-1"]',
        description: "Giảm số lượng iPhone",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="quantity-1"]',
        expected: "1",
        description: "Kiểm tra số lượng đã giảm",
      },
    ],
  },
  {
    id: "TC006",
    name: "Xóa sản phẩm khỏi giỏ hàng",
    description: "Remove product from cart",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-3"]',
        description: "Thêm AirPods vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="cart-button"]',
        description: "Vào trang giỏ hàng",
      },
      {
        action: "wait_for_url",
        url: "/cart",
        description: "Đợi tải trang giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="remove-3"]',
        description: "Xóa AirPods khỏi giỏ hàng",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="empty-cart"]',
        description: "Kiểm tra thông báo giỏ hàng trống",
      },
    ],
  },
  {
    id: "TC007",
    name: "Luồng thanh toán hoàn chỉnh",
    description: "Complete checkout flow",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-1"]',
        description: "Thêm iPhone vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-2"]',
        description: "Thêm MacBook vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="cart-button"]',
        description: "Vào trang giỏ hàng",
      },
      {
        action: "wait_for_url",
        url: "/cart",
        description: "Đợi tải trang giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="checkout-button"]',
        description: "Click thanh toán",
      },
      {
        action: "wait_for_url",
        url: "/checkout",
        description: "Đợi tải trang thanh toán",
      },
      {
        action: "fill_form",
        fields: {
          '[data-testid="email-input"]': "test@example.com",
          '[data-testid="first-name-input"]': "Nguyen",
          '[data-testid="last-name-input"]': "Van A",
          '[data-testid="address-input"]': "123 Nguyen Trai",
          '[data-testid="city-input"]': "Ho Chi Minh",
          '[data-testid="zip-code-input"]': "70000",
          '[data-testid="phone-input"]': "0901234567",
        },
        description: "Điền thông tin khách hàng",
      },
      {
        action: "select_option",
        selector: '[data-testid="payment-method-select"]',
        value: "credit-card",
        description: "Chọn phương thức thanh toán",
      },
      {
        action: "click",
        selector: '[data-testid="place-order-button"]',
        description: "Đặt hàng",
      },
      {
        action: "wait_for_url",
        url: "/order-confirmation",
        description: "Đợi tải trang xác nhận",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="success-title"]',
        description: "Kiểm tra thông báo đặt hàng thành công",
      },
    ],
  },
  {
    id: "TC008",
    name: "Kiểm tra thông tin đơn hàng sau khi đặt hàng",
    description: "Verify order information after placing order",
    steps: [
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-4"]',
        description: "Thêm iPad vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="cart-button"]',
        description: "Vào trang giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="checkout-button"]',
        description: "Click thanh toán",
      },
      {
        action: "fill_form",
        fields: {
          '[data-testid="email-input"]': "customer@test.com",
          '[data-testid="first-name-input"]': "Tran",
          '[data-testid="last-name-input"]': "Thi B",
          '[data-testid="address-input"]': "456 Le Loi",
          '[data-testid="city-input"]': "Ha Noi",
          '[data-testid="zip-code-input"]': "10000",
          '[data-testid="phone-input"]': "0987654321",
        },
        description: "Điền thông tin khách hàng",
      },
      {
        action: "select_option",
        selector: '[data-testid="payment-method-select"]',
        value: "cod",
        description: "Chọn thanh toán khi nhận hàng",
      },
      {
        action: "click",
        selector: '[data-testid="place-order-button"]',
        description: "Đặt hàng",
      },
      {
        action: "wait_for_url",
        url: "/order-confirmation",
        description: "Đợi tải trang xác nhận",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="recipient-name"]',
        expected: "Tran Thi B",
        description: "Kiểm tra tên người nhận",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="email-address"]',
        expected: "customer@test.com",
        description: "Kiểm tra email",
      },
      {
        action: "check_element_text",
        selector: '[data-testid="payment-method"]',
        expected: "Thanh toán khi nhận hàng",
        description: "Kiểm tra phương thức thanh toán",
      },
    ],
  },
  {
    id: "TC009",
    name: "Kiểm tra navigation giữa các trang",
    description: "Test navigation between pages",
    steps: [
      {
        action: "navigate",
        url: "/product/1",
        description: "Truy cập trang chi tiết sản phẩm",
      },
      {
        action: "click",
        selector: '[data-testid="back-button"]',
        description: "Click nút quay lại",
      },
      {
        action: "wait_for_url",
        url: "/",
        description: "Kiểm tra quay về trang chủ",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-1"]',
        description: "Thêm sản phẩm vào giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="cart-button"]',
        description: "Vào trang giỏ hàng",
      },
      {
        action: "click",
        selector: '[data-testid="back-button"]',
        description: "Click quay lại từ giỏ hàng",
      },
      {
        action: "wait_for_url",
        url: "/",
        description: "Kiểm tra quay về trang chủ",
      },
    ],
  },
  {
    id: "TC010",
    name: "Kiểm tra responsive design trên mobile",
    description: "Test responsive design on mobile viewport",
    steps: [
      {
        action: "set_viewport",
        width: 375,
        height: 667,
        description: "Chuyển sang viewport mobile",
      },
      {
        action: "navigate",
        url: "/",
        description: "Truy cập trang chủ",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="products-grid"]',
        description: "Kiểm tra grid sản phẩm hiển thị",
      },
      {
        action: "click",
        selector: '[data-testid="view-product-1"]',
        description: "Xem chi tiết sản phẩm",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="product-image"]',
        description: "Kiểm tra hình ảnh sản phẩm hiển thị",
      },
      {
        action: "click",
        selector: '[data-testid="add-to-cart-button"]',
        description: "Thêm vào giỏ hàng",
      },
      {
        action: "check_element_visible",
        selector: '[data-testid="cart-items"]',
        description: "Kiểm tra giỏ hàng hiển thị đúng",
      },
    ],
  },
]
