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
import java.io.PrintStream;
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
    TestData testData = new TestData();

    //Attributes of the JSON file
    public String srcURL, srcHtmlShape, trigger, dstURL, dstHtmlShape;

    //Content of source and destination HTML document
    String srcHtml, dstHtml;

    FileWriter file = null;

    @BeforeClass
    public static void openBrowser(){
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        driver.manage().window().maximize();
    }
    @Test
    public void loginPageTest() throws Exception, InterruptedException {

        driver.get(loginPage);
        srcHtml = driver.getPageSource();

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.xpath("//*[@id='login-button']")).click();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dstHtml = driver.getPageSource();

        String actualURL = driver.getCurrentUrl();
        System.out.println(actualURL);
        Assert.assertEquals(expectedURL, actualURL);
        System.out.println("Login test passed.");

        //Build JSON record: ID PAGE (= URL + HTML SHAPE)
        srcURL       = loginPage;
        srcHtmlShape = computeIdentifyingShape(srcHtml);
        trigger      = "xpath: //*[@id='login-button']";
        dstURL       = actualURL;
        dstHtmlShape = computeIdentifyingShape(dstHtml);

        testData = new TestData(srcURL, srcHtmlShape, trigger, dstURL, dstHtmlShape);
        dataList.add(testData.buildJsonObject());
    }
    @After
    public void mainPageTest() throws Exception{

        List<String> urlLinks = getUrlInALinks(dstHtml);
        System.out.println(urlLinks.toString());

        srcURL       = dstURL;
        srcHtmlShape = dstHtmlShape;

        for (String link:urlLinks){
            if (!link.contains("#") && !link.isEmpty()) {
                driver.get(link);
                System.out.println(driver.getCurrentUrl());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                trigger      = link;
                dstURL       = driver.getCurrentUrl().toString();
                dstHtmlShape = computeIdentifyingShape(driver.getPageSource());

                testData =  new TestData(srcURL, srcHtmlShape, trigger, dstURL, dstHtmlShape);
                dataList.add(testData.buildJsonObject());

                driver.navigate().back();
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        srcURL       = dstURL;
        srcHtmlShape = dstHtmlShape;
        List<String> idLinks = getIdInLinks(dstHtml);
        System.out.println(idLinks.toString());
        String cssSelect=null;
        for (String link:idLinks){
            if (!link.contains("sidebar") && !link.isEmpty()) {
                cssSelect = "a[id='"+link+"']";
                driver.findElement(By.cssSelector(cssSelect)).click();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                trigger      = "css_selector:" + link;
                dstURL       = driver.getCurrentUrl().toString();
                dstHtmlShape = computeIdentifyingShape(driver.getPageSource());

                testData = new TestData(srcURL, srcHtmlShape, trigger, dstURL, dstHtmlShape);
                dataList.add(testData.buildJsonObject());

                System.out.println(trigger);

                driver.navigate().back();
            }
        }
        addToJsonFile(dataList);
    }
    public void assertTitle() {
        String actualTitle = driver.getTitle();
        System.out.println(actualTitle);
        Assert.assertEquals("Swag Labs", actualTitle);
        System.out.println("Title test passed.");
    }
    @AfterClass
    public static void closeBrowser(){
        driver.close();
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
    public static List<String> getIdInLinks(String html) {
        Document document = Jsoup.parse(html);
        return document.getElementsByTag("a")
                .stream()
                .map(link -> link.attr("id"))
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
}