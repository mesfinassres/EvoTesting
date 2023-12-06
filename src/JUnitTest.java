//https://www.traceable.ai/blog-post/how-to-test-api-security-a-guide-and-checklist
//https://developer.mozilla.org/en-US/docs/Web/HTML/Element    - HTML elements reference

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeVisitor;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class JUnitTest {
    public static WebDriver driver = new ChromeDriver();
    public static String loginPage = "https://www.saucedemo.com/";
    public static String expectedURL = "https://www.saucedemo.com/inventory.html";
    JSONObject jsonObject;
    JSONArray dataList = new JSONArray();

    FileWriter file = null;

    @BeforeClass
    public static void openBrowser(){
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        driver.manage().window().maximize();
    }
    @Test
    public void assertLogin() throws InterruptedException {
        String srcHtml, dstHtml;

        driver.get(loginPage);
        srcHtml = driver.getPageSource();

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.xpath("//*[@id='login-button']")).click();
        dstHtml = driver.getPageSource();

        String actualURL = driver.getCurrentUrl();
        System.out.println(actualURL);
        Assert.assertEquals(expectedURL, actualURL);
        System.out.println("Login test passed.");

        //ID PAGE (= URL + HTML SHAPE)
        TestData testData = new TestData(loginPage, computeIdentifyingShape(srcHtml), "xpath: //*[@id='login-button']", actualURL, computeIdentifyingShape(dstHtml));
        dataList.add(testData.buildJsonObject());

        //addData(testData.buildJsonObject());
        TestData testData2 = new TestData("loginPage", "computeIdentifyingShape", "xpath: //*[@id='login-button']", "actualURL", "computeIdentifyingShape");
        dataList.add(testData2.buildJsonObject());

        driver.findElement(By.cssSelector("a[id='item_4_title_link']")).click();
        System.out.println(driver.getCurrentUrl().toString());
    }
    @Test
    public void assertTitle() {
        String actualTitle = driver.getTitle();
        System.out.println(actualTitle);
        Assert.assertEquals("Swag Labs", actualTitle);
        System.out.println("Title test passed.");
    }
    @Test
    public void assertLinks() throws Exception{
        List<WebElement> allLinks = driver.findElements(By.tagName("a"));
        Assert.assertEquals(20, allLinks.size());
        System.out.println("# links test passed. Total no of links available: " + allLinks.size());
        printLinksID(allLinks);
    }
    @Test
    public void assertHTML() {
        String htmlContent = driver.getPageSource();
        String errorCheckResult = checkErrorsInHtml(htmlContent);
        List<String> urls = getUrlInALinks(htmlContent);
        String pathAndQueries = getPathAndQueries(expectedURL);
        String shape = computeIdentifyingShape(htmlContent);
        URL url = null;
        try {
            url = new URL(expectedURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean isLinkValid = checkLink(url);

        // Print the results
        System.out.println("Error Check Result:\n" + errorCheckResult);
        System.out.println("\nExtracted URLs:");
        for (String u : urls) {
            System.out.println(u);
        }
        System.out.println("\nPath and Queries: " + pathAndQueries);
        System.out.println("\nIdentifying Shape: " + shape);
        System.out.println("\nIs Link Valid? " + isLinkValid);
    }
    @After
    public void saveTest() throws Exception{
        addToJsonFile(dataList);
    }
    @AfterClass
    public static void closeBrowser(){
        driver.close();
    }

    public static String checkErrorsInHtml(String html) {
        Parser parser = Parser.htmlParser().setTrackErrors(1000);
        Jsoup.parse(html, "", parser);
        if (parser.getErrors().isEmpty()) {
            return null;
        }
        return "Number of HTML errors " + parser.getErrors().size() + "\n" + parser.getErrors().toString();
    }

    public static List<String> getUrlInALinks(String html) {
        Document document = Jsoup.parse(html);
        return document.getElementsByTag("a")
                .stream()
                .map(link -> link.attr("href"))
                .collect(Collectors.toList());
    }

    public static String getPathAndQueries(String url) {
        try {
            URL parsedUrl = new URL(url);
            return (parsedUrl.getPath() != null ? parsedUrl.getPath() : "/") +
                    (parsedUrl.getQuery() != null ? parsedUrl.getQuery() : "");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately based on your application's requirements
            return "INVALID_URL";
        }
    }

    public static String computeIdentifyingShape(String html) {
        try {
            Document document = Jsoup.parse(html);
            StringBuilder buffer = new StringBuilder(html.length());

            document.traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    String name = node.nodeName();
                    if ("#text".equals(name)) {
                        buffer.append("text");
                    } else {
                        buffer.append("<").append(name).append(node.attributes().asList().stream()
                                .map(attribute -> " " + attribute.getKey())
                                .collect(Collectors.joining())).append(">");
                    }
                }

                @Override
                public void tail(Node node, int depth) {
                    String name = node.nodeName();
                    if (!"#text".equals(name)) {
                        buffer.append("</").append(name).append(">");
                    }
                }
            });

            return buffer.toString();
        } catch (Exception e) {
            return "INVALID_HTML";
        }
    }

    public static boolean checkLink(URL url) {
        try {
            url.openConnection().connect();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void printLinksID(List<WebElement> allLinks){
        String fileAddress;

        for (int i = 0; i < allLinks.size(); i++) {
            fileAddress = allLinks.get(i).getAttribute("href");
            String getIDs = allLinks.get(i).getAttribute("id");
            String uniqueLinks = null;

            if (fileAddress != null) {
                if (getIDs.contains("item"))
                    uniqueLinks = fileAddress.replaceAll("#", "?id=") + allLinks.get(i).getAttribute("id").replaceAll("[^0-9]", "");
                else uniqueLinks = fileAddress;
                System.out.println(uniqueLinks);
            }
        }
    }

    public void addToJsonFile(JSONArray jsonArray){
        try {
            file = new FileWriter("testData.json");
            file.write(jsonArray.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}