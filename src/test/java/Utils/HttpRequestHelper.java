package Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpRequestHelper {
    private static String getResponseEntityContent(HttpResponse response) throws IOException{
        HttpEntity responseEntity = response.getEntity();
        String lines = "";

        if (responseEntity != null) {
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(responseEntity.getContent()));

            String line;

            while ((line = rd.readLine()) != null) {
                lines += line;
            }
        }

        return lines.equals("") ? null : lines;
    }

    public static HttpResponseWrapper makeGetRequest(String url) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        // Get the response
        HttpResponse response = client.execute(request);
        HttpEntity responseEntity = response.getEntity();

        String lines = getResponseEntityContent(response);

        HttpResponseWrapper wrapper = new HttpResponseWrapper();
        wrapper.body = lines;
        wrapper.response = response;

        return wrapper;
    }

    public static HttpResponseWrapper makePostRequest(String url, HttpEntity entity) throws IOException{
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);

        if (entity != null) {
            request.setEntity(entity);
        }

        // Get the response
        HttpResponse response = client.execute(request);
        HttpEntity responseEntity = response.getEntity();

        String lines = getResponseEntityContent(response);

        HttpResponseWrapper wrapper = new HttpResponseWrapper();
        wrapper.body = lines ;
        wrapper.response = response;

        return wrapper;
    }
}
