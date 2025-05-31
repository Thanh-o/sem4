from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import Select
import time
import os

# Mở trình duyệt Chrome
driver = webdriver.Chrome()

# Mở file HTML local
file_path = "file:///" + os.path.abspath("C:/Boss/GitHub/sem4/Facebook_Signup_Project/web/sign_up.html")
driver.get(file_path)

# Điền thông tin vào form
driver.find_element(By.ID, "firstName").send_keys("John")
driver.find_element(By.ID, "surname").send_keys("Doe")

Select(driver.find_element(By.ID, "dobDay")).select_by_visible_text("15")
Select(driver.find_element(By.ID, "dobMonth")).select_by_visible_text("May")
Select(driver.find_element(By.ID, "dobYear")).select_by_visible_text("2000")

driver.find_element(By.ID, "genderMale").click()

driver.find_element(By.ID, "contact").send_keys("john.doe@example.com")
driver.find_element(By.ID, "password").send_keys("StrongPass123")

# Click nút đăng ký
driver.find_element(By.ID, "signUpBtn").click()

# Đợi 3 giây để xem kết quả
time.sleep(3)
driver.quit()
