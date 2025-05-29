import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time

class FacebookRegistrationTest(unittest.TestCase):
    def setUp(self):
        # Initialize Chrome WebDriver
        self.driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()))
        self.driver.implicitly_wait(10)
        # Navigate to registration page (replace with actual URL)
        self.base_url = "http://localhost/facebook/register"
        self.driver.get(self.base_url)
    
    def tearDown(self):
        # Close browser after each test
        self.driver.quit()
    
    def test_TC01_verify_ui_elements(self):
        """TC01: Verify registration page UI elements"""
        # Check logo
        logo = self.driver.find_element(By.CSS_SELECTOR, "img[alt='facebook logo']")
        self.assertTrue(logo.is_displayed(), "Logo not displayed")
        
        # Check title
        title = self.driver.find_element(By.XPATH, "//h1[contains(text(), 'Create a new account')]")
        self.assertTrue(title.is_displayed(), "Title not displayed")
        
        # Check input fields
        fields = ["first_name", "surname", "mobile_email", "password"]
        for field_id in fields:
            element = self.driver.find_element(By.ID, field_id)
            self.assertTrue(element.is_displayed(), f"Field {field_id} not displayed")
        
        # Check Date of Birth dropdowns
        dob_fields = ["day", "month", "year"]
        for field_id in dob_fields:
            element = self.driver.find_element(By.ID, field_id)
            self.assertTrue(element.is_displayed(), f"DoB field {field_id} not displayed")
        
        # Check Gender radio buttons
        gender = self.driver.find_element(By.NAME, "gender")
        self.assertTrue(gender.is_displayed(), "Gender field not displayed")
        
        # Check Sign Up button
        signup_button = self.driver.find_element(By.NAME, "signup")
        self.assertTrue(signup_button.is_displayed(), "Sign Up button not displayed")
    
    def test_TC02_first_name_mandatory(self):
        """TC02: Verify First Name is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "first_name_error")
        self.assertTrue(error_icon.is_displayed(), "First Name error icon not displayed")
    
    def test_TC03_surname_mandatory(self):
        """TC03: Verify Surname is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "surname_error")
        self.assertTrue(error_icon.is_displayed(), "Surname error icon not displayed")
    
    def test_TC04_dob_mandatory(self):
        """TC04: Verify Date of Birth is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "dob_error")
        self.assertTrue(error_icon.is_displayed(), "DoB error icon not displayed")
    
    def test_TC05_age_under_13_invalid(self):
        """TC05: Verify age under 13 is invalid"""
        Select(self.driver.find_element(By.ID, "day")).select_by_value("1")
        Select(self.driver.find_element(By.ID, "month")).select_by_value("January")
        Select(self.driver.find_element(By.ID, "year")).select_by_value("2020")
        self.driver.find_element(By.NAME, "signup").click()
        error_msg = self.driver.find_element(By.ID, "year_error")
        self.assertEqual(error_msg.text, "Must be at least 13 years old.", "Age error message not displayed")
    
    def test_TC06_age_over_13_valid(self):
        """TC06: Verify age over 13 is valid"""
        Select(self.driver.find_element(By.ID, "day")).select_by_value("1")
        Select(self.driver.find_element(By.ID, "month")).select_by_value("January")
        Select(self.driver.find_element(By.ID, "year")).select_by_value("2000")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "dob_error")
        self.assertEqual(len(error_icon), 0, "DoB error icon displayed unexpectedly")
    
    def test_TC07_gender_mandatory(self):
        """TC07: Verify Gender is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "gender_error")
        self.assertTrue(error_icon.is_displayed(), "Gender error icon not displayed")
    
    def test_TC08_gender_valid(self):
        """TC08: Verify valid Gender selection"""
        self.driver.find_element(By.XPATH, "//input[@value='Female']").click()
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "gender_error")
        self.assertEqual(len(error_icon), 0, "Gender error icon displayed unexpectedly")
    
    def test_TC09_mobile_email_mandatory(self):
        """TC09: Verify Mobile/Email is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "mobile_email_error")
        self.assertTrue(error_icon.is_displayed(), "Mobile/Email error icon not displayed")
    
    def test_TC10_password_mandatory(self):
        """TC10: Verify Password is mandatory"""
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "password_error")
        self.assertTrue(error_icon.is_displayed(), "Password error icon not displayed")
    
    def test_TC11_password_under_8_chars_invalid(self):
        """TC11: Verify password under 8 characters is invalid"""
        self.driver.find_element(By.ID, "password").send_keys("pass123")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "password_error")
        self.assertTrue(error_icon.is_displayed(), "Password error icon not displayed")
    
    def test_TC12_password_8_chars_valid(self):
        """TC12: Verify password 8 or more characters is valid"""
        self.driver.find_element(By.ID, "password").send_keys("password123")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "password_error")
        self.assertEqual(len(error_icon), 0, "Password error icon displayed unexpectedly")
    
    def test_TC13_successful_registration(self):
        """TC13: Verify successful registration with valid data"""
        self.driver.find_element(By.ID, "first_name").send_keys("John")
        self.driver.find_element(By.ID, "surname").send_keys("Doe")
        Select(self.driver.find_element(By.ID, "day")).select_by_value("1")
        Select(self.driver.find_element(By.ID, "month")).select_by_value("January")
        Select(self.driver.find_element(By.ID, "year")).select_by_value("1990")
        self.driver.find_element(By.XPATH, "//input[@value='Male']").click()
        self.driver.find_element(By.ID, "mobile_email").send_keys("john@example.com")
        self.driver.find_element(By.ID, "password").send_keys("password123")
        self.driver.find_element(By.NAME, "signup").click()
        snackbar = self.driver.find_element(By.ID, "snackbar")
        self.assertEqual(snackbar.text, "Account created successfully!", "Success message not displayed")
    
    def test_TC16_invalid_email(self):
        """TC16: Verify invalid email format"""
        self.driver.find_element(By.ID, "mobile_email").send_keys("invalid_email")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "mobile_email_error")
        self.assertTrue(error_icon.is_displayed(), "Email error icon not displayed")
    
    def test_TC17_invalid_phone(self):
        """TC17: Verify invalid phone number"""
        self.driver.find_element(By.ID, "mobile_email").send_keys("123")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_element(By.ID, "mobile_email_error")
        self.assertTrue(error_icon.is_displayed(), "Phone error icon not displayed")
    
    def test_TC19_special_chars_first_name(self):
        """TC19: Verify special characters in First Name (accepted per current code)"""
        self.driver.find_element(By.ID, "first_name").send_keys("@John#")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "first_name_error")
        self.assertEqual(len(error_icon), 0, "First Name error icon displayed unexpectedly")
    
    def test_TC20_special_chars_surname(self):
        """TC20: Verify special characters in Surname (accepted per current code)"""
        self.driver.find_element(By.ID, "surname").send_keys("@Doe#")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "surname_error")
        self.assertEqual(len(error_icon), 0, "Surname error icon displayed unexpectedly")
    
    def test_TC26_valid_email(self):
        """TC26: Verify valid email format"""
        self.driver.find_element(By.ID, "mobile_email").send_keys("test.user@example.com")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "mobile_email_error")
        self.assertEqual(len(error_icon), 0, "Email error icon displayed unexpectedly")
    
    def test_TC27_valid_phone(self):
        """TC27: Verify valid phone number"""
        self.driver.find_element(By.ID, "mobile_email").send_keys("+84912345678")
        self.driver.find_element(By.NAME, "signup").click()
        error_icon = self.driver.find_elements(By.ID, "mobile_email_error")
        self.assertEqual(len(error_icon), 0, "Phone error icon displayed unexpectedly")

if __name__ == "__main__":
    unittest.main()