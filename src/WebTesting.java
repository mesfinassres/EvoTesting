//https://www.traceable.ai/blog-post/how-to-test-api-security-a-guide-and-checklist
//https://developer.mozilla.org/en-US/docs/Web/HTML/Element    - HTML elements reference

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.IOException;
import java.util.List;

public class WebTesting {
    static String loginPage = "https://www.linkedin.com/login";
    static String userName = "mesfin.assres@gmail.com";
    static String passWord = "Lemahadas";
    public static WebDriver driver = new EdgeDriver();

    public static void main(String[] args) {
        //Web driver setting
        System.setProperty("webdriver.edge.driver", "resources/msedgedriver.exe");
        driver.manage().window().maximize();
        //Instantiate an object
        WebTesting webTesting = new WebTesting();

        //Launch website
        webTesting.openTestSite();
        
        //Invoke login method
        webTesting.login();

        try {
            webTesting.getPage(loginPage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Test URL
        System.out.print("URL ");
        String actualUrl="https://www.linkedin.com/feed/";
        String expectedUrl= driver.getCurrentUrl();

        webTesting.assertEquals(expectedUrl, actualUrl);

        //Test Title
        System.out.print("Title ");
        String actualTitle = "(13) Feed | LinkedIn";
        String expectedTitle = driver.getTitle();

        webTesting.assertEquals(expectedTitle, actualTitle);
        //System.out.println(driver.getTitle());

        webTesting.closeBrowser();
    }
    public void openTestSite(){
          driver.get(loginPage);
    }
    public void login(){
         WebElement username=driver.findElement(By.id("username"));
         WebElement password=driver.findElement(By.id("password"));
         WebElement login=driver.findElement(By.xpath("//button[text()='Sign in']"));
         username.sendKeys(userName);
         password.sendKeys(passWord);
         login.click();
    }
    public void getPage(String website) throws IOException {

        driver.navigate().to(website);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<WebElement> allLinks = driver.findElements(By.tagName("a"));

        System.out.println("Total no of links Available: " + allLinks.size());

        for (int i = 0; i < allLinks.size(); i++) {

            String fileAddress = allLinks.get(i).getAttribute("href");

            System.out.println(fileAddress);
            if (fileAddress.contains("Notifications")) {
                driver.get(fileAddress);
            } else {
                getPage(allLinks.get(i).getAttribute("href"));
            }
        }

    }
    public void assertEquals(String expectedVal, String actualVal){
           if(actualVal.equalsIgnoreCase(expectedVal)) {
             System.out.println("test passed");
           }
           else {
               System.out.println("test failed");
           }
    }
    public void closeBrowser(){
        driver.quit();
    }
}