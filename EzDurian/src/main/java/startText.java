import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URL;

public class startText {

    public static void main(String[] args) throws Exception {
        startText start = new startText();
        start.StartSession("KC");
    }

    public void StartSession(String text) throws Exception {

        //trying docker selenium standalone
        String remoteUrl = "http://localhost:4444/";
        WebDriver driver ;
        //ChromeDriver driver;
        //the profile path can be found : chrome://version
        String profileDir = readConfig().getProfile();
        String contact = readConfig().getContact();

        if(profileDir == null) {
            System.out.println("use default");
            ChromeOptions options = new ChromeOptions();
            driver = new RemoteWebDriver(new URL(remoteUrl),options);
        }else {
            System.out.println("Read config file from: " + profileDir);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("user-data-dir="+profileDir);

            //option.setHeadless(true);

            driver = new RemoteWebDriver(new URL(remoteUrl),options);
        }

        driver.get("https://web.whatsapp.com/");
        //To wait for element visible
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@class=\"_3e4VU\"]")));

        SeleniumFindContact(driver,contact);
        //SeleniumSendText(driver,text);

        driver.quit();

    }

    public void SeleniumFindContact(WebDriver driver, String name) throws Exception {
        //searchBox
        WebElement boxSearch = driver.findElement(By.xpath("//button[@class=\"_3e4VU\"]"));
        //click search button
        boxSearch.click();

        WebElement searchContact = driver.findElement(By.xpath("//div[@class=\"_3FRCZ copyable-text selectable-text\"]"));
        searchContact.sendKeys(name);
        System.out.println("Found "+ name);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[@title="+ "\"" + name + "\"" +"]")));

        //open chat with the contact
        WebElement targetContact = driver.findElement(By.xpath("//span[@title="+ "\"" + name + "\"" +"]"));
        targetContact.click();
        System.out.println("Open chat");
    }

    public void SeleniumSendText(WebDriver driver, String text) throws Exception {
        //get textBox and send message
        WebElement textBox = driver.findElement(By.xpath("//div[@class=\"_3FRCZ copyable-text selectable-text\"][@contenteditable=\"true\"][@data-tab=\"1\"]"));

        textBox.sendKeys(text +"\n");
        System.out.println("Sent : "+ text);
        Thread.sleep(1000);
    }

    public config readConfig() throws Exception {

        File file = new File("config");

        if (file.exists())
        {
            //get the config file and read the profile value, this is in windows
            File configFile = new File(file.getPath()+"\\config.yaml");
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            config savedData = om.readValue(configFile, config.class);

            String profile = savedData.getProfile();
            return savedData;
        }
        else
        {
            System.out.println("no config file exist!");
            return null;
        }

    }

    public static class config {

        private String profile;
        private String contact;

        public config(){}
        public config(String profile, String contact) {

            this.profile = profile;
            this.contact = contact;

        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
        public String getProfile() {
            return profile;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }
        public String getContact() {
            return contact;
        }

    }

}
