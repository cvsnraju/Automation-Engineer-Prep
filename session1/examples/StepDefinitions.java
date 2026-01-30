package session1.examples;

import io.cucumber.java.en.*;
import org.junit.Assert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepDefinitions {
    private final String base = "http://localhost:4568";
    private HttpResponse<String> lastResponse;
    private String lastBody;

    @Given("the mock API server is running")
    public void the_mock_api_server_is_running() {
        // assume server is started by user; optionally implement a health check
    }

    @When("I GET {string}")
    public void i_get(String path) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(base + path)).GET().build();
        lastResponse = client.send(req, HttpResponse.BodyHandlers.ofString());
        lastBody = lastResponse.body();
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        Assert.assertNotNull(lastResponse);
        Assert.assertEquals(expectedStatus.intValue(), lastResponse.statusCode());
    }

    @Then("the response field {string} should be {int}")
    public void the_response_field_should_be(String key, Integer expected) {
        Assert.assertNotNull(lastBody);
        int actual = extractInt(lastBody, key);
        Assert.assertEquals(expected.intValue(), actual);
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
