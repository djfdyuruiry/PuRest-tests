import Utils.HttpRequestHelper;
import Utils.HttpResponseWrapper;
import Utils.TestConfig;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
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

public class ContentFileHandling {
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        driver.quit();
    }//bodyType

    @Test
    public void contentFileHandling_WhenQueryStringPassedToServer_RouteHandlerShouldReceiveFields() {
        String key0 = "monkeys";
        String key1 = "zoo";
        String value0 = "23";
        String value1 = "belfast";

        driver.get(String.format("http://%s/tests/form?%s=%s&%s=%s",
                TestConfig.WEB_SERVER_HOST_PORT, key0, value0, key1, value1));

        WebElement element0 = driver.findElement(By.id(key0));
        WebElement element1 = driver.findElement(By.id(key1));

        Assert.assertEquals("Server failed to pass correct query string fields to route handler.",
            value0 + "|" + value1, element0.getText() + "|" + element1.getText());
    }

    @Test
    public void contentFileHandling_WhenValueReturnedFromHandlerAndSerializableResponseContentTypeSet_ServerShouldSerializeReturnValueToBody() throws IOException {
        String url = String.format("http://%s/tests/responsecontent", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper wrapper;
        StringEntity entity;

        // application/json
        entity = new StringEntity("{\"field\":\"value\"}");
        entity.setContentType("application/json");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for route 'tests/responsecontent'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertEquals("Server failed to serialize the return value from handler to content type 'application/json'.",
                "{\"field\":\"value\"}", wrapper.body);

        // application/xml
        entity = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><field>value</field>");
        entity.setContentType("application/xml");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for route 'tests/responsecontent'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertEquals("Server failed to serialize the return value from handler to content type 'application/xml'.",
                "<field>value</field>", wrapper.body);

        // text/csv
        entity = new StringEntity("1,2,3,4,5");
        entity.setContentType("text/csv");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for route 'tests/responsecontent'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertEquals("Server failed to serialize the return value from handler to content type 'text/csv'.",
                "1,2,3", wrapper.body);
    }

    @Test
    public void contentFileHandling_WhenDeserializablePostDataUploadedToServer_ServerShouldDeserializeBody() throws IOException {
        String url = String.format("http://%s/tests/content.lhtml", TestConfig.WEB_SERVER_HOST_PORT);
        HttpResponseWrapper wrapper;
        StringEntity entity;

        // application/x-www-form-urlencoded
        String key0 = "monkeys";
        String value0 = "23";

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair(key0, value0));

        wrapper = HttpRequestHelper.makePostRequest(url, new UrlEncodedFormEntity(urlParameters));

        Assert.assertEquals("Server failed to return 200 in response to POST request for server-side script file 'tests/content.lhtml'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertTrue("Server failed to convert the POST data of type 'application/x-www-form-urlencoded' to a lua table.",
                wrapper.body.contains("##table##"));

        // application/json
        entity = new StringEntity("{\"field\":\"value\"}");
        entity.setContentType("application/json");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for server-side script file 'tests/content.lhtml'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertTrue("Server failed to convert the POST data of type 'application/json' to a lua table.",
                wrapper.body.contains("##table##"));

        // application/xml
        entity = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><field>value</field>");
        entity.setContentType("application/xml");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for server-side script file 'tests/content.lhtml'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertTrue("Server failed to convert the POST data of type 'application/xml' to a lua table.",
                wrapper.body.contains("##table##"));

        // text/csv
        entity = new StringEntity("1,2,3,4,5");
        entity.setContentType("text/csv");

        wrapper = HttpRequestHelper.makePostRequest(url, entity);

        Assert.assertEquals("Server failed to return 200 in response to POST request for server-side script file 'tests/content.lhtml'.",
                200, wrapper.response.getStatusLine().getStatusCode());

        Assert.assertTrue("Server failed to convert the POST data of type 'text/csv' to a lua table.",
                wrapper.body.contains("##table##"));
    }
}
