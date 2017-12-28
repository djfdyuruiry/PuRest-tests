import Utils.HttpRequestHelper;
import Utils.HttpResponseWrapper;
import Utils.TestConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class WebApps {
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
    public void webApps_WhenUrlRouteMapped_ServerShouldCallHandler() {
        driver.get(String.format("http://%s/tests/", TestConfig.WEB_SERVER_HOST_PORT));
        Assert.assertEquals("Server failed to map URL route and call handler.",
                "URL Routing Test", driver.getTitle());
    }

    @Test
    public void webApps_WhenUrlRouteParametersDefined_ServerShouldPassTheseToHandler() {
        String urlParam = "monkeys";
        driver.get(String.format("http://%s/tests/params/%s", TestConfig.WEB_SERVER_HOST_PORT, urlParam));

        WebElement element = driver.findElement(By.id("param0"));

        Assert.assertEquals("Server failed to pass URL parameters to route handler.",
                urlParam, element.getText());
    }

    @Test
    public void webApps_WhenHttpMethodFilteredAndAcceptableMethodUsed_ServerShouldServeRoute() {
        driver.get(String.format("http://%s/tests/method/", TestConfig.WEB_SERVER_HOST_PORT));

        Assert.assertEquals("Server failed to pass URL parameters to route handler.",
                "HTTP Method Test", driver.getTitle());
    }

    @Test
    public void webApps_WhenHttpMethodFilteredAndUnacceptableMethodUsed_ServerShouldReturn403() throws IOException {
        String url = String.format("http://%s/tests/method", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper response = HttpRequestHelper.makePostRequest(url, null);

        int statusCode = response.response.getStatusLine().getStatusCode();

        Assert.assertEquals("Server failed to respond with HTTP status code of 404.",
                403, statusCode);
    }

    @Test
    public void webApps_WhenRequestHostFilteredAndAcceptableHostUsed_ServerShouldServeRoute() {
        driver.get(String.format("http://%s/hosttest/index.html", TestConfig.WEB_SERVER_ALT_HOST_PORT));

        Assert.assertEquals(String.format("Server failed to serve route via request host %s.", TestConfig.WEB_SERVER_HOST_PORT),
                "HTTP Host Filtering Test", driver.getTitle());
    }

    @Test
    public void webApps_WhenRequestHostFilteredAndUnacceptableHostUsed_ServerShouldReturn403() throws IOException {
        String url = String.format("http://%s/hosttest/index.html", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper response = HttpRequestHelper.makeGetRequest(url);

        int statusCode = response.response.getStatusLine().getStatusCode();

        Assert.assertEquals(String.format("Server failed to respond with HTTP status code of 404 for request host '%s'.",
                        TestConfig.WEB_SERVER_HOST_PORT), 403, statusCode);
    }

    @Test
    public void webApps_WhenRequestMadeForHiddenFile_ServerShouldReturn404() throws IOException {
        String url = String.format("http://%s/tests/test.bmp", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper response = HttpRequestHelper.makeGetRequest(url);

        int statusCode = response.response.getStatusLine().getStatusCode();

        Assert.assertEquals("Server failed to respond with HTTP status code of 404 for hidden file type 'bmp'.",
                404, statusCode);
    }

    @Test
    public void webApps_WhenRequestMadeForNonHiddenFile_ServerShouldServeFile() throws IOException {
        String url = String.format("http://%s/tests/test.txt", TestConfig.WEB_SERVER_HOST_PORT);
        driver.get(url);

        Assert.assertEquals("Server failed to serve non-hidden file type 'txt", url, driver.getTitle());
    }

    @Test
    public void webApps_WhenRequestMadeForPhpFile_ServerShouldRenderScript() throws IOException {
        driver.get(String.format("http://%s/tests/test.php", TestConfig.WEB_SERVER_HOST_PORT));

        Assert.assertEquals("Server failed to render PHP script.", "PHP Test", driver.getTitle());
    }
}
