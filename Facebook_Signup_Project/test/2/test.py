# import pytest
import csv
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import Select
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import os
from datetime import datetime

class FacebookSignUpTest:
    def __init__(self):
        self.driver = None
        self.wait = None
        self.test_results = []
        
    def setup_driver(self):
        """Setup Chrome WebDriver with options"""
        chrome_options = Options()
        chrome_options.add_argument("--no-sandbox")
        chrome_options.add_argument("--disable-dev-shm-usage")
        chrome_options.add_argument("--disable-gpu")
        chrome_options.add_argument("--window-size=1920,1080")
        # chrome_options.add_argument("--headless")  # Uncomment for headless mode
        
        # Update this path to your ChromeDriver location
        # service = Service('/path/to/chromedriver')
        # self.driver = webdriver.Chrome(service=service, options=chrome_options)
        
        self.driver = webdriver.Chrome(options=chrome_options)
        self.driver.maximize_window()
        self.wait = WebDriverWait(self.driver, 10)
    
    def teardown_driver(self):
        """Close the browser"""
        if self.driver:
            self.driver.quit()
    
    def load_test_cases(self, csv_file):
        """Load test cases from CSV file"""
        test_cases = []
        try:
            with open(csv_file, 'r', encoding='utf-8') as file:
                csv_reader = csv.DictReader(file)
                for row in csv_reader:
                    test_cases.append(row)
        except FileNotFoundError:
            print(f"Error: CSV file '{csv_file}' not found!")
            return []
        return test_cases
    
    def open_signup_page(self, file_path):
        """Open the sign up HTML file"""
        try:
            # Convert relative path to absolute path
            abs_path = os.path.abspath(file_path)
            self.driver.get(f"file://{abs_path}")
            self.wait.until(EC.presence_of_element_located((By.ID, "signupForm")))
            return True
        except TimeoutException:
            print("Error: Could not load the signup page")
            return False
    
    def clear_form(self):
        """Clear all form fields"""
        try:
            # Clear text inputs
            self.driver.find_element(By.ID, "firstName").clear()
            self.driver.find_element(By.ID, "surname").clear()
            self.driver.find_element(By.ID, "contact").clear()
            self.driver.find_element(By.ID, "password").clear()
            
            # Reset dropdowns
            Select(self.driver.find_element(By.ID, "dobDay")).select_by_index(0)
            Select(self.driver.find_element(By.ID, "dobMonth")).select_by_index(0)
            Select(self.driver.find_element(By.ID, "dobYear")).select_by_index(0)
            
            # Clear radio buttons
            gender_radios = self.driver.find_elements(By.NAME, "gender")
            for radio in gender_radios:
                if radio.is_selected():
                    # Click to deselect (though HTML5 radio buttons can't be truly deselected)
                    pass
                    
        except Exception as e:
            print(f"Warning: Could not clear some form fields: {e}")
    
    def fill_form(self, test_data):
        """Fill the signup form with test data"""
        try:
            # Fill first name
            if test_data['First_Name']:
                first_name_field = self.driver.find_element(By.ID, "firstName")
                first_name_field.clear()
                first_name_field.send_keys(test_data['First_Name'])
            
            # Fill surname
            if test_data['Surname']:
                surname_field = self.driver.find_element(By.ID, "surname")
                surname_field.clear()
                surname_field.send_keys(test_data['Surname'])
            
            # Select day
            if test_data['Day']:
                day_select = Select(self.driver.find_element(By.ID, "dobDay"))
                day_select.select_by_visible_text(test_data['Day'])
            
            # Select month
            if test_data['Month']:
                month_select = Select(self.driver.find_element(By.ID, "dobMonth"))
                month_select.select_by_visible_text(test_data['Month'])
            
            # Select year
            if test_data['Year']:
                year_select = Select(self.driver.find_element(By.ID, "dobYear"))
                year_select.select_by_visible_text(test_data['Year'])
            
            # Select gender
            if test_data['Gender']:
                gender_value = test_data['Gender'].lower()
                if gender_value in ['male', 'female', 'custom']:
                    gender_radio = self.driver.find_element(By.ID, f"gender{test_data['Gender']}")
                    gender_radio.click()
            
            # Fill contact
            if test_data['Contact']:
                contact_field = self.driver.find_element(By.ID, "contact")
                contact_field.clear()
                contact_field.send_keys(test_data['Contact'])
            
            # Fill password
            if test_data['Password']:
                password_field = self.driver.find_element(By.ID, "password")
                password_field.clear()
                password_field.send_keys(test_data['Password'])
            
            return True
            
        except Exception as e:
            print(f"Error filling form: {e}")
            return False
    
    def submit_form(self):
        """Submit the form and check for validation"""
        try:
            submit_button = self.driver.find_element(By.ID, "signUpBtn")
            submit_button.click()
            time.sleep(1)  # Wait for any validation to occur
            return True
        except Exception as e:
            print(f"Error submitting form: {e}")
            return False
    
    def check_form_validation(self):
        """Check if form validation occurred"""
        try:
            # Check for HTML5 validation messages
            invalid_fields = self.driver.find_elements(By.CSS_SELECTOR, ":invalid")
            if invalid_fields:
                return False, f"Invalid fields found: {len(invalid_fields)}"
            
            # Check if form is still on the same page (validation failed)
            current_url = self.driver.current_url
            if "sign_up.html" in current_url or current_url.startswith("file://"):
                # Check for any visible error messages
                error_elements = self.driver.find_elements(By.CSS_SELECTOR, ".error, .alert-danger, [role='alert']")
                if error_elements:
                    return False, "Error messages displayed"
                
                # If no visible errors but still on same page, might be HTML5 validation
                required_fields = self.driver.find_elements(By.CSS_SELECTOR, "[required]")
                for field in required_fields:
                    if not field.get_attribute("value"):
                        return False, f"Required field empty: {field.get_attribute('name') or field.get_attribute('id')}"
                
                return True, "Form validation passed"
            else:
                return True, "Form submitted successfully (redirected)"
                
        except Exception as e:
            return False, f"Error checking validation: {e}"
    
    def run_single_test(self, test_case):
        """Run a single test case"""
        test_id = test_case['TestCase_ID']
        expected_result = test_case['Expected_Result'].lower()
        
        print(f"\nRunning {test_id}: {test_case['Test_Description']}")
        
        try:
            # Clear and fill form
            self.clear_form()
            if not self.fill_form(test_case):
                return {'test_id': test_id, 'status': 'FAIL', 'message': 'Failed to fill form'}
            
            # Submit form
            if not self.submit_form():
                return {'test_id': test_id, 'status': 'FAIL', 'message': 'Failed to submit form'}
            
            # Check validation
            validation_passed, validation_message = self.check_form_validation()
            
            # Determine test result
            if expected_result == 'success':
                if validation_passed:
                    status = 'PASS'
                    message = 'Test passed as expected'
                else:
                    status = 'FAIL'
                    message = f'Expected success but got: {validation_message}'
            else:  # expected_result == 'error'
                if not validation_passed:
                    status = 'PASS'
                    message = 'Test passed - validation error detected as expected'
                else:
                    status = 'FAIL'
                    message = 'Expected validation error but form was accepted'
            
            return {
                'test_id': test_id,
                'status': status,
                'message': message,
                'validation_message': validation_message
            }
            
        except Exception as e:
            return {
                'test_id': test_id,
                'status': 'ERROR',
                'message': f'Test execution error: {str(e)}'
            }
    
    def run_all_tests(self, csv_file, html_file):
        """Run all test cases from CSV file"""
        print("Starting Facebook Sign Up Form Test Automation")
        print("=" * 50)
        
        # Setup
        self.setup_driver()
        
        try:
            # Load test cases
            test_cases = self.load_test_cases(csv_file)
            if not test_cases:
                print("No test cases loaded. Exiting.")
                return
            
            print(f"Loaded {len(test_cases)} test cases")
            
            # Open signup page
            if not self.open_signup_page(html_file):
                print("Failed to open signup page. Exiting.")
                return
            
            # Run tests
            passed_tests = 0
            failed_tests = 0
            error_tests = 0
            
            for i, test_case in enumerate(test_cases, 1):
                result = self.run_single_test(test_case)
                self.test_results.append(result)
                
                print(f"[{i}/{len(test_cases)}] {result['test_id']}: {result['status']} - {result['message']}")
                
                if result['status'] == 'PASS':
                    passed_tests += 1
                elif result['status'] == 'FAIL':
                    failed_tests += 1
                else:
                    error_tests += 1
                
                # Small delay between tests
                time.sleep(0.5)
            
            # Print summary
            print("\n" + "=" * 50)
            print("TEST EXECUTION SUMMARY")
            print("=" * 50)
            print(f"Total Tests: {len(test_cases)}")
            print(f"Passed: {passed_tests}")
            print(f"Failed: {failed_tests}")
            print(f"Errors: {error_tests}")
            print(f"Pass Rate: {(passed_tests/len(test_cases)*100):.1f}%")
            
            # Save results
            self.save_results()
            
        finally:
            self.teardown_driver()
    
    def save_results(self):
        """Save test results to CSV file"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"test_results_{timestamp}.csv"
        
        try:
            with open(filename, 'w', newline='', encoding='utf-8') as file:
                fieldnames = ['test_id', 'status', 'message', 'validation_message', 'timestamp']
                writer = csv.DictWriter(file, fieldnames=fieldnames)
                
                writer.writeheader()
                for result in self.test_results:
                    result['timestamp'] = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                    writer.writerow(result)
            
            print(f"\nTest results saved to: {filename}")
            
        except Exception as e:
            print(f"Error saving results: {e}")

def main():
    """Main execution function"""
    # File paths - update these to match your file locations
    CSV_FILE = "test_case.csv"  # Path to your test cases CSV
    HTML_FILE = "sign_up.html"           # Path to your HTML file
    
    # Initialize and run tests
    test_runner = FacebookSignUpTest()
    test_runner.run_all_tests(CSV_FILE, HTML_FILE)

if __name__ == "__main__":
    main()

# Additional utility functions for specific test scenarios

class AdvancedTestScenarios:
    """Additional test scenarios for comprehensive testing"""
    
    def __init__(self, driver):
        self.driver = driver
        self.wait = WebDriverWait(driver, 10)
    
    def test_javascript_disabled(self):
        """Test form behavior with JavaScript disabled"""
        # This would require running Chrome with --disable-javascript
        pass
    
    def test_mobile_responsiveness(self):
        """Test form on mobile viewport"""
        self.driver.set_window_size(375, 667)  # iPhone 6/7/8 size
        # Add mobile-specific test logic here
    
    def test_accessibility_features(self):
        """Test keyboard navigation and screen reader compatibility"""
        # Test tab navigation
        first_name = self.driver.find_element(By.ID, "firstName")
        first_name.send_keys(Keys.TAB)
        # Add more accessibility tests
    
    def test_performance(self):
        """Test form loading and submission performance"""
        start_time = time.time()
        # Measure form load time, validation time, etc.
        load_time = time.time() - start_time
        return load_time

# Example usage for running specific test cases
def run_specific_tests():
    """Example of running specific test cases"""
    test_runner = FacebookSignUpTest()
    test_runner.setup_driver()
    
    try:
        test_runner.open_signup_page("sign_up.html")
        
        # Example: Test only positive test cases
        positive_test = {
            'TestCase_ID': 'TC_MANUAL_001',
            'Test_Description': 'Manual positive test',
            'First_Name': 'John',
            'Surname': 'Doe',
            'Day': '15',
            'Month': 'Jan',
            'Year': '1990',
            'Gender': 'Male',
            'Contact': 'john.doe@email.com',
            'Password': 'password123',
            'Expected_Result': 'Success'
        }
        
        result = test_runner.run_single_test(positive_test)
        print(f"Manual test result: {result}")
        
    finally:
        test_runner.teardown_driver()

# Uncomment the line below to run specific tests instead of all tests
# run_specific_tests()