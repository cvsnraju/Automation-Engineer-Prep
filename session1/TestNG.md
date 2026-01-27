# TestNG — Quick Reference

Annotations
- `@BeforeSuite`, `@AfterSuite`
- `@BeforeTest`, `@AfterTest`
- `@BeforeClass`, `@AfterClass`
- `@BeforeMethod`, `@AfterMethod`
- `@Test` (with `groups`, `priority`, `dependsOnMethods`)

Grouping
- Use `groups` attribute on `@Test` and run with a TestNG XML including `<groups>`.

Parameterization
- Use `@Parameters` with TestNG XML or `@DataProvider` for data-driven tests.

Data Providers
- Define methods annotated with `@DataProvider` returning `Object[][]` and reference from `@Test(dataProvider="name")`.

Assertions
- Use `org.testng.Assert` methods: `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`.

See `examples/TestNGExample.java` for a compact example covering annotations, grouping, parameters, data provider, and assertions.
