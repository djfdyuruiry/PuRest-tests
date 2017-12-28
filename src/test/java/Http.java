import TestCategories.Security;
import TestCategories.WebBrowser;
import Utils.HttpRequestHelper;
import Utils.HttpResponseWrapper;
import Utils.TestConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public class Http {
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
    @Category(WebBrowser.class)
    public void http_ExistingWebPageRequested_ServerShouldServePage() {
        driver.get(String.format("http://%s/tests/index.html", TestConfig.WEB_SERVER_HOST_PORT));
        Assert.assertEquals("Server failed to respond with requested page.",
                "Load Web Page Test", driver.getTitle());
    }

    @Test
    @Category(WebBrowser.class)
    public void http_NonExistentWebPageRequested_ServerShouldReturn404() throws IOException {
        String url = String.format("http://%s/tests/mubojumbo_01010_index.html", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper response = HttpRequestHelper.makeGetRequest(url);

        int statusCode = response.response.getStatusLine().getStatusCode();

        Assert.assertEquals("Server failed to respond with HTTP status code of 404.",
                404, statusCode);
    }

    @Test
    @Category(Security.class)
    public void http_WebPageProtectedByAuthenticationRequestedWithCorrectCredentials_ServerShouldServePage()  {
        driver.get(String.format("http://%s:%s@%s/tests/authentication_test.html",
                TestConfig.WEB_SERVER_USERNAME, TestConfig.WEB_SERVER_PASSWORD, TestConfig.WEB_SERVER_HOST_PORT));

        Assert.assertEquals("Error loading page protected by HTTP authentication.",
            "Authentication Test", driver.getTitle());
    }

    @Test
    @Category(Security.class)
    public void http_WebPageProtectedByAuthenticationRequestedWithoutCredentials_ServerShouldReturn401() throws IOException {
        String url = String.format("http://%s/tests/authentication_test.html", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper response = HttpRequestHelper.makeGetRequest(url);

        int statusCode = response.response.getStatusLine().getStatusCode();

        Assert.assertEquals("Server failed to respond with HTTP status code of 401.",
                401, statusCode);
    }
}
