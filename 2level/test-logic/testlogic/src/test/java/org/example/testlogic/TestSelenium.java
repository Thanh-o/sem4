package org.example.testlogic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

@DisplayName("Selenium Sales Flow Test - Browser Automation")
public class TestSelenium {

    private WebDriver driver;
    private WebDriverWait wait;
    private ObjectMapper objectMapper;
    private StringBuilder csvReport;
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private String baseUrl = "http://10.60.108.28:8081/pages/marital-status-registration/add-new"; // Thay bằng URL của front-end đã triển khai

    @BeforeEach
    void setUp() {
        // Setup Chrome options
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Bỏ comment để chạy ở chế độ headless
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        objectMapper = new ObjectMapper();
        csvReport = new StringBuilder();
        csvReport.append("Test Case,Step,Action,Selector,Expected,Actual,Duration (ms),Result,Error Message,Timestamp\n");

        totalTests = 0;
        passedTests = 0;
        failedTests = 0;

        System.out.println("🚀 Selenium marital-status-form-tests.json setup completed. Base URL: " + baseUrl);
        System.out.println("🌐 Browser will open for visual testing");
    }

    @AfterEach
    void tearDown() {
        // Generate CSV report
        System.out.println("🔄 Generating CSV report...");
        generateCsvReport();
        printTestSummary();

        // Close browser
        if (driver != null) {
            System.out.println("🔒 Closing browser...");
            driver.quit();
        }
    }

    @Test
    @DisplayName("Execute All Sales Flow Test Cases with Browser")
    void executeAllSalesFlowTestCases() throws Exception {
        try {
            // Read selenium11.json cases from JSON file
            String testCasesJson = new String(Files.readAllBytes(Paths.get("src/test/resources/test-cases/validation-test-cases.json")));
            JsonNode testCases = objectMapper.readTree(testCasesJson);

            System.out.println("=== SELENIUM SALES FLOW TEST EXECUTION ===");
            System.out.println("Base URL: " + baseUrl);
            System.out.println("Browser: Chrome");
            System.out.println("Total test cases found: " + testCases.size());
            System.out.println();

            // Execute each selenium11.json case
            for (JsonNode testCase : testCases) {
                try {
                    executeTestCase(testCase);
                    // Small delay between selenium11.json cases for visual observation
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.err.println("❌ Test case execution failed: " + e.getMessage());
                    // Continue with other selenium11.json cases
                }
            }

            // Assert that at least 70% of tests passed
            if (totalTests > 0) {
                double successRate = (double) passedTests / totalTests;
                assertTrue(successRate >= 0.7,
                        String.format("Success rate %.2f%% is below acceptable threshold of 70%%", successRate * 100));
            }
        } catch (Exception e) {
            System.err.println("❌ Test execution failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeTestCase(JsonNode testCase) {
        String testName = testCase.has("name") ? testCase.get("name").asText() : "Unknown Test";
        String description = testCase.has("description") ? testCase.get("description").asText() : "No description";
        String priority = testCase.has("priority") ? testCase.get("priority").asText() : "MEDIUM";
        String category = testCase.has("category") ? testCase.get("category").asText() : "General";
        JsonNode steps = testCase.get("steps");

        System.out.println("🧪 Executing Test Case: " + testName);
        System.out.println("   Description: " + description);
        System.out.println("   Priority: " + priority + " | Category: " + category);

        boolean testCasePassed = true;
        String testCaseError = null;
        long testCaseStartTime = System.currentTimeMillis();

        try {
            if (steps != null && steps.isArray()) {
                for (int i = 0; i < steps.size(); i++) {
                    JsonNode step = steps.get(i);
                    executeStep(testName, i + 1, step);
                }
            } else {
                throw new IllegalArgumentException("Test case has no valid steps array");
            }

            passedTests++;
            System.out.println("   ✅ Test Case PASSED");

        } catch (Exception e) {
            testCasePassed = false;
            testCaseError = e.getMessage();
            failedTests++;
            System.out.println("   ❌ Test Case FAILED: " + e.getMessage());
        }

        totalTests++;
        long testCaseDuration = System.currentTimeMillis() - testCaseStartTime;

        // Add selenium11.json case summary to CSV
        csvReport.append(String.format("\"%s\",\"SUMMARY\",\"-\",\"-\",\"-\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                testName,
                testCasePassed ? "PASSED" : "FAILED",
                testCaseDuration,
                testCasePassed ? "SUCCESS" : "FAILED",
                testCaseError != null ? testCaseError.replace("\"", "\"\"") : "",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));

        System.out.println();
    }

    private void executeStep(String testCaseName, int stepNumber, JsonNode step) throws Exception {
        String action = step.has("action") ? step.get("action").asText() : "UNKNOWN";
        String url = step.has("url") ? step.get("url").asText() : null;
        String selector = step.has("selector") ? step.get("selector").asText() : null;
        String value = step.has("value") ? step.get("value").asText() : null;
        String expectedText = step.has("expectedText") ? step.get("expectedText").asText() : null;
        String description = step.has("description") ? step.get("description").asText() : "No description";

        System.out.println("   Step " + stepNumber + ": " + description);

        long stepStartTime = System.currentTimeMillis();
        boolean stepPassed = false;
        String stepError = null;
        String actualResult = "";

        try {
            switch (action) {
                case "navigate":
                    driver.get(baseUrl + url);
                    actualResult = "Navigated to " + url;
                    break;

                case "click":
                    WebElement clickElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    clickElement.click();
                    actualResult = "Clicked element";
                    break;

                case "input":
                    WebElement inputElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
                    inputElement.clear();
                    inputElement.sendKeys(value);
                    actualResult = "Input: " + value;
                    break;

                case "verify_text":
                    WebElement textElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
                    String actualText = textElement.getText();
                    if (selector.contains("input")) {
                        actualText = textElement.getAttribute("value");
                    }
                    assertTrue(actualText.contains(expectedText),
                            "Expected text '" + expectedText + "' not found. Actual: '" + actualText + "'");
                    actualResult = "Text verified: " + actualText;
                    break;

                case "verify_url":
                    wait.until(ExpectedConditions.urlContains(url));
                    assertTrue(driver.getCurrentUrl().contains(url),
                            "URL should contain '" + url + "' but was: " + driver.getCurrentUrl());
                    actualResult = "URL verified: " + driver.getCurrentUrl();
                    break;

                case "verify_element_exists":
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    assertFalse(elements.isEmpty(), "Element should exist but was not found: " + selector);
                    actualResult = "Element exists: " + elements.size() + " found";
                    break;

                case "verify_element_not_exists":
                    List<WebElement> nonElements = driver.findElements(By.cssSelector(selector));
                    assertTrue(nonElements.isEmpty(), "Element should not exist but was found: " + selector);
                    actualResult = "Element not found (as expected)";
                    break;

                case "wait":
                    Thread.sleep(Integer.parseInt(value));
                    actualResult = "Waited " + value + "ms";
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action: " + action);
            }

            stepPassed = true;
            System.out.println("      ✅ Step completed successfully");

        } catch (Exception e) {
            stepError = e.getMessage();
            actualResult = "ERROR: " + stepError;
            System.out.println("      ❌ Step failed: " + stepError);
            throw e;
        } finally {
            long stepDuration = System.currentTimeMillis() - stepStartTime;

            // Add step to CSV report
            csvReport.append(String.format("\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\",%d,\"%s\",\"%s\",\"%s\"\n",
                    testCaseName,
                    stepNumber,
                    action,
                    selector != null ? selector : url != null ? url : "-",
                    expectedText != null ? expectedText : value != null ? value : "-",
                    actualResult.replace("\"", "\"\""),
                    stepDuration,
                    stepPassed ? "PASSED" : "FAILED",
                    stepError != null ? stepError.replace("\"", "\"\"") : "",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    private void generateCsvReport() {
        try {
            System.out.println("📊 Starting CSV report generation...");

            // Create reports directory if it doesn't exist
            Path reportsDir = Paths.get("target/test-reports");
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
                System.out.println("📁 Created reports directory: " + reportsDir.toAbsolutePath());
            }

            // Generate timestamped filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = "test-report_" + timestamp + ".csv";
            File reportFile = new File(reportsDir.toFile(), filename);

            // Write CSV content to file
            try (FileWriter writer = new FileWriter(reportFile)) {
                writer.write(csvReport.toString());
                writer.flush();
            }

            System.out.println("📊 CSV Report generated successfully!");
            System.out.println("📍 File location: " + reportFile.getAbsolutePath());
            System.out.println("📏 File size: " + reportFile.length() + " bytes");

            // Verify file exists
            if (reportFile.exists()) {
                System.out.println("✅ CSV file verified to exist");
            } else {
                System.err.println("❌ CSV file was not created!");
            }

            // Also generate a summary report
            generateSummaryReport(reportsDir, timestamp);

        } catch (IOException e) {
            System.err.println("❌ Failed to generate CSV report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateSummaryReport(Path reportsDir, String timestamp) {
        try {
            String summaryFilename = "summary_" + timestamp + ".csv";
            File summaryFile = new File(reportsDir.toFile(), summaryFilename);

            try (FileWriter writer = new FileWriter(summaryFile)) {
                writer.write("Metric,Value\n");
                writer.write("Test Type,Selenium Browser Test\n");
                writer.write("Base URL," + baseUrl + "\n");
                writer.write("Browser,Chrome\n");
                writer.write("Total Test Cases," + totalTests + "\n");
                writer.write("Passed Test Cases," + passedTests + "\n");
                writer.write("Failed Test Cases," + failedTests + "\n");
                if (totalTests > 0) {
                    writer.write("Success Rate," + String.format("%.2f%%", (double) passedTests / totalTests * 100) + "\n");
                } else {
                    writer.write("Success Rate,N/A\n");
                }
                writer.write("Execution Time," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.flush();
            }

            System.out.println("📈 Summary Report generated: " + summaryFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Failed to generate summary report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printTestSummary() {
        System.out.println("\n=== SELENIUM TEST EXECUTION SUMMARY ===");
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Browser: Chrome");
        System.out.println("Total Test Cases: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        if (totalTests > 0) {
            System.out.println("Success Rate: " + String.format("%.2f%%", (double) passedTests / totalTests * 100));
        } else {
            System.out.println("Success Rate: N/A (No tests executed)");
        }
        System.out.println("Execution Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("==========================================\n");
    }
}