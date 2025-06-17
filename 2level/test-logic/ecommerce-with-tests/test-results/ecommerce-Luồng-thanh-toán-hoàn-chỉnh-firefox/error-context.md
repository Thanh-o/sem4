# Test info

- Name: Luồng thanh toán hoàn chỉnh
- Location: C:\Boss\GitHub\sem4\2level\test-logic\ecommerce-with-tests\tests\playwright\ecommerce.spec.js:71:7

# Error details

```
Error: page.selectOption: Error: Element is not a <select> element
Call log:
  - waiting for locator('[data-testid="payment-method-select"]')
    - locator resolved to <button dir="ltr" type="button" role="combobox" data-state="closed" data-placeholder="" aria-expanded="false" aria-autocomplete="none" aria-controls="radix-:r0:" data-testid="payment-method-select" class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 [&>span]:line-clamp…>…</button>
  - attempting select option action
    - waiting for element to be visible and enabled

    at PlaywrightTestRunner.executeStep (C:\Boss\GitHub\sem4\2level\test-logic\ecommerce-with-tests\tests\playwright\ecommerce.spec.js:45:25)
    at PlaywrightTestRunner.runTestCase (C:\Boss\GitHub\sem4\2level\test-logic\ecommerce-with-tests\tests\playwright\ecommerce.spec.js:64:18)
    at C:\Boss\GitHub\sem4\2level\test-logic\ecommerce-with-tests\tests\playwright\ecommerce.spec.js:73:5
```

# Page snapshot

```yaml
- alert
- banner:
  - link "Quay lại giỏ hàng":
    - /url: /cart
    - button "Quay lại giỏ hàng":
      - img
      - text: Quay lại giỏ hàng
  - heading "Thanh toán" [level=1]
- main:
  - text: Thông tin liên hệ Email *
  - textbox "Email *": test@example.com
  - text: Địa chỉ giao hàng Họ *
  - textbox "Họ *": Nguyen
  - text: Tên *
  - textbox "Tên *": Van A
  - text: Địa chỉ *
  - textbox "Địa chỉ *": 123 Nguyen Trai
  - text: Thành phố *
  - textbox "Thành phố *": Ho Chi Minh
  - text: Mã bưu điện *
  - textbox "Mã bưu điện *": "70000"
  - text: Số điện thoại *
  - textbox "Số điện thoại *": "0901234567"
  - text: Phương thức thanh toán
  - combobox: Chọn phương thức thanh toán
  - text: "Đơn hàng của bạn iPhone 15 Prox1 $999 MacBook Air M2x1 $1199 Tạm tính: $2198 Phí vận chuyển: Miễn phí Tổng cộng: $2198"
  - button "Đặt hàng"
```

# Test source

```ts
   1 | import { test, expect } from "@playwright/test"
   2 | import { testCases } from "../test-cases.js"
   3 |
   4 | // Playwright implementation của test cases
   5 | class PlaywrightTestRunner {
   6 |   constructor(page) {
   7 |     this.page = page
   8 |   }
   9 |
   10 |   async executeStep(step) {
   11 |     switch (step.action) {
   12 |       case "navigate":
   13 |         await this.page.goto(step.url)
   14 |         break
   15 |
   16 |       case "click":
   17 |         await this.page.click(step.selector)
   18 |         break
   19 |
   20 |       case "check_element":
   21 |         const element = await this.page.locator(step.selector)
   22 |         await expect(element).toHaveText(step.expected)
   23 |         break
   24 |
   25 |       case "check_element_count":
   26 |         const elements = await this.page.locator(step.selector)
   27 |         await expect(elements).toHaveCount(step.expected)
   28 |         break
   29 |
   30 |       case "check_element_visible":
   31 |         await expect(this.page.locator(step.selector)).toBeVisible()
   32 |         break
   33 |
   34 |       case "check_element_text":
   35 |         await expect(this.page.locator(step.selector)).toHaveText(step.expected)
   36 |         break
   37 |
   38 |       case "fill_form":
   39 |         for (const [selector, value] of Object.entries(step.fields)) {
   40 |           await this.page.fill(selector, value)
   41 |         }
   42 |         break
   43 |
   44 |       case "select_option":
>  45 |         await this.page.selectOption(step.selector, step.value)
      |                         ^ Error: page.selectOption: Error: Element is not a <select> element
   46 |         break
   47 |
   48 |       case "wait":
   49 |         await this.page.waitForTimeout(step.duration)
   50 |         break
   51 |
   52 |       case "wait_for_url":
   53 |         await this.page.waitForURL(step.url)
   54 |         break
   55 |
   56 |       case "set_viewport":
   57 |         await this.page.setViewportSize({ width: step.width, height: step.height })
   58 |         break
   59 |     }
   60 |   }
   61 |
   62 |   async runTestCase(testCase) {
   63 |     for (const step of testCase.steps) {
   64 |       await this.executeStep(step)
   65 |     }
   66 |   }
   67 | }
   68 |
   69 | // Generate Playwright tests from test cases
   70 | testCases.forEach((testCase) => {
   71 |   test(testCase.name, async ({ page }) => {
   72 |     const runner = new PlaywrightTestRunner(page)
   73 |     await runner.runTestCase(testCase)
   74 |   })
   75 | })
   76 |
   77 | // Additional Playwright-specific tests
   78 | test("Performance: Page load times", async ({ page }) => {
   79 |   const startTime = Date.now()
   80 |   await page.goto("/")
   81 |   const loadTime = Date.now() - startTime
   82 |
   83 |   expect(loadTime).toBeLessThan(3000) // Should load within 3 seconds
   84 | })
   85 |
   86 | test("Accessibility: Check for basic a11y violations", async ({ page }) => {
   87 |   await page.goto("/")
   88 |
   89 |   // Check for alt text on images
   90 |   const images = await page.locator("img").all()
   91 |   for (const img of images) {
   92 |     const alt = await img.getAttribute("alt")
   93 |     expect(alt).toBeTruthy()
   94 |   }
   95 |
   96 |   // Check for proper heading structure
   97 |   const h1Count = await page.locator("h1").count()
   98 |   expect(h1Count).toBeGreaterThanOrEqual(1)
   99 | })
  100 |
  101 | test("Error handling: 404 page", async ({ page }) => {
  102 |   const response = await page.goto("/non-existent-page")
  103 |   expect(response?.status()).toBe(404)
  104 | })
  105 |
```