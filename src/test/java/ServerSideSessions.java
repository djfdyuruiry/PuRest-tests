import Utils.TestConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ServerSideSessions {
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
    public void serverSideSessions_WhenRequestMadeToApp_SessionDataShouldBeAvailableToServerCode() {
        driver.get(String.format("http://%s/tests/session", TestConfig.WEB_SERVER_HOST_PORT));

        WebElement element0 = driver.findElement(By.id("sessionVisits"));
        int numVisits = Integer.parseInt(element0.getText());

        Assert.assertTrue("Server failed to pass session data to route handler.",
                numVisits > 0);
    }

    @Test
    public void serverSideSessions_WhenRequestsAreMadeToApp_SessionDataShouldPersistBetweenRequests() {
        driver.get(String.format("http://%s/tests/session", TestConfig.WEB_SERVER_HOST_PORT));

        WebElement element0 = driver.findElement(By.id("sessionVisits"));
        int numVisits = Integer.parseInt(element0.getText());

        driver.get(String.format("http://%s/tests/session", TestConfig.WEB_SERVER_HOST_PORT));
        element0 = driver.findElement(By.id("sessionVisits"));

        int nextNumVisits = Integer.parseInt(element0.getText());

        Assert.assertTrue("Server failed to persist session data between client requests.",
                nextNumVisits == numVisits + 1);
    }
}
