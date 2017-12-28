import Utils.HttpRequestHelper;
import Utils.HttpResponseWrapper;
import Utils.TestConfig;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerSideScripts {
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
    public void serverSideScripts_WhenScriptFileRequested_ServerShouldPassQueryStringToScript() {
        String key0 = "monkeys";
        String key1 = "zoo";
        String value0 = "23";
        String value1 = "belfast";

        driver.get(String.format("http://%s/tests/form.lhtml?%s=%s&%s=%s",
                TestConfig.WEB_SERVER_HOST_PORT, key0, value0, key1, value1));

        WebElement element0 = driver.findElement(By.id(key0));
        WebElement element1 = driver.findElement(By.id(key1));

        Assert.assertEquals("Server failed to pass correct query string fields to server-side script.",
                value0 + "|" + value1, element0.getText() + "|" + element1.getText());
    }

    @Test
    public void serverSideScripts_WhenScriptFileRequested_ServerShouldPassPostDataToScript() throws IOException {
        String url = String.format("http://%s/tests/form.lhtml", TestConfig.WEB_SERVER_HOST_PORT);

        String key0 = "monkeys";
        String key1 = "zoo";
        String value0 = "23";
        String value1 = "belfast";

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair(key0, value0));
        urlParameters.add(new BasicNameValuePair(key1, value1));

        HttpResponseWrapper wrapper = HttpRequestHelper.makePostRequest(url, new UrlEncodedFormEntity(urlParameters));

        Assert.assertEquals("Server failed to return 200 in response to POST request for server-side script file 'tests/form.lhtml'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertTrue("Server failed to pass the POST data fields to server-side script.",
                wrapper.body.contains(key0) && wrapper.body.contains(value0) &&
                wrapper.body.contains(key1) && wrapper.body.contains(value1));
    }

    @Test
    public void serverSideScripts_WhenScriptFileRequestedWithPrinterScriplet_ServerShouldWriteOutputToResponseHtml() {
        driver.get(String.format("http://%s/tests/test.lhtml", TestConfig.WEB_SERVER_HOST_PORT));

        Assert.assertEquals("Server failed to write output from server-side script to response HTML.",
                "Form Data Test", driver.getTitle());
    }

    @Test
    public void serverSideScripts_WhenScriptFileRequestedWithJsonScriptlet_ServerShouldWriteJsonToResponseHtml() {
        driver.get(String.format("http://%s/tests/jstest.lhtml", TestConfig.WEB_SERVER_HOST_PORT));

        WebElement element0 = driver.findElement(By.id("nameOut"));
        WebElement element1 = driver.findElement(By.id("ageOut"));

        Assert.assertEquals("Server failed to print JSON scriptlet on dynamic page.",
                "bob|20", element0.getText() + "|" + element1.getText());
    }
}
