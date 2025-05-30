from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
import time

def test_facebook_signup():
    # Initialize the WebDriver (using Chrome; ensure chromedriver is installed)
    driver = webdriver.Chrome()
    driver.maximize_window()

    try:
        # Navigate to the Flutter web app URL (replace with your actual URL)
        driver.get("http://localhost:63063")  # Adjust URL as needed

        # Wait for the page to load
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//*[contains(text(), 'Create a new account')]"))
        )

        # Fill in First Name
        first_name_field = driver.find_element(By.XPATH, "//input[@placeholder='First name']")
        first_name_field.send_keys("John")

        # Fill in Surname
        surname_field = driver.find_element(By.XPATH, "//input[@placeholder='Surname']")
        surname_field.send_keys("Doe")

        # Select Date of Birth
        # Day dropdown
        day_dropdown = Select(driver.find_element(By.XPATH, "//select[@placeholder='Day']"))
        day_dropdown.select_by_visible_text("15")

        # Month dropdown
        month_dropdown = Select(driver.find_element(By.XPATH, "//select[@placeholder='Month']"))
        month_dropdown.select_by_visible_text("Jan")

        # Year dropdown
        year_dropdown = Select(driver.find_element(By.XPATH, "//select[@placeholder='Year']"))
        year_dropdown.select_by_visible_text("1995")

        # Select Gender (e.g., Male)
        gender_radio = driver.find_element(By.XPATH, "//input[@type='radio' and @value='Male']")
        gender_radio.click()

        # Fill in Mobile number or email
        mobile_email_field = driver.find_element(By.XPATH, "//input[@placeholder='Mobile number or email']")
        mobile_email_field.send_keys("john.doe@example.com")

        # Fill in Password
        password_field = driver.find_element(By.XPATH, "//input[@placeholder='New password']")
        password_field.send_keys("SecurePass123")

        # Click Sign Up button
        signup_button = driver.find_element(By.XPATH, "//button[contains(text(), 'Sign Up')]")
        signup_button.click()

        # Wait for and verify the success message (SnackBar)
        success_message = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//*[contains(text(), 'Account created successfully')]"))
        )
        assert "Account created successfully" in success_message.text, "Success message not found"
        print("Test passed: Account created successfully")

    except Exception as e:
        print(f"Test failed: {str(e)}")

    finally:
        # Wait briefly to observe the result
        time.sleep(2)
        # Clean up
        driver.quit()

if __name__ == "__main__":
    test_facebook_signup()