import Utils.TestConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Mvc {
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void mvc_WhenControllerRouteCalled_ViewShouldBeProcessedWithCorrectModel() {
        driver.get(String.format("http://%s/tests/view", TestConfig.WEB_SERVER_HOST_PORT));

        WebElement element0 = driver.findElement(By.id("name"));

        Assert.assertEquals("Server failed to render dynamic title for view when processed from controller route handler.",
                "MVC View Test", driver.getTitle());

        Assert.assertEquals("Server failed to load view with correct model from controller route handler.",
                "bob", element0.getText());
    }
}
