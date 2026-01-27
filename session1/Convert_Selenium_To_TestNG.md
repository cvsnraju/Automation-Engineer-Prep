# Convert Selenium tests to TestNG — Quick Guide

This guide shows how to convert existing Selenium tests to TestNG and run them with Maven.

1) Add dependencies (example: `session1/examples-runner/pom.xml`)

- `selenium-java` — Selenium bindings
- `testng` — TestNG test framework
- `webdrivermanager` — optional helper to auto-download browser drivers

Example dependencies (test scope):

```xml
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>4.9.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.8.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>io.github.bonigarcia</groupId>
  <artifactId>webdrivermanager</artifactId>
  <version>5.4.1</version>
  <scope>test</scope>
</dependency>
```

2) Convert test class annotations

- JUnit `@Before`/`@After` → TestNG `@BeforeMethod`/`@AfterMethod`
- JUnit `@BeforeClass`/`@AfterClass` → TestNG `@BeforeClass`/`@AfterClass`
- Keep test methods annotated with `@Test` (TestNG)

3) Example TestNG Selenium test
- See `session1/examples-runner/src/test/java/session1/examples/SimpleSeleniumTest.java` — uses `WebDriverManager` and TestNG.

Run tests:

```bash
cd "Automation-Engineer-Prep/session1/examples-runner"
mvn test
```

Notes
- Ensure a GUI browser is available or use a headless option (Chrome/Firefox headless) if running on CI.
- You can group tests via TestNG `groups` and run specific groups with surefire.
- For larger projects, prefer Page Object Model and dependency injection to improve maintainability.
