import csv
import time
import os
from datetime import datetime
import json
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class FacebookSignUpTest:
    def __init__(self, html_file_path):
        self.driver = None
        self.html_file_path = html_file_path
        self.test_results = []
        self.start_time = None
        self.end_time = None
        
    def setup_driver(self):
        """Setup WebDriver"""
        try:
            from selenium.webdriver.chrome.options import Options
            chrome_options = Options()
            chrome_options.add_argument("--no-sandbox")
            chrome_options.add_argument("--disable-dev-shm-usage")
            chrome_options.add_argument("--headless")  # Run in headless mode for better performance
            
            self.driver = webdriver.Chrome(options=chrome_options)
            self.driver.implicitly_wait(10)
            logger.info("WebDriver initialized successfully")
            return True
        except Exception as e:
            logger.error(f"Error initializing WebDriver: {str(e)}")
            return False
    
    def load_page(self):
        """Load HTML page"""
        try:
            if not os.path.exists(self.html_file_path):
                logger.error(f"File does not exist: {self.html_file_path}")
                return False
            
            file_url = f"file:///{os.path.abspath(self.html_file_path).replace(os.sep, '/')}"
            logger.info(f"Loading: {file_url}")
            
            self.driver.get(file_url)
            time.sleep(2)
            
            # Check if form exists
            try:
                WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located((By.ID, "signupForm"))
                )
                logger.info("Sign-up form found")
                return True
            except TimeoutException:
                logger.error("Sign-up form not found")
                return False
                
        except Exception as e:
            logger.error(f"Error loading page: {str(e)}")
            return False
    
    def parse_test_data(self, test_data_string):
        """Parse test data from string format"""
        test_data = {}
        if not test_data_string:
            return test_data
            
        pairs = test_data_string.split('|')
        for pair in pairs:
            if ':' in pair:
                key, value = pair.split(':', 1)
                test_data[key] = value
        
        return test_data
    
    def clear_form(self):
        """Clear form data"""
        try:
            WebDriverWait(self.driver, 10).until(
                EC.presence_of_element_located((By.ID, "signupForm"))
            )
            
            # Clear text inputs
            text_inputs = ['firstName', 'surname', 'contact', 'password']
            for input_id in text_inputs:
                try:
                    element = self.driver.find_element(By.ID, input_id)
                    element.clear()
                except NoSuchElementException:
                    logger.warning(f"Element not found: {input_id}")
            
            # Reset dropdowns
            dropdowns = ['dobDay', 'dobMonth', 'dobYear']
            for dropdown_id in dropdowns:
                try:
                    select = Select(self.driver.find_element(By.ID, dropdown_id))
                    select.select_by_index(0)
                except NoSuchElementException:
                    logger.warning(f"Dropdown not found: {dropdown_id}")
                    
        except Exception as e:
            logger.warning(f"Error clearing form: {str(e)}")
    
    def fill_form(self, test_data):
        """Fill form with test data"""
        try:
            # Fill text inputs
            if 'firstName' in test_data and test_data['firstName']:
                element = self.driver.find_element(By.ID, 'firstName')
                element.clear()
                element.send_keys(test_data['firstName'])
            
            if 'surname' in test_data and test_data['surname']:
                element = self.driver.find_element(By.ID, 'surname')
                element.clear()
                element.send_keys(test_data['surname'])
            
            if 'contact' in test_data and test_data['contact']:
                element = self.driver.find_element(By.ID, 'contact')
                element.clear()
                element.send_keys(test_data['contact'])
            
            if 'password' in test_data and test_data['password']:
                element = self.driver.find_element(By.ID, 'password')
                element.clear()
                element.send_keys(test_data['password'])
            
            # Fill date of birth
            if 'dobDay' in test_data and test_data['dobDay']:
                day_select = Select(self.driver.find_element(By.ID, 'dobDay'))
                day_select.select_by_visible_text(test_data['dobDay'])
            
            if 'dobMonth' in test_data and test_data['dobMonth']:
                month_select = Select(self.driver.find_element(By.ID, 'dobMonth'))
                month_select.select_by_visible_text(test_data['dobMonth'])
            
            if 'dobYear' in test_data and test_data['dobYear']:
                year_select = Select(self.driver.find_element(By.ID, 'dobYear'))
                year_select.select_by_visible_text(test_data['dobYear'])
            
            # Select gender
            if 'gender' in test_data and test_data['gender']:
                if test_data['gender'] == 'Male':
                    self.driver.find_element(By.ID, 'genderMale').click()
                elif test_data['gender'] == 'Female':
                    self.driver.find_element(By.ID, 'genderFemale').click()
                elif test_data['gender'] == 'Custom':
                    self.driver.find_element(By.ID, 'genderCustom').click()
            
            logger.info("Form filled successfully")
            return True
            
        except Exception as e:
            logger.error(f"Error filling form: {str(e)}")
            return False
    
    def validate_form(self, test_data):
        """Validate form data"""
        try:
            # Check required fields
            required_fields = ['firstName', 'surname', 'contact', 'password']
            
            for field_id in required_fields:
                element = self.driver.find_element(By.ID, field_id)
                value = element.get_attribute('value')
                if not value:
                    return False, f"Required field {field_id} is empty"
            
            # Check date fields
            date_fields = ['dobDay', 'dobMonth', 'dobYear']
            for field_id in date_fields:
                element = self.driver.find_element(By.ID, field_id)
                value = element.get_attribute('value')
                if not value:
                    return False, f"Date field {field_id} is not selected"
            
            # Check gender selection
            gender_selected = False
            radio_buttons = self.driver.find_elements(By.NAME, 'gender')
            for radio in radio_buttons:
                if radio.is_selected():
                    gender_selected = True
                    break
            
            if not gender_selected:
                return False, "Gender is not selected"
            
            # Check password length
            password = self.driver.find_element(By.ID, 'password').get_attribute('value')
            if len(password) < 8:
                return False, "Password is too short"
            
            # Check email format
            contact = self.driver.find_element(By.ID, 'contact').get_attribute('value')
            if '@' in contact and '.' in contact:
                pass
            elif contact.isdigit() and len(contact) >= 10:
                pass
            else:
                return False, "Invalid contact format"
            
            return True, "Validation passed"
            
        except Exception as e:
            return False, f"Validation error: {str(e)}"
    
    def run_test_case(self, test_case_row):
        """Run a single test case"""
        test_case_id = test_case_row['test_case_id']
        test_description = test_case_row['test_description']
        test_data_string = test_case_row['test_data']
        expected_result = test_case_row['expected_result']
        
        logger.info(f"Running test case: {test_case_id} - {test_description}")
        
        test_start_time = time.time()
        
        try:
            # Parse test data
            test_data = self.parse_test_data(test_data_string)
            
            # Clear form before test
            self.clear_form()
            
            # Fill data
            if not self.fill_form(test_data):
                return {
                    'test_case_id': test_case_id,
                    'test_description': test_description,
                    'expected_result': expected_result,
                    'actual_result': 'Failed to fill form',
                    'status': 'FAIL',
                    'execution_time': round(time.time() - test_start_time, 2),
                    'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
                }
            
            # Validate form
            is_valid, validation_message = self.validate_form(test_data)
            
            # Determine actual result
            if is_valid:
                actual_result = "Registration successful with confirmation message"
            else:
                actual_result = f"Error message: {validation_message}"
            
            # Compare with expected result
            if expected_result.lower() in actual_result.lower() or actual_result.lower() in expected_result.lower():
                status = 'PASS'
            else:
                status = 'FAIL'
            
            return {
                'test_case_id': test_case_id,
                'test_description': test_description,
                'expected_result': expected_result,
                'actual_result': actual_result,
                'status': status,
                'execution_time': round(time.time() - test_start_time, 2),
                'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            }
            
        except Exception as e:
            return {
                'test_case_id': test_case_id,
                'test_description': test_description,
                'expected_result': expected_result,
                'actual_result': f'Test execution error: {str(e)}',
                'status': 'ERROR',
                'execution_time': round(time.time() - test_start_time, 2),
                'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            }
    
    def read_test_cases(self, csv_file_path):
        """Read test cases from CSV file"""
        test_cases = []
        try:
            with open(csv_file_path, 'r', encoding='utf-8') as file:
                csv_reader = csv.DictReader(file)
                for row in csv_reader:
                    test_cases.append(row)
            logger.info(f"Read {len(test_cases)} test cases from CSV file")
            return test_cases
        except Exception as e:
            logger.error(f"Error reading CSV file: {str(e)}")
            return []
    
    def update_csv_results(self, csv_file_path):
        """Update results in CSV file"""
        try:
            # Read current CSV file
            test_cases = []
            with open(csv_file_path, 'r', encoding='utf-8') as file:
                csv_reader = csv.DictReader(file)
                fieldnames = csv_reader.fieldnames
                for row in csv_reader:
                    test_cases.append(row)
            
            # Update results
            for result in self.test_results:
                for test_case in test_cases:
                    if test_case['test_case_id'] == result['test_case_id']:
                        test_case['actual_result'] = result['actual_result']
                        test_case['status'] = result['status']
                        break
            
            # Write back to CSV file
            with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
                writer = csv.DictWriter(file, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(test_cases)
            
            logger.info(f"Updated results in file: {csv_file_path}")
            
        except Exception as e:
            logger.error(f"Error updating CSV: {str(e)}")
    
    def generate_html_report(self):
        """Generate detailed HTML test report"""
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r['status'] == 'PASS'])
        failed_tests = len([r for r in self.test_results if r['status'] == 'FAIL'])
        error_tests = len([r for r in self.test_results if r['status'] == 'ERROR'])
        pass_rate = (passed_tests/total_tests)*100 if total_tests > 0 else 0
        
        execution_time = round(self.end_time - self.start_time, 2) if self.start_time and self.end_time else 0
        
        html_content = f"""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Facebook Sign-Up Test Report</title>
    <style>
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }}
        .header p {{
            margin: 10px 0 0 0;
            opacity: 0.9;
        }}
        .summary {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            padding: 30px;
            background-color: #f8f9fa;
        }}
        .summary-card {{
            background: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }}
        .summary-card h3 {{
            margin: 0 0 10px 0;
            color: #333;
        }}
        .summary-card .number {{
            font-size: 2em;
            font-weight: bold;
            margin: 10px 0;
        }}
        .pass {{ color: #28a745; }}
        .fail {{ color: #dc3545; }}
        .error {{ color: #ffc107; }}
        .total {{ color: #007bff; }}
        .results {{
            padding: 30px;
        }}
        .results h2 {{
            color: #333;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
        }}
        .test-case {{
            border: 1px solid #ddd;
            border-radius: 8px;
            margin: 15px 0;
            overflow: hidden;
        }}
        .test-case-header {{
            padding: 15px 20px;
            background-color: #f8f9fa;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }}
        .test-case-body {{
            padding: 20px;
            background-color: white;
        }}
        .status-badge {{
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.8em;
            font-weight: bold;
            text-transform: uppercase;
        }}
        .status-pass {{
            background-color: #d4edda;
            color: #155724;
        }}
        .status-fail {{
            background-color: #f8d7da;
            color: #721c24;
        }}
        .status-error {{
            background-color: #fff3cd;
            color: #856404;
        }}
        .test-details {{
            display: grid;
            gap: 15px;
        }}
        .test-detail {{
            display: grid;
            grid-template-columns: 150px 1fr;
            gap: 10px;
        }}
        .test-detail strong {{
            color: #666;
        }}
        .footer {{
            background-color: #333;
            color: white;
            text-align: center;
            padding: 20px;
        }}
        .progress-bar {{
            width: 100%;
            height: 20px;
            background-color: #e9ecef;
            border-radius: 10px;
            overflow: hidden;
            margin: 10px 0;
        }}
        .progress-fill {{
            height: 100%;
            background: linear-gradient(90deg, #28a745 0%, #20c997 100%);
            transition: width 0.3s ease;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Facebook Sign-Up Test Report</h1>
            <p>Generated on {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="summary">
            <div class="summary-card">
                <h3>Total Tests</h3>
                <div class="number total">{total_tests}</div>
            </div>
            <div class="summary-card">
                <h3>Passed</h3>
                <div class="number pass">{passed_tests}</div>
            </div>
            <div class="summary-card">
                <h3>Failed</h3>
                <div class="number fail">{failed_tests}</div>
            </div>
            <div class="summary-card">
                <h3>Errors</h3>
                <div class="number error">{error_tests}</div>
            </div>
            <div class="summary-card">
                <h3>Pass Rate</h3>
                <div class="number pass">{pass_rate:.1f}%</div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: {pass_rate}%"></div>
                </div>
            </div>
            <div class="summary-card">
                <h3>Execution Time</h3>
                <div class="number total">{execution_time}s</div>
            </div>
        </div>
        
        <div class="results">
            <h2>Test Case Results</h2>
        """
        
        for result in self.test_results:
            status_class = f"status-{result['status'].lower()}"
            html_content += f"""
            <div class="test-case">
                <div class="test-case-header">
                    <div>
                        <strong>{result['test_case_id']}</strong>
                        <span style="margin-left: 10px; color: #666;">{result.get('test_description', '')}</span>
                    </div>
                    <span class="status-badge {status_class}">{result['status']}</span>
                </div>
                <div class="test-case-body">
                    <div class="test-details">
                        <div class="test-detail">
                            <strong>Expected Result:</strong>
                            <span>{result['expected_result']}</span>
                        </div>
                        <div class="test-detail">
                            <strong>Actual Result:</strong>
                            <span>{result['actual_result']}</span>
                        </div>
                        <div class="test-detail">
                            <strong>Execution Time:</strong>
                            <span>{result.get('execution_time', 'N/A')}s</span>
                        </div>
                        <div class="test-detail">
                            <strong>Timestamp:</strong>
                            <span>{result.get('timestamp', 'N/A')}</span>
                        </div>
                    </div>
                </div>
            </div>
            """
        
        html_content += """
        </div>
        
        <div class="footer">
            <p>Test Report Generated by Facebook Sign-Up Test Automation Framework</p>
        </div>
    </div>
</body>
</html>
        """
        
        # Save HTML report
        report_filename = f"test_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.html"
        with open(report_filename, 'w', encoding='utf-8') as file:
            file.write(html_content)
        
        logger.info(f"HTML report generated: {report_filename}")
        return report_filename
    
    def generate_json_report(self):
        """Generate JSON test report"""
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r['status'] == 'PASS'])
        failed_tests = len([r for r in self.test_results if r['status'] == 'FAIL'])
        error_tests = len([r for r in self.test_results if r['status'] == 'ERROR'])
        
        report_data = {
            'test_summary': {
                'total_tests': total_tests,
                'passed_tests': passed_tests,
                'failed_tests': failed_tests,
                'error_tests': error_tests,
                'pass_rate': round((passed_tests/total_tests)*100, 2) if total_tests > 0 else 0,
                'execution_time': round(self.end_time - self.start_time, 2) if self.start_time and self.end_time else 0,
                'start_time': self.start_time,
                'end_time': self.end_time,
                'report_generated': datetime.now().isoformat()
            },
            'test_results': self.test_results
        }
        
        # Save JSON report
        report_filename = f"test_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        with open(report_filename, 'w', encoding='utf-8') as file:
            json.dump(report_data, file, indent=2, ensure_ascii=False)
        
        logger.info(f"JSON report generated: {report_filename}")
        return report_filename
    
    def generate_console_report(self):
        """Generate console test report"""
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r['status'] == 'PASS'])
        failed_tests = len([r for r in self.test_results if r['status'] == 'FAIL'])
        error_tests = len([r for r in self.test_results if r['status'] == 'ERROR'])
        
        print("\n" + "="*100)
        print("                                    TEST EXECUTION REPORT")
        print("="*100)
        print(f"Total Test Cases: {total_tests}")
        print(f"Passed: {passed_tests}")
        print(f"Failed: {failed_tests}")
        print(f"Errors: {error_tests}")
        print(f"Pass Rate: {(passed_tests/total_tests)*100:.2f}%" if total_tests > 0 else "0%")
        if self.start_time and self.end_time:
            print(f"Execution Time: {round(self.end_time - self.start_time, 2)}s")
        print("="*100)
        
        print("\nDETAILED RESULTS:")
        print("-"*100)
        for result in self.test_results:
            status_symbol = "✓" if result['status'] == 'PASS' else "✗" if result['status'] == 'FAIL' else "!"
            print(f"{status_symbol} {result['test_case_id']}: {result['status']} ({result.get('execution_time', 'N/A')}s)")
            print(f"   Expected: {result['expected_result']}")
            print(f"   Actual: {result['actual_result']}")
            print("-"*100)
    
    def run_all_tests(self, csv_file_path):
        """Run all test cases"""
        self.start_time = time.time()
        
        if not self.setup_driver():
            logger.error("Cannot initialize WebDriver")
            return
        
        if not self.load_page():
            logger.error("Cannot load page")
            self.driver.quit()
            return
        
        test_cases = self.read_test_cases(csv_file_path)
        if not test_cases:
            logger.error("No test cases to run")
            self.driver.quit()
            return
        
        logger.info(f"Starting to run {len(test_cases)} test cases")
        
        for test_case in test_cases:
            result = self.run_test_case(test_case)
            self.test_results.append(result)
            logger.info(f"Test case {result['test_case_id']}: {result['status']}")
            time.sleep(1)
        
        self.end_time = time.time()
        
        # Update CSV results
        self.update_csv_results(csv_file_path)
        
        # Generate all reports
        self.generate_console_report()
        html_report = self.generate_html_report()
        json_report = self.generate_json_report()
        
        print(f"\n📊 Reports generated:")
        print(f"   • HTML Report: {html_report}")
        print(f"   • JSON Report: {json_report}")
        print(f"   • CSV Updated: {csv_file_path}")
        
        self.driver.quit()
        logger.info("All test cases completed")

# Usage example
if __name__ == "__main__":
    # Find HTML file
    current_dir = os.getcwd()
    html_files = [f for f in os.listdir(current_dir) if f.endswith('.html')]
    
    if html_files:
        html_file_path = os.path.join(current_dir, html_files[0])
        print(f"Found HTML file: {html_files[0]}")
    else:
        html_file_path = "sign_up.html"
        print("Using default HTML file: sign_up.html")
    
    csv_file_path = "test_cases.csv"
    
    # Create sample CSV if it doesn't exist
    if not os.path.exists(csv_file_path):
        sample_data = [
            {
                'test_case_id': 'TC001',
                'test_description': 'Valid registration with all fields',
                'test_data': 'firstName:John|surname:Doe|contact:john.doe@email.com|password:Password123|dobDay:15|dobMonth:January|dobYear:1990|gender:Male',
                'expected_result': 'Registration successful',
                'actual_result': '',
                'status': ''
            },
            {
                'test_case_id': 'TC002',
                'test_description': 'Registration with missing first name',
                'test_data': 'firstName:|surname:Doe|contact:john.doe@email.com|password:Password123|dobDay:15|dobMonth:January|dobYear:1990|gender:Male',
                'expected_result': 'Error message: Required field firstName is empty',
                'actual_result': '',
                'status': ''
            }
        ]
        
        with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
            fieldnames = ['test_case_id', 'test_description', 'test_data', 'expected_result', 'actual_result', 'status']
            writer = csv.DictWriter(file, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(sample_data)
        
        print(f"Created sample CSV file: {csv_file_path}")
    
    # Run tests
    test_runner = FacebookSignUpTest(html_file_path)
    test_runner.run_all_tests(csv_file_path)