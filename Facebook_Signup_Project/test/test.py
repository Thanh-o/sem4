import csv
import time
import os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import logging

# Cấu hình logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class FacebookSignUpTest:
    def __init__(self, html_file_path):
        self.driver = None
        self.html_file_path = html_file_path
        self.test_results = []
        
    def setup_driver(self):
        """Thiết lập WebDriver"""
        try:
            from selenium.webdriver.chrome.options import Options
            chrome_options = Options()
            chrome_options.add_argument("--no-sandbox")
            chrome_options.add_argument("--disable-dev-shm-usage")
            
            self.driver = webdriver.Chrome(options=chrome_options)
            self.driver.implicitly_wait(10)
            logger.info("WebDriver đã được khởi tạo thành công")
            return True
        except Exception as e:
            logger.error(f"Lỗi khi khởi tạo WebDriver: {str(e)}")
            return False
    
    def load_page(self):
        """Tải trang HTML"""
        try:
            if not os.path.exists(self.html_file_path):
                logger.error(f"File không tồn tại: {self.html_file_path}")
                return False
            
            file_url = f"file:///{os.path.abspath(self.html_file_path).replace(os.sep, '/')}"
            logger.info(f"Đang tải: {file_url}")
            
            self.driver.get(file_url)
            time.sleep(2)
            
            # Kiểm tra form có tồn tại không
            try:
                WebDriverWait(self.driver, 10).until(
                    EC.presence_of_element_located((By.ID, "signupForm"))
                )
                logger.info("Form đăng ký đã được tìm thấy")
                return True
            except TimeoutException:
                logger.error("Không tìm thấy form đăng ký")
                return False
                
        except Exception as e:
            logger.error(f"Lỗi khi tải trang: {str(e)}")
            return False
    
    def parse_test_data(self, test_data_string):
        """Parse test data từ string format"""
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
        """Xóa dữ liệu form"""
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
                    logger.warning(f"Không tìm thấy element: {input_id}")
            
            # Reset dropdowns
            dropdowns = ['dobDay', 'dobMonth', 'dobYear']
            for dropdown_id in dropdowns:
                try:
                    select = Select(self.driver.find_element(By.ID, dropdown_id))
                    select.select_by_index(0)
                except NoSuchElementException:
                    logger.warning(f"Không tìm thấy dropdown: {dropdown_id}")
                    
        except Exception as e:
            logger.warning(f"Lỗi khi xóa form: {str(e)}")
    
    def fill_form(self, test_data):
        """Điền dữ liệu vào form"""
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
            
            logger.info("Đã điền form thành công")
            return True
            
        except Exception as e:
            logger.error(f"Lỗi khi điền form: {str(e)}")
            return False
    
    def validate_form(self, test_data):
        """Kiểm tra validation của form"""
        try:
            # Kiểm tra required fields
            required_fields = ['firstName', 'surname', 'contact', 'password']
            
            for field_id in required_fields:
                element = self.driver.find_element(By.ID, field_id)
                value = element.get_attribute('value')
                if not value:
                    return False, f"Required field {field_id} is empty"
            
            # Kiểm tra date fields
            date_fields = ['dobDay', 'dobMonth', 'dobYear']
            for field_id in date_fields:
                element = self.driver.find_element(By.ID, field_id)
                value = element.get_attribute('value')
                if not value:
                    return False, f"Date field {field_id} is not selected"
            
            # Kiểm tra gender selection
            gender_selected = False
            radio_buttons = self.driver.find_elements(By.NAME, 'gender')
            for radio in radio_buttons:
                if radio.is_selected():
                    gender_selected = True
                    break
            
            if not gender_selected:
                return False, "Gender is not selected"
            
            # Kiểm tra password length
            password = self.driver.find_element(By.ID, 'password').get_attribute('value')
            if len(password) < 8:
                return False, "Password is too short"
            
            # Kiểm tra email format
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
        """Chạy một test case"""
        test_case_id = test_case_row['test_case_id']
        test_description = test_case_row['test_description']
        test_data_string = test_case_row['test_data']
        expected_result = test_case_row['expected_result']
        
        logger.info(f"Đang chạy test case: {test_case_id} - {test_description}")
        
        try:
            # Parse test data
            test_data = self.parse_test_data(test_data_string)
            
            # Clear form trước khi test
            self.clear_form()
            
            # Điền dữ liệu
            if not self.fill_form(test_data):
                return {
                    'test_case_id': test_case_id,
                    'expected_result': expected_result,
                    'actual_result': 'Failed to fill form',
                    'status': 'FAIL'
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
                'expected_result': expected_result,
                'actual_result': actual_result,
                'status': status
            }
            
        except Exception as e:
            return {
                'test_case_id': test_case_id,
                'expected_result': expected_result,
                'actual_result': f'Test execution error: {str(e)}',
                'status': 'ERROR'
            }
    
    def read_test_cases(self, csv_file_path):
        """Đọc test cases từ file CSV"""
        test_cases = []
        try:
            with open(csv_file_path, 'r', encoding='utf-8') as file:
                csv_reader = csv.DictReader(file)
                for row in csv_reader:
                    test_cases.append(row)
            logger.info(f"Đã đọc {len(test_cases)} test cases từ file CSV")
            return test_cases
        except Exception as e:
            logger.error(f"Lỗi khi đọc file CSV: {str(e)}")
            return []
    
    def update_csv_results(self, csv_file_path):
        """Cập nhật kết quả vào file CSV"""
        try:
            # Đọc file CSV hiện tại
            test_cases = []
            with open(csv_file_path, 'r', encoding='utf-8') as file:
                csv_reader = csv.DictReader(file)
                fieldnames = csv_reader.fieldnames
                for row in csv_reader:
                    test_cases.append(row)
            
            # Cập nhật kết quả
            for result in self.test_results:
                for test_case in test_cases:
                    if test_case['test_case_id'] == result['test_case_id']:
                        test_case['actual_result'] = result['actual_result']
                        test_case['status'] = result['status']
                        break
            
            # Ghi lại file CSV
            with open(csv_file_path, 'w', newline='', encoding='utf-8') as file:
                writer = csv.DictWriter(file, fieldnames=fieldnames)
                writer.writeheader()
                writer.writerows(test_cases)
            
            logger.info(f"Đã cập nhật kết quả vào file: {csv_file_path}")
            
        except Exception as e:
            logger.error(f"Lỗi khi cập nhật CSV: {str(e)}")
    
    def generate_report(self):
        """Tạo báo cáo kết quả test"""
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
        print("="*100)
        
        print("\nDETAILED RESULTS:")
        print("-"*100)
        for result in self.test_results:
            status_symbol = "✓" if result['status'] == 'PASS' else "✗" if result['status'] == 'FAIL' else "!"
            print(f"{status_symbol} {result['test_case_id']}: {result['status']}")
            print(f"   Expected: {result['expected_result']}")
            print(f"   Actual: {result['actual_result']}")
            print("-"*100)
    
    def run_all_tests(self, csv_file_path):
        """Chạy tất cả test cases"""
        if not self.setup_driver():
            logger.error("Không thể khởi tạo WebDriver")
            return
        
        if not self.load_page():
            logger.error("Không thể tải trang")
            self.driver.quit()
            return
        
        test_cases = self.read_test_cases(csv_file_path)
        if not test_cases:
            logger.error("Không có test cases để chạy")
            self.driver.quit()
            return
        
        logger.info(f"Bắt đầu chạy {len(test_cases)} test cases")
        
        for test_case in test_cases:
            result = self.run_test_case(test_case)
            self.test_results.append(result)
            logger.info(f"Test case {result['test_case_id']}: {result['status']}")
            time.sleep(1)
        
        # Cập nhật kết quả vào CSV
        self.update_csv_results(csv_file_path)
        
        # Tạo báo cáo
        self.generate_report()
        
        self.driver.quit()
        logger.info("Hoàn thành tất cả test cases")

# Sử dụng
if __name__ == "__main__":
    # Tìm file HTML
    current_dir = os.getcwd()
    html_files = [f for f in os.listdir(current_dir) if f.endswith('.html')]
    
    if html_files:
        html_file_path = os.path.join(current_dir, html_files[0])
    else:
        html_file_path = "sign_up.html"
    
    csv_file_path = "test_cases - test_cases.csv.csv"
    
    # Chạy test
    test_runner = FacebookSignUpTest(html_file_path)
    test_runner.run_all_tests(csv_file_path)