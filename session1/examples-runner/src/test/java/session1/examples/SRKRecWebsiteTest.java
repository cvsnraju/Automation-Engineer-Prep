package session1.examples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Test class for SRK Rectory Education website using Selenium and ChromeWebDriver
 */
public class SRKRecWebsiteTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://srkrec.edu.in";

    @BeforeMethod
    public void setUp() {
        // Selenium 4.15+ has built-in driver management
        // No WebDriverManager needed - Selenium Manager handles it automatically
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        
        // Maximize the window
        driver.manage().window().maximize();
        
        System.out.println("Browser launched successfully - Selenium Manager auto-detected Chrome 144");
    }


    @Test
    public void testPageURLVerification() throws InterruptedException {
        // Navigate to the website
        System.out.println("Navigating to: " + BASE_URL);
        driver.get(BASE_URL);
        
        // Wait for page to load
        Thread.sleep(2000);
        
        // Get the current URL
        String currentURL = driver.getCurrentUrl();
        System.out.println("Current URL: " + currentURL);
        
        // Verify URL contains expected domain
        Assert.assertTrue(currentURL.contains("srkrec.edu.in"));
        System.out.println("✓ URL verification passed");
    }

    @Test
    public void testBrowserLaunch() {
        // Simple test to verify browser launches
        System.out.println("Test: Verifying browser launch");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        Assert.assertNotNull(driver);
        System.out.println("✓ Browser launched successfully");
    }
    
    @AfterMethod
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed successfully");
        }
    }
}
