package org.example.testlogic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("External Frontend Testing with Selenium")
public class ExternalFrontendTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private ObjectMapper objectMapper;
    private StringBuilder csvReport;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Comment out để xem browser hoạt động
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        objectMapper = new ObjectMapper();
        csvReport = new StringBuilder();

        // CSV Header
        csvReport.append("Frontend URL,Test Case,Step,Action,Selector,Expected,Actual,Duration (ms),Result,Error Message,Timestamp\n");

        totalTests = 0;
        passedTests = 0;
        failedTests = 0;

        System.out.println("🚀 External Frontend selenium11.json setup completed");
    }

    @AfterEach
    void tearDown() {
        generateCsvReport();
        printTestSummary();

        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "http://localhost:3000, React Frontend",
            "http://localhost:8080, Vue Frontend",
            "http://localhost:4200, Angular Frontend",
            "https://your-deployed-app.vercel.app, Production Frontend",
            "https://your-staging-app.herokuapp.com, Staging Frontend"
    })
    @DisplayName("Test Multiple External Frontends")
    void testExternalFrontends(String frontendUrl, String frontendName) {
        System.out.println("🧪 Testing " + frontendName + " at " + frontendUrl);

        if (!isFrontendAccessible(frontendUrl)) {
            System.out.println("⚠️ Skipping " + frontendName + " - not accessible");
            return;
        }

        try {
            testBasicFunctionality(frontendUrl, frontendName);
            passedTests++;
            System.out.println("✅ " + frontendName + " selenium11.json PASSED");
        } catch (Exception e) {
            failedTests++;
            System.err.println("❌ " + frontendName + " selenium11.json FAILED: " + e.getMessage());
        }
        totalTests++;
    }

    @Test
    @DisplayName("Test Specific External Frontend with Custom Test Cases")
    void testSpecificExternalFrontend() throws IOException {
        // URL của frontend có sẵn mà bạn muốn selenium11.json
        String frontendUrl = "http://localhost:3000"; // Thay đổi URL này
        String frontendName = "My React App";

        if (!isFrontendAccessible(frontendUrl)) {
            System.out.println("⚠️ Frontend not accessible at " + frontendUrl);
            return;
        }

        System.out.println("🧪 Testing " + frontendName + " at " + frontendUrl);

        try {
            // Load custom selenium11.json cases for this specific frontend
            executeCustomTestCases(frontendUrl, frontendName);
            passedTests++;
            System.out.println("✅ Custom selenium11.json cases PASSED for " + frontendName);
        } catch (Exception e) {
            failedTests++;
            System.err.println("❌ Custom selenium11.json cases FAILED for " + frontendName + ": " + e.getMessage());
        }
        totalTests++;
    }

    @Test
    @DisplayName("Test E-commerce Frontend (Generic)")
    void testEcommerceFrontend() {
        // Test một frontend e-commerce có sẵn
        String frontendUrl = "https://demo.opencart.com"; // Ví dụ demo site
        String frontendName = "OpenCart Demo";

        System.out.println("🧪 Testing " + frontendName + " at " + frontendUrl);

        try {
            testEcommerceFlow(frontendUrl, frontendName);
            passedTests++;
            System.out.println("✅ E-commerce selenium11.json PASSED for " + frontendName);
        } catch (Exception e) {
            failedTests++;
            System.err.println("❌ E-commerce selenium11.json FAILED for " + frontendName + ": " + e.getMessage());
        }
        totalTests++;
    }

    private boolean isFrontendAccessible(String url) {
        try {
            driver.get(url);
            // Đợi trang load và kiểm tra title không rỗng
            wait.until(ExpectedConditions.not(ExpectedConditions.titleIs("")));
            return true;
        } catch (Exception e) {
            System.err.println("Cannot access " + url + ": " + e.getMessage());
            return false;
        }
    }

    private void testBasicFunctionality(String frontendUrl, String frontendName) {
        long testStartTime = System.currentTimeMillis();

        try {
            // Step 1: Navigate to homepage
            executeStep(frontendUrl, frontendName, "Navigate to Homepage", () -> {
                driver.get(frontendUrl);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            });

            // Step 2: Check if page loads properly
            executeStep(frontendUrl, frontendName, "Check Page Load", () -> {
                String title = driver.getTitle();
                assertFalse(title.isEmpty(), "Page title should not be empty");
                System.out.println("   Page title: " + title);
            });

            // Step 3: Look for common elements
            executeStep(frontendUrl, frontendName, "Check Common Elements", () -> {
                // Tìm các element phổ biến
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                List<WebElement> links = driver.findElements(By.tagName("a"));
                List<WebElement> inputs = driver.findElements(By.tagName("input"));

                System.out.println("   Found " + buttons.size() + " buttons, " +
                        links.size() + " links, " + inputs.size() + " inputs");
            });

            // Step 4: Test navigation (if applicable)
            executeStep(frontendUrl, frontendName, "Test Navigation", () -> {
                List<WebElement> navLinks = driver.findElements(By.cssSelector("nav a, .nav a, .navbar a"));
                if (!navLinks.isEmpty()) {
                    System.out.println("   Found " + navLinks.size() + " navigation links");
                    // Click first nav link if exists
                    if (navLinks.size() > 0) {
                        String linkText = navLinks.get(0).getText();
                        navLinks.get(0).click();
                        System.out.println("   Clicked on: " + linkText);
                        // Sử dụng WebDriverWait thay vì Thread.sleep
                        waitForPageLoad();
                    }
                }
            });

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - testStartTime;
            addToCsvReport(frontendUrl, frontendName, "Basic Functionality", "ERROR", "", "",
                    duration, "FAILED", e.getMessage());
            throw e;
        }
    }

    private void executeCustomTestCases(String frontendUrl, String frontendName) throws IOException {
        // Load selenium11.json cases từ file JSON (nếu có)
        Path testCasesPath = Paths.get("src/selenium11.json/resources/selenium11.json-cases/external-frontend-tests.json");

        if (Files.exists(testCasesPath)) {
            String testCasesJson = new String(Files.readAllBytes(testCasesPath));
            JsonNode testCases = objectMapper.readTree(testCasesJson);

            for (JsonNode testCase : testCases) {
                String testName = testCase.get("name").asText();
                JsonNode steps = testCase.get("steps");

                System.out.println("   Executing: " + testName);

                for (JsonNode step : steps) {
                    executeCustomStep(frontendUrl, frontendName, testName, step);
                }
            }
        } else {
            // Nếu không có file JSON, chạy selenium11.json cases mặc định
            executeDefaultTestCases(frontendUrl, frontendName);
        }
    }

    private void executeDefaultTestCases(String frontendUrl, String frontendName) {
        // Test case 1: Homepage functionality
        executeStep(frontendUrl, frontendName, "Homepage Test", () -> {
            driver.get(frontendUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Tìm và selenium11.json các element phổ biến
            testCommonElements();
        });

        // Test case 2: Form interactions (nếu có)
        executeStep(frontendUrl, frontendName, "Form Interaction Test", () -> {
            List<WebElement> forms = driver.findElements(By.tagName("form"));
            if (!forms.isEmpty()) {
                System.out.println("   Found " + forms.size() + " forms");
                testFormInteractions();
            }
        });

        // Test case 3: Button clicks
        executeStep(frontendUrl, frontendName, "Button Click Test", () -> {
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            if (!buttons.isEmpty()) {
                System.out.println("   Testing button interactions");
                testButtonClicks(buttons);
            }
        });
    }

    private void testEcommerceFlow(String frontendUrl, String frontendName) {
        // Test flow cho e-commerce site
        executeStep(frontendUrl, frontendName, "Browse Products", () -> {
            driver.get(frontendUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Tìm products hoặc items
            List<WebElement> products = findProducts();
            assertTrue(products.size() > 0, "Should find products on the page");
            System.out.println("   Found " + products.size() + " products");
        });

        executeStep(frontendUrl, frontendName, "Search Functionality", () -> {
            // Tìm search box
            WebElement searchBox = findSearchBox();
            if (searchBox != null) {
                searchBox.clear();
                searchBox.sendKeys("selenium11.json");

                // Tìm search button
                WebElement searchButton = findSearchButton();
                if (searchButton != null) {
                    searchButton.click();
                    waitForPageLoad();
                    System.out.println("   Search functionality tested");
                }
            }
        });

        executeStep(frontendUrl, frontendName, "Product Details", () -> {
            // Click vào product đầu tiên (nếu có)
            List<WebElement> productLinks = findProductLinks();
            if (!productLinks.isEmpty()) {
                productLinks.get(0).click();
                waitForPageLoad();
                System.out.println("   Navigated to product details");
            }
        });
    }

    private void executeStep(String frontendUrl, String frontendName, String stepName, Runnable stepAction) {
        long stepStartTime = System.currentTimeMillis();
        boolean stepPassed = false;
        String errorMessage = null;

        try {
            System.out.println("   Step: " + stepName);
            stepAction.run();
            stepPassed = true;
            System.out.println("      ✅ " + stepName + " completed");
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println("      ❌ " + stepName + " failed: " + errorMessage);
            throw new RuntimeException(stepName + " failed", e);
        } finally {
            long duration = System.currentTimeMillis() - stepStartTime;
            addToCsvReport(frontendUrl, frontendName, stepName, "STEP", "", "",
                    duration, stepPassed ? "PASSED" : "FAILED", errorMessage);
        }
    }

    private void executeCustomStep(String frontendUrl, String frontendName, String testName, JsonNode step) {
        String action = step.get("action").asText();
        String selector = step.has("selector") ? step.get("selector").asText() : null;
        String value = step.has("value") ? step.get("value").asText() : null;
        String expectedText = step.has("expectedText") ? step.get("expectedText").asText() : null;

        executeStep(frontendUrl, frontendName, testName + " - " + action, () -> {
            switch (action) {
                case "navigate":
                    String url = step.get("url").asText();
                    driver.get(frontendUrl + url);
                    break;

                case "click":
                    WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    clickElement.click();
                    break;

                case "input":
                    WebElement inputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
                    inputElement.clear();
                    inputElement.sendKeys(value);
                    break;

                case "verify_text":
                    WebElement textElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
                    String actualText = textElement.getText();
                    assertTrue(actualText.contains(expectedText),
                            "Expected '" + expectedText + "' but found '" + actualText + "'");
                    break;

                case "verify_element_exists":
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                    break;

                case "wait":
                    // Sử dụng WebDriverWait thay vì Thread.sleep
                    int waitTime = Integer.parseInt(value);
                    waitWithTimeout(Duration.ofMillis(waitTime));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }
        });
    }

    // Helper methods để tìm elements phổ biến
    private void testCommonElements() {
        // Test header
        List<WebElement> headers = driver.findElements(By.cssSelector("header, .header, #header"));
        System.out.println("      Found " + headers.size() + " header elements");

        // Test navigation
        List<WebElement> navs = driver.findElements(By.cssSelector("nav, .nav, .navbar, #nav"));
        System.out.println("      Found " + navs.size() + " navigation elements");

        // Test footer
        List<WebElement> footers = driver.findElements(By.cssSelector("footer, .footer, #footer"));
        System.out.println("      Found " + footers.size() + " footer elements");
    }

    private void testFormInteractions() {
        List<WebElement> inputs = driver.findElements(By.cssSelector("input[type='text'], input[type='email']"));
        for (int i = 0; i < Math.min(inputs.size(), 3); i++) {
            try {
                WebElement input = inputs.get(i);
                if (input.isDisplayed() && input.isEnabled()) {
                    input.clear();
                    input.sendKeys("selenium11.json input");
                    System.out.println("      Tested input field " + (i + 1));
                }
            } catch (Exception e) {
                System.out.println("      Could not interact with input " + (i + 1));
            }
        }
    }

    private void testButtonClicks(List<WebElement> buttons) {
        for (int i = 0; i < Math.min(buttons.size(), 3); i++) {
            try {
                WebElement button = buttons.get(i);
                if (button.isDisplayed() && button.isEnabled()) {
                    String buttonText = button.getText();
                    // Tránh click vào các button nguy hiểm
                    if (!buttonText.toLowerCase().contains("delete") &&
                            !buttonText.toLowerCase().contains("remove") &&
                            !buttonText.toLowerCase().contains("submit")) {
                        button.click();
                        waitForPageLoad();
                        System.out.println("      Clicked button: " + buttonText);
                        driver.navigate().back(); // Go back after click
                        waitForPageLoad();
                    }
                }
            } catch (Exception e) {
                System.out.println("      Could not click button " + (i + 1));
            }
        }
    }

    private List<WebElement> findProducts() {
        // Tìm products với các selector phổ biến
        List<WebElement> products = driver.findElements(By.cssSelector(
                ".product, .item, .card, .product-card, .product-item, [class*='product'], [class*='item']"
        ));
        return products;
    }

    private WebElement findSearchBox() {
        try {
            return driver.findElement(By.cssSelector(
                    "input[type='search'], input[placeholder*='search'], input[name*='search'], #search, .search-input"
            ));
        } catch (Exception e) {
            return null;
        }
    }

    private WebElement findSearchButton() {
        try {
            return driver.findElement(By.cssSelector(
                    "button[type='submit'], .search-button, .search-btn, input[type='submit']"
            ));
        } catch (Exception e) {
            return null;
        }
    }

    private List<WebElement> findProductLinks() {
        return driver.findElements(By.cssSelector(
                ".product a, .item a, .product-link, .product-title a, [class*='product'] a"
        ));
    }

    // Utility methods để thay thế Thread.sleep
    private void waitForPageLoad() {
        try {
            // Đợi trang load hoàn thành
            wait.until(driver -> ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));

            // Đợi thêm một chút cho các elements dynamic
            waitWithTimeout(Duration.ofMillis(1000));
        } catch (Exception e) {
            // Fallback nếu JavaScript không hoạt động
            waitWithTimeout(Duration.ofMillis(2000));
        }
    }

    private void waitWithTimeout(Duration duration) {
        try {
            // Sử dụng WebDriverWait với empty condition để tạo delay
            new WebDriverWait(driver, duration).until(driver -> false);
        } catch (org.openqa.selenium.TimeoutException e) {
            // Expected timeout - this is our delay mechanism
        }
    }

    private void addToCsvReport(String frontendUrl, String testCase, String step, String action,
                                String selector, String expected, long duration, String result, String error) {
        csvReport.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                frontendUrl,
                testCase,
                step,
                action,
                selector != null ? selector : "",
                expected != null ? expected : "",
                duration,
                result,
                error != null ? error.replace("\"", "\"\"") : "",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }

    private void generateCsvReport() {
        try {
            Path reportsDir = Paths.get("target/selenium11.json-reports");
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "external-frontend-selenium11.json-report_" + timestamp + ".csv";
            File reportFile = new File(reportsDir.toFile(), filename);

            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(csvReport.toString());
                writer.flush();
            }

            System.out.println("📊 CSV Report generated: " + reportFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Failed to generate CSV report: " + e.getMessage());
        }
    }

    private void printTestSummary() {
        System.out.println("\n=== EXTERNAL FRONTEND TEST SUMMARY ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        if (totalTests > 0) {
            System.out.println("Success Rate: " + String.format("%.2f%%", (double) passedTests / totalTests * 100));
        }
        System.out.println("==========================================\n");
    }
}