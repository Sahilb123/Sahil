import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.Assert.assertTrue;

public class LoginTest {

    private WebDriver driver;

    @Before
    public void setUp() {
        // Set up the WebDriver (use appropriate driver for your browser)
        driver = new ChromeDriver();
    }

    @Test
    public void testLogin() {
        // Navigate to the application
        driver.get("http://<tomcat-server>:8080/selenium-app");

        // Interact with the login form
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("testpassword");
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Assert that the user is on the welcome page
        String welcomeMessage = driver.findElement(By.tagName("p")).getText();
        assertTrue(welcomeMessage.contains("You have successfully logged in!"));
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
