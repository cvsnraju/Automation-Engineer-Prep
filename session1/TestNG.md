# TestNG — Quick Reference

Annotations
- `@BeforeSuite`, `@AfterSuite`
- `@BeforeTest`, `@AfterTest`
- `@BeforeClass`, `@AfterClass`
- `@BeforeMethod`, `@AfterMethod`
- `@Test` (with `groups`, `priority`, `dependsOnMethods`)


@BeforeMethod    // Runs before each @Test method
@AfterMethod     // Runs after each @Test method
@BeforeClass     // Runs once before all tests in class
@AfterClass      // Runs once after all tests in class
@Test            // Marks method as a test case
@DataProvider    // Provides test data
@Parameters      // Receives parameters from XML/annotations
@Ignore          // Skips test execution

Grouping
- Use `groups` attribute on `@Test` and run with a TestNG XML including `<groups>`.

Parameterization
- Use `@Parameters` with TestNG XML or `@DataProvider` for data-driven tests.

Data Providers
- Define methods annotated with `@DataProvider` returning `Object[][]` and reference from `@Test(dataProvider="name")`.

Assertions
- Use `org.testng.Assert` methods: `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`.

See `examples/TestNGExample.java` for a compact example covering annotations, grouping, parameters, data provider, and assertions.

https://testng.org



