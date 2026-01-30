package session1.examples;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSeleniumTest {
    private static final String BASE = "http://localhost:4568";
    private static HttpClient client;

    @BeforeClass
    public static void beforeClass() {
        client = HttpClient.newHttpClient();
    }

    @AfterClass
    public static void afterClass() {
        client = null;
    }

    @Test(groups = {"api", "smoke"})
    public void testCseApi() throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE + "/api/cse")).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(res.statusCode(), 200);
        String body = res.body();
        int faculty = extractInt(body, "facultyCount");
        int avail = extractInt(body, "availableToday");
        Assert.assertTrue(faculty > 0, "facultyCount should be > 0");
        Assert.assertTrue(avail >= 0, "availableToday should be >= 0");
    }

    @Test(groups = {"api"})
    public void testItApi() throws Exception {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(BASE + "/api/it")).GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(res.statusCode(), 200);
        String body = res.body();
        int faculty = extractInt(body, "facultyCount");
        int avail = extractInt(body, "availableToday");
        Assert.assertTrue(faculty > 0, "facultyCount should be > 0");
        Assert.assertTrue(avail >= 0, "availableToday should be >= 0");
    }

    private int extractInt(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        throw new IllegalStateException("Key not found in JSON: " + key);
    }
}

