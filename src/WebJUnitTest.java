import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
//JUnit
public class WebJUnitTest {
    public static WebDriver driver;

    @BeforeClass
    public static void openBrowser()
    {
        System. setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void assertURL() {
        // TODO Auto-generated method stub
        driver.get("https://www.lambdatest.com/");

        String actualURL = driver.getCurrentUrl();
        System.out.println(actualURL);

        Assert.assertEquals("https://www.lambdatest.com/",actualURL);
        System.out.println("Test Passed");
    }

    @AfterClass
    public static void closeBrowser()
    {
        driver.close();
    }

}