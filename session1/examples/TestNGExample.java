package session1.examples;

import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestNGExample {

    @BeforeClass
    public void setupClass() {
        System.out.println("BeforeClass - setup");
    }

    @BeforeMethod
    public void setup() {
        System.out.println("BeforeMethod - start test");
    }

    @Test(groups = {"fast"})
    public void simpleAssertion() {
        Assert.assertTrue(1 + 1 == 2, "Basic math should work");
    }

    @Test(dataProvider = "dp")
    public void dataDrivenTest(int a, int b, int expected) {
        Assert.assertEquals(a + b, expected);
    }

    @DataProvider(name = "dp")
    public Object[][] dataProvider() {
        return new Object[][]{{1, 1, 2}, {2, 3, 5}, {5, 7, 12}};
    }

    @Test(dependsOnMethods = {"simpleAssertion"}, groups = {"slow"})
    public void dependentTest() {
        Assert.assertNotNull(new Object());
    }

    // New: API tests that call the Java mock API server at http://localhost:4568
    private final String base = "http://localhost:4568";

    @Test(groups = {"api"})
    public void testCseApi() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(base + "/api/cse")).GET().build();
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
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(base + "/api/it")).GET().build();
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

    @AfterMethod
    public void teardown() {
        System.out.println("AfterMethod - end test");
    }

    @AfterClass
    public void teardownClass() {
        System.out.println("AfterClass - cleanup");
    }
}
