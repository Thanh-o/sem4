
import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
import openpyxl
import time

def load_test_data_from_excel(file_path):
    wb = openpyxl.load_workbook(file_path)
    sheet = wb.active
    test_data = []
    for row in sheet.iter_rows(min_row=2, values_only=True):
        formcontrol, label, field_type, expected_error = row
        if formcontrol and label:
            test_data.append((formcontrol.strip(), label.strip(), field_type.strip(), expected_error.strip() if expected_error else ""))
    return test_data

# Load test data
test_cases = load_test_data_from_excel("data/validation_fields_full.xlsx")

@pytest.mark.parametrize("formcontrol_name,label,field_type,expected_error", test_cases)
def test_realtime_validation(driver, formcontrol_name, label, field_type, expected_error):
    print(f"\n⏳ Đang kiểm thử: {label} ({formcontrol_name}) [{field_type}]")

    if field_type == "text" or field_type == "date":
        # Với input text/date, tìm thẻ input
        input_xpath = f"//input[@formcontrolname='{formcontrol_name}']"
        input_field = driver.find_element(By.XPATH, input_xpath)
        input_field.send_keys("abc")
        time.sleep(0.3)
        input_field.clear()
        input_field.send_keys(Keys.TAB)
        time.sleep(1)

        form_item_xpath = f"{input_xpath}/ancestor::vts-form-item"

    elif field_type == "select":
        # Với select, click vào để mở, sau đó click ra ngoài để blur
        select_xpath = f"//vts-select[@formcontrolname='{formcontrol_name}']"
        select = driver.find_element(By.XPATH, select_xpath)
        select.click()
        time.sleep(0.5)

        # Click ra ngoài (offset tương đối)
        ActionChains(driver).move_by_offset(0, 200).click().perform()
        time.sleep(1)

        form_item_xpath = f"{select_xpath}/ancestor::vts-form-item"

    else:
        pytest.skip(f"Không hỗ trợ kiểu field_type: {field_type}")

    # Kiểm tra class báo lỗi
    form_item = driver.find_element(By.XPATH, form_item_xpath)
    class_attr = form_item.get_attribute("class")
    assert "vts-form-item-has-error" in class_attr, f"[{label}] không có class lỗi sau khi để trống"

    # Kiểm tra nội dung lỗi (nếu có khai báo expected_error)
    try:
        error_text = form_item.find_element(By.XPATH, ".//small").text.strip()
        if expected_error:
            assert expected_error in error_text, f"[{label}] lỗi không đúng. Thấy: '{error_text}'"
        else:
            assert error_text != "", f"[{label}] không có thông báo lỗi"
    except:
        pytest.fail(f"[{label}] không tìm thấy phần tử thông báo lỗi")
