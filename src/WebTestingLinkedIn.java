//https://www.traceable.ai/blog-post/how-to-test-api-security-a-guide-and-checklist
//https://developer.mozilla.org/en-US/docs/Web/HTML/Element    - HTML elements reference

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.List;

public class WebTestingLinkedIn {
    static String loginPage = "https://www.linkedin.com/login";
    static String userName = "mesfin.assres@gmail.com";
    static String passWord = "********";
    //public static WebDriver driver = new EdgeDriver();
    public static WebDriver driver = new ChromeDriver();

    public static void main(String[] args) {
        //Web driver setting
        //System.setProperty("webdriver.edge.driver", "resources/msedgedriver.exe");
        System.setProperty("webdriver.chrome.driver","resources/chromedriver.exe");
        driver.manage().window().maximize();
        //Instantiate an object
        WebTestingLinkedIn webTesting = new WebTestingLinkedIn();
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
        String homePage = "https://sites.google.com/site/mesfinassres/home";
        driver.navigate().to(website);

        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        int size = allLinks.size();
        System.out.println("Total no of links Available: " + size);
        String[] fileAddress = new String[100];

        for (int i = 0; i < size; i++) {
            fileAddress[i] = allLinks.get(i).getAttribute("href");
            System.out.println(fileAddress[i]);
        }
        for (int i = 0; i < size; i++) {
            //Delay
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Open Webpages
            if (fileAddress[i]!= homePage)
                driver.get(fileAddress[i]);
        }
        /*
        for (int i = 0; i < size; i++) {
            String fileAddress = allLinks.get(i).getAttribute("href");
            System.out.println(fileAddress);

            if (fileAddress.contains("links")) {
                driver.get(fileAddress);
            } else {
                getPage(allLinks.get(i).getAttribute("href"));
            }
        }

         */
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