package session1.examples;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "session1.examples",
    plugin = {"pretty"}
)
public class RunCucumberTest {
}
