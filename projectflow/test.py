import unittest
import time
from selenium import webdriver
from selenium.webdriver.edge.service import Service as EdgeService
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException

class ProjectFlowE2ETest(unittest.TestCase):
    def setUp(self):
        edge_service = EdgeService(executable_path=r'C:\GitHubMain\GroupProjects\projectflow\msedgedriver.exe')
        self.driver = webdriver.Edge(service=edge_service)
        self.driver.get("http://localhost:8080")
        self.driver.maximize_window()
        self.wait = WebDriverWait(self.driver, 10)

    # def test_register_new_user(self):
    #     driver = self.driver
    #     wait = self.wait

    #     driver.get("http://localhost:8080/register")
    #     time.sleep(5)
    #     login_field = wait.until(EC.presence_of_element_located((By.NAME, "login")))
    #     password_field = driver.find_element(By.NAME, "passwordHash")
    #     confirm_field = driver.find_element(By.NAME, "confirmPassword")
    #     submit_button = driver.find_element(By.TAG_NAME, "button")

    #     login_field.send_keys("skebobik3")
    #     password_field.send_keys("123456")
    #     confirm_field.send_keys("123456")
    #     time.sleep(5)
    #     submit_button.click()

    #     wait.until(EC.url_contains("/"))
    #     self.assertIn("/", driver.current_url)

    def test_login_correct_credentials(self):
        driver = self.driver
        wait = self.wait

        driver.get("http://localhost:8080/login")
        time.sleep(5)

        login_field = wait.until(EC.presence_of_element_located((By.NAME, "login")))
        password_field = driver.find_element(By.NAME, "password")
        submit_button = driver.find_element(By.TAG_NAME, "button")

        login_field.send_keys("skebobik2")
        password_field.send_keys("123456")
        time.sleep(5)
        submit_button.click()


        wait.until(EC.url_contains("/"))
        self.assertIn("/", driver.current_url, "Should redirect to index after login")

    def test_login_wrong_credentials(self):
        driver = self.driver
        wait = self.wait

        driver.get("http://localhost:8080/login")
        time.sleep(5)

        login_field = wait.until(EC.presence_of_element_located((By.NAME, "login")))
        password_field = driver.find_element(By.NAME, "password")
        submit_button = driver.find_element(By.TAG_NAME, "button")

        login_field.send_keys("skebobik2")
        password_field.send_keys("wrongpassword")
        time.sleep(5)
        submit_button.click()

        self.assertIn("/login", driver.current_url, "Should stay on login page")
        self.assertIn("Неверный логин или пароль", driver.page_source, "Should show error message")

    def test_login_and_view_project(self):
        driver = self.driver
        wait = self.wait

        driver.get("http://localhost:8080/login")
        time.sleep(3)

        login_field = wait.until(EC.presence_of_element_located((By.NAME, "login")))
        password_field = driver.find_element(By.NAME, "password")
        submit_button = driver.find_element(By.TAG_NAME, "button")

        login_field.send_keys("skebobik2")
        password_field.send_keys("123456")
        submit_button.click()
        wait.until(EC.url_contains("/"))
        self.assertIn("/", driver.current_url, "Should redirect to dashboard after login")

        driver.get("http://localhost:8080/projects/6")
        time.sleep(3)

        project_title = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, ".project-header h1")))
        self.assertTrue(project_title.is_displayed())

        project_name = project_title.text
        self.assertIn("Тестовый проект", project_name, "Should show correct project name")

        project_description = driver.find_element(By.CSS_SELECTOR, ".project-header p").text
        self.assertIsNotNone(project_description, "Should show project description")

        tasks_button = driver.find_element(By.XPATH, "//button[text()='Задачи']")
        members_button = driver.find_element(By.XPATH, "//button[text()='Участники']")
        self.assertTrue(tasks_button.is_displayed(), "Tasks button should be visible")
        self.assertTrue(members_button.is_displayed(), "Members button should be visible")

        tasks_button.click()
        time.sleep(2)

        tasks_section = driver.find_element(By.ID, "tasks")
        self.assertTrue(tasks_section.is_displayed(), "Tasks section should be visible after clicking button")

        try:
            task_table = driver.find_element(By.CSS_SELECTOR, "#tasks table")
            self.assertTrue(task_table.is_displayed(), "Task table should be visible")
        except NoSuchElementException:
            print("No tasks found — OK")

        members_button.click()
        time.sleep(2)

        members_section = driver.find_element(By.ID, "members")
        self.assertTrue(members_section.is_displayed(), "Members section should be visible after clicking button")

        comments_section = driver.find_element(By.CSS_SELECTOR, "div[style*='margin-top: 2rem;'] h3")
        self.assertIn("Комментарии", comments_section.text, "Should show 'Комментарии' section")

    def test_login_and_add_comment_to_project_5(self):
        driver = self.driver
        wait = self.wait

        driver.get("http://localhost:8080/login")
        time.sleep(2)

        login_field = wait.until(EC.presence_of_element_located((By.NAME, "login")))
        password_field = driver.find_element(By.NAME, "password")
        submit_button = driver.find_element(By.TAG_NAME, "button")

        login_field.send_keys("skebobik2")
        password_field.send_keys("123456")
        submit_button.click()

        wait.until(EC.url_contains("/"))
        self.assertIn("/", driver.current_url, "Should redirect to dashboard after login")

        driver.get("http://localhost:8080/projects/5")
        time.sleep(3)

        project_header = wait.until(EC.presence_of_element_located((By.CSS_SELECTOR, ".project-header h1")))
        self.assertTrue(project_header.is_displayed(), "Project header should be visible")

        comment_textarea = wait.until(EC.presence_of_element_located((By.NAME, "commentText")))
        comment_submit_button = driver.find_element(By.CSS_SELECTOR, "button[type='submit']")

        comment_text = "Это тестовый коммавыфаыфавыik3"
        comment_textarea.send_keys(comment_text)

        driver.execute_script("arguments[0].click();", comment_submit_button)

        time.sleep(2)

        self.assertIn(comment_text, driver.page_source, "Comment should appear on the page after submission")

    def tearDown(self):
        self.driver.quit()


if __name__ == "__main__":
    unittest.main()