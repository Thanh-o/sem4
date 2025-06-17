import { testCases } from "./test-cases.js"

class TestRunner {
  constructor() {
    this.results = []
    this.currentTest = null
  }

  async runAllTests() {
    console.log("🚀 Bắt đầu chạy test suite...\n")

    for (const testCase of testCases) {
      await this.runSingleTest(testCase)
    }

    this.generateReport()
  }

  async runSingleTest(testCase) {
    console.log(`📋 Chạy test: ${testCase.id} - ${testCase.name}`)
    this.currentTest = {
      ...testCase,
      startTime: Date.now(),
      status: "running",
      errors: [],
    }

    try {
      for (const step of testCase.steps) {
        await this.executeStep(step)
      }

      this.currentTest.status = "passed"
      this.currentTest.endTime = Date.now()
      console.log(`✅ Test ${testCase.id} PASSED\n`)
    } catch (error) {
      this.currentTest.status = "failed"
      this.currentTest.endTime = Date.now()
      this.currentTest.errors.push(error.message)
      console.log(`❌ Test ${testCase.id} FAILED: ${error.message}\n`)
    }

    this.results.push(this.currentTest)
  }

  async executeStep(step) {
    console.log(`  ⏳ ${step.description}`)

    // Simulate step execution based on action type
    switch (step.action) {
      case "navigate":
        await this.simulateNavigation(step.url)
        break
      case "click":
        await this.simulateClick(step.selector)
        break
      case "check_element":
        await this.simulateElementCheck(step.selector, step.expected)
        break
      case "check_element_count":
        await this.simulateElementCountCheck(step.selector, step.expected)
        break
      case "check_element_visible":
        await this.simulateVisibilityCheck(step.selector)
        break
      case "check_element_text":
        await this.simulateTextCheck(step.selector, step.expected)
        break
      case "fill_form":
        await this.simulateFillForm(step.fields)
        break
      case "select_option":
        await this.simulateSelectOption(step.selector, step.value)
        break
      case "wait":
        await this.simulateWait(step.duration)
        break
      case "wait_for_url":
        await this.simulateWaitForUrl(step.url)
        break
      case "set_viewport":
        await this.simulateSetViewport(step.width, step.height)
        break
      default:
        throw new Error(`Unknown action: ${step.action}`)
    }

    console.log(`  ✓ ${step.description} - Completed`)
  }

  async simulateNavigation(url) {
    await this.delay(500)
    // In real implementation, this would use Playwright's page.goto()
    console.log(`    → Navigating to: ${url}`)
  }

  async simulateClick(selector) {
    await this.delay(300)
    console.log(`    → Clicking: ${selector}`)
  }

  async simulateElementCheck(selector, expected) {
    await this.delay(200)
    console.log(`    → Checking element ${selector} equals "${expected}"`)
  }

  async simulateElementCountCheck(selector, expected) {
    await this.delay(200)
    console.log(`    → Checking ${selector} count equals ${expected}`)
  }

  async simulateVisibilityCheck(selector) {
    await this.delay(200)
    console.log(`    → Checking visibility of: ${selector}`)
  }

  async simulateTextCheck(selector, expected) {
    await this.delay(200)
    console.log(`    → Checking text of ${selector} equals "${expected}"`)
  }

  async simulateFillForm(fields) {
    await this.delay(800)
    console.log(`    → Filling form with ${Object.keys(fields).length} fields`)
    for (const [selector, value] of Object.entries(fields)) {
      console.log(`      • ${selector}: "${value}"`)
    }
  }

  async simulateSelectOption(selector, value) {
    await this.delay(300)
    console.log(`    → Selecting option "${value}" in ${selector}`)
  }

  async simulateWait(duration) {
    await this.delay(duration)
    console.log(`    → Waited ${duration}ms`)
  }

  async simulateWaitForUrl(url) {
    await this.delay(500)
    console.log(`    → Waiting for URL: ${url}`)
  }

  async simulateSetViewport(width, height) {
    await this.delay(200)
    console.log(`    → Setting viewport to ${width}x${height}`)
  }

  delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms))
  }

  generateReport() {
    const passed = this.results.filter((r) => r.status === "passed").length
    const failed = this.results.filter((r) => r.status === "failed").length
    const total = this.results.length

    console.log("\n" + "=".repeat(60))
    console.log("📊 BÁO CÁO KẾT QUẢ TEST")
    console.log("=".repeat(60))
    console.log(`Tổng số test: ${total}`)
    console.log(`✅ Passed: ${passed}`)
    console.log(`❌ Failed: ${failed}`)
    console.log(`📈 Tỷ lệ thành công: ${((passed / total) * 100).toFixed(1)}%`)
    console.log("=".repeat(60))

    if (failed > 0) {
      console.log("\n❌ CHI TIẾT CÁC TEST FAILED:")
      this.results
        .filter((r) => r.status === "failed")
        .forEach((test) => {
          console.log(`\n• ${test.id} - ${test.name}`)
          test.errors.forEach((error) => {
            console.log(`  Error: ${error}`)
          })
        })
    }

    console.log("\n⏱️  THỜI GIAN THỰC THI:")
    this.results.forEach((test) => {
      const duration = test.endTime - test.startTime
      const status = test.status === "passed" ? "✅" : "❌"
      console.log(`${status} ${test.id}: ${duration}ms`)
    })

    console.log("\n🎯 Test suite hoàn thành!")
  }

  // Method to run specific test by ID
  async runTestById(testId) {
    const testCase = testCases.find((tc) => tc.id === testId)
    if (!testCase) {
      console.log(`❌ Không tìm thấy test với ID: ${testId}`)
      return
    }

    console.log(`🎯 Chạy test đơn lẻ: ${testId}\n`)
    await this.runSingleTest(testCase)
    this.generateReport()
  }

  // Method to run tests by category/tag
  async runTestsByTag(tag) {
    const filteredTests = testCases.filter(
      (tc) =>
        tc.name.toLowerCase().includes(tag.toLowerCase()) || tc.description.toLowerCase().includes(tag.toLowerCase()),
    )

    if (filteredTests.length === 0) {
      console.log(`❌ Không tìm thấy test nào với tag: ${tag}`)
      return
    }

    console.log(`🏷️  Chạy ${filteredTests.length} test với tag: ${tag}\n`)

    for (const testCase of filteredTests) {
      await this.runSingleTest(testCase)
    }

    this.generateReport()
  }
}

// Export for use
export { TestRunner }

// Example usage
const runner = new TestRunner()

// Chạy tất cả test
console.log("🔧 DEMO: Chạy Test Suite E-commerce")
console.log("Đây là simulation - trong thực tế sẽ sử dụng Playwright để test thật\n")

await runner.runAllTests()

// Uncomment để chạy test cụ thể:
// await runner.runTestById('TC007');
// await runner.runTestsByTag('checkout');
