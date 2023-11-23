import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class SimpleTest {
    public static void main(String[] args) {
        //System.setProperty("webdriver.edge.edgedriver","resources/msedgedriver.exe");
        System.setProperty("webdriver.edge.chromedriver","resources/chromedriver.exe");

        //EdgeDriver driver = new EdgeDriver();
        ChromeDriver driver = new ChromeDriver();

        driver.get("https://www.ebay.com");
        driver.manage().window().maximize();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.quit();
    }
}
