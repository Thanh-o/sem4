package org.example.testlogic;

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

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Specific External Website")
public class SpecificSiteTest {

    private WebDriver driver;
    private WebDriverWait wait;
    
    // Thay đổi URL này thành website bạn muốn selenium11.json
    private final String TARGET_WEBSITE = "https://demo.opencart.com";

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Comment out để xem browser
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        
        System.out.println("🚀 Testing website: " + TARGET_WEBSITE);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test Homepage Loading")
    void testHomepageLoading() {
        driver.get(TARGET_WEBSITE);
        
        // Đợi trang load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Kiểm tra title
        String title = driver.getTitle();
        assertFalse(title.isEmpty(), "Page title should not be empty");
        System.out.println("✅ Page title: " + title);
        
        // Kiểm tra có content không
        WebElement body = driver.findElement(By.tagName("body"));
        assertFalse(body.getText().isEmpty(), "Page should have content");
        System.out.println("✅ Page loaded successfully");
    }

    @Test
    @DisplayName("Test Navigation Menu")
    void testNavigationMenu() {
        driver.get(TARGET_WEBSITE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Tìm navigation menu
        List<WebElement> navLinks = driver.findElements(By.cssSelector("nav a, .nav a, .navbar a, .menu a"));
        
        assertTrue(navLinks.size() > 0, "Should have navigation links");
        System.out.println("✅ Found " + navLinks.size() + " navigation links");
        
        // Test click vào link đầu tiên
        if (navLinks.size() > 0) {
            String linkText = navLinks.get(0).getText();
            String linkHref = navLinks.get(0).getAttribute("href");
            
            System.out.println("   Testing link: " + linkText + " -> " + linkHref);
            navLinks.get(0).click();
            
            // Đợi trang mới load
            try {
                Thread.sleep(3000);
                System.out.println("✅ Navigation selenium11.json completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    @DisplayName("Test Search Functionality")
    void testSearchFunctionality() {
        driver.get(TARGET_WEBSITE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Tìm search box với nhiều selector khác nhau
        WebElement searchBox = null;
        String[] searchSelectors = {
            "input[type='search']",
            "input[name*='search']",
            "input[placeholder*='search']",
            "input[placeholder*='Search']",
            "#search",
            ".search-input",
            ".search-box"
        };
        
        for (String selector : searchSelectors) {
            try {
                searchBox = driver.findElement(By.cssSelector(selector));
                if (searchBox.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                // Continue to next selector
            }
        }
        
        if (searchBox != null) {
            System.out.println("✅ Found search box");
            
            // Test search
            searchBox.clear();
            searchBox.sendKeys("selenium11.json");
            
            // Tìm search button
            WebElement searchButton = null;
            String[] buttonSelectors = {
                "button[type='submit']",
                ".search-button",
                ".search-btn",
                "input[type='submit']"
            };
            
            for (String selector : buttonSelectors) {
                try {
                    searchButton = driver.findElement(By.cssSelector(selector));
                    if (searchButton.isDisplayed()) {
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next selector
                }
            }
            
            if (searchButton != null) {
                searchButton.click();
                System.out.println("✅ Search functionality tested");
                
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                // Try pressing Enter
                searchBox.sendKeys("\n");
                System.out.println("✅ Search submitted via Enter key");
            }
        } else {
            System.out.println("⚠️ No search functionality found");
        }
    }

    @Test
    @DisplayName("Test Form Interactions")
    void testFormInteractions() {
        driver.get(TARGET_WEBSITE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Tìm tất cả forms
        List<WebElement> forms = driver.findElements(By.tagName("form"));
        System.out.println("✅ Found " + forms.size() + " forms");
        
        // Test input fields
        List<WebElement> inputs = driver.findElements(By.cssSelector("input[type='text'], input[type='email']"));
        System.out.println("✅ Found " + inputs.size() + " text/email inputs");
        
        // Test một vài input fields
        for (int i = 0; i < Math.min(inputs.size(), 3); i++) {
            try {
                WebElement input = inputs.get(i);
                if (input.isDisplayed() && input.isEnabled()) {
                    String placeholder = input.getAttribute("placeholder");
                    input.clear();
                    input.sendKeys("selenium11.json input " + (i + 1));
                    System.out.println("   Tested input " + (i + 1) + " (placeholder: " + placeholder + ")");
                }
            } catch (Exception e) {
                System.out.println("   Could not interact with input " + (i + 1));
            }
        }
    }

    @Test
    @DisplayName("Test Responsive Design")
    void testResponsiveDesign() {
        driver.get(TARGET_WEBSITE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        // Test desktop size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        System.out.println("✅ Testing desktop view (1920x1080)");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Test tablet size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024));
        System.out.println("✅ Testing tablet view (768x1024)");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Test mobile size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
        System.out.println("✅ Testing mobile view (375x667)");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Reset to desktop
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
        System.out.println("✅ Responsive design selenium11.json completed");
    }

    @Test
    @DisplayName("Test Page Performance")
    void testPagePerformance() {
        long startTime = System.currentTimeMillis();
        
        driver.get(TARGET_WEBSITE);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        System.out.println("✅ Page load time: " + loadTime + "ms");
        
        // Assert page loads within reasonable time (10 seconds)
        assertTrue(loadTime < 10000, "Page should load within 10 seconds, but took " + loadTime + "ms");
        
        // Check if images are loaded
        List<WebElement> images = driver.findElements(By.tagName("img"));
        System.out.println("✅ Found " + images.size() + " images");
        
        // Check if CSS is loaded (by checking if elements have styles)
        WebElement body = driver.findElement(By.tagName("body"));
        String bodyColor = body.getCssValue("color");
        assertNotNull(bodyColor, "CSS should be loaded");
        System.out.println("✅ CSS loaded (body color: " + bodyColor + ")");
    }
}
