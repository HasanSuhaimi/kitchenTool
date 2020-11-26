import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class startApp {

    public static void main(String[] args) throws Exception {
        startApp start = new startApp();
        try
        {
            start.SeleniumPulldata();
        }
        catch (Exception e) {

            System.out.println(e);
            System.exit(0);
        }
    }

    public void SeleniumLogin(WebDriver driver) throws Exception {
        //get webpage and login
        driver.get("https://ezydurian.onpay.my/admin/login");

        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));

        WebElement loginButton = driver.findElement(By.name("login"));

        username.sendKeys("Ezyduriansales@gmail.com");
        password.sendKeys("ezydurian123");

        loginButton.click();

    }
    
    public void SeleniumForm() throws Exception {
        
        String CHROMEDRIVER_PATH = "/usr/local/bin/chromedriver";
        System.setProperty("webdriver.chrome.driver",CHROMEDRIVER_PATH);
        //set the browser in the background
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
       
        WebDriver driver = new ChromeDriver(options);

        SeleniumLogin(driver);
        
        try
        {
            //get webpage and login
            driver.get("https://ezydurian.onpay.my/admin/forms/edit/46");

            WebElement pilihan = driver.findElement(By.xpath("//a[@href=\"#t-options\"]"));

            pilihan.click();

            WebElement minisiteBox = driver.findElement(By.xpath("//input[@id=\"minisite_enabled\"]"));
            Actions actions = new Actions(driver);
            actions.moveToElement(minisiteBox).click().build().perform();
            Thread.sleep(500);

            WebElement savedBox = driver.findElement(By.xpath("//button[@type=\"submit\"]"));
            savedBox.click();
            Thread.sleep(2500);

            driver.quit();
        }
        catch (Exception e) {

            System.out.println(e);
            driver.quit();
        }

    }

    public void SeleniumPulldata() throws Exception {

        System.out.println("Starting");
        
        String CHROMEDRIVER_PATH = "/usr/local/bin/chromedriver";
        System.setProperty("webdriver.chrome.driver",CHROMEDRIVER_PATH);
        //set the browser in the background
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        options.setBinary("/usr/bin/google-chrome");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        
        //trying docker selenium standalone
        String remoteUrl = "http://localhost:4444/";
        //WebDriver driver = new RemoteWebDriver(new URL(remoteUrl),options);
        //make sure to pass options as the parameter
        WebDriver driver = new ChromeDriver(options);

        //start bot
        telegramBot bot = new telegramBot();
        
        SeleniumLogin(driver);

        try
        {
        driver.get("https://ezydurian.onpay.my/admin/reports/sales");

        driver.findElement(By.name("c[invoice_number]")).click();
        driver.findElement(By.name("c[client_phone_number]")).click();
        driver.findElement(By.name("c[client_address]")).click();
        driver.findElement(By.name("c[extra_field_1]")).click();
        driver.findElement(By.name("c[extra_field_2]")).click();
        driver.findElement(By.name("c[products]")).click();
        driver.findElement(By.name("c[confirmed_at]")).click();
        driver.findElement(By.name("c[status]")).click();

        //specify status jualan = disahkan
        Select statusJualan = new Select(driver.findElement(By.xpath("//select[@class=\"form-control\"]")));
        statusJualan.selectByVisibleText("Yang telah disahkan");

        //get today date
        WebElement date = driver.findElement(By.xpath("//input[@class=\"form-control\"]"));
        date.click();
        WebElement todayDate = driver.findElement(By.xpath("//th[@class=\"today\"]"));
        todayDate.click();

        //send np
        driver.findElement(By.id("fv")).sendKeys("NP");

        WebElement reportButton = driver.findElement(By.id("show"));

        //enter and get the data
        reportButton.sendKeys(Keys.ENTER);

        // get the whole table
        WebElement reportTable = driver.findElement(By.xpath("//table/tbody"));

        // make a list of all rows (/tr) in the table
        List <WebElement> rows_table = reportTable.findElements(By.tagName("tr"));
        int table_size = rows_table.size();
        
        dataFile datafile = checkDataAmount();
        int savedAmount = datafile.getDatas().size();

        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //check existence data, if no new data, end chromedriver session
        if(table_size != savedAmount) {
            System.out.println(nowDate + ": New order, updated file");
            //initiate a new list to store invoice from the web
            List <String> invoicesL = new ArrayList<String>();
            // initiate a list of data
            List <data> datas = new ArrayList<data>();
            //store data in a list (with the latest first)
            for(int x =0 ; x < table_size; x++)
            {
                //for each row, get all the columns
                List <WebElement> column_row = rows_table.get(x).findElements(By.tagName("td"));

                String index = column_row.get(0).getText();
                String invoice = column_row.get(1).getText();
                String name = column_row.get(2).getText();
                //skip email
                String number = column_row.get(4).getText();
                String adress = column_row.get(5).getText();
                String field1 = column_row.get(6).getText();
                String field2 = column_row.get(7).getText();
                String product = column_row.get(8).getText();
                String total = column_row.get(9).getText();
                String entered_at = column_row.get(10).getText();
                String confirmed_at = column_row.get(11).getText();
                String status = column_row.get(12).getText();

                data Data = new data(index,invoice,name,number,adress,field1,field2,product,total,entered_at,confirmed_at,status);
                invoicesL.add(invoice);
                datas.add(Data);
            }
            
            driver.quit();
            //convert the stored data invoice List to Set
            Set<String> dataSet = new HashSet<>(datafile.getInvoices());

            for (int x = table_size; x > 0; x--) {
                    String el = invoicesL.get(x-1);
                    //System.out.println(el);

                    if (!dataSet.contains(el)) {

                        //get index of the missing invoice in the list from web
                        int indexOfMissingInv = invoicesL.indexOf(el);

                        //get the String index of the targeted element
                        String index = datas.get(indexOfMissingInv).getIndex();
                        int itemIndex = Integer.parseInt(index) - 1;

                        data dataItem = datas.get(itemIndex);

                        // read saved data (yaml file, db etc)
                        dataFile File = checkDataAmount();
                        //arguments: new data, current stored datas, new invoice, current stored list, yaml file
                        appendDataFile(dataItem, File.getDatas(), el, File.getInvoices(),"data.yaml");

                        //send the missing invoice to telegram
                        String baseName = datas.get(indexOfMissingInv).getName();
                        String name = URLEncoder.encode(baseName,"UTF-8");
                        
                        String baseProduct = datas.get(indexOfMissingInv).getProduct();
                        String product = URLEncoder.encode(baseProduct,"UTF-8");
                        //String product = datas.get(indexOfMissingInv).getProduct();
                        
                        String contNumber = URLEncoder.encode(datas.get(indexOfMissingInv).getNumber(),"UTF-8");
                        String address = URLEncoder.encode(datas.get(indexOfMissingInv).getAdress(),"UTF-8");
                        String field1 = URLEncoder.encode(datas.get(indexOfMissingInv).getField1(),"UTF-8");
                        String field2 = URLEncoder.encode(datas.get(indexOfMissingInv).getField2(),"UTF-8");

                        String confirmedAt = URLEncoder.encode(datas.get(indexOfMissingInv).getConfirmed_at(),"UTF-8");

                        //globalIndex will be the size of stored data
                        int globalIndex = File.getDatas().size();

                        // add try block
                        try {
                             
                        bot.sendToTelegram("<b>NEW ORDER: "+"NP. "+globalIndex+"!</b>" + "%0Atotal: " + table_size +
                                ",%0A <b>Confirmed at :</b> "+ confirmedAt +
                                ",%0A <b>Recipient:</b> NP. "+globalIndex + " "+ name +
                                ",%0A <b>Product :</b> "+ product +
                                ",%0A <b>Alternative no :</b> "+ field2 +
                                ",%0A <b>Delivery info :</b> "+ field1 +
                                ",%0A <b>Recipient address :</b> "+ address );

                        } catch (Exception e) {
                           bot.sendToTelegram("<b>NEW ORDER: "+"NP. "+globalIndex+" pending!</b>" + "%0Atotal: " + table_size);
                           
                           String URL = URLEncoder.encode("<b>NEW ORDER: "+"NP. "+globalIndex+"!</b>" + "\ntotal: " + table_size +
                                ",\n <b>Confirmed at :</b> "+ confirmedAt +
                                ",\n <b>Recipient:</b> NP. "+globalIndex + " "+ baseName +
                                ",\n <b>Product :</b> "+ baseProduct +
                                ",\n <b>Alternative no :</b> "+ field2,"UTF-8");
                           
                           bot.sendToTelegramPre(URL);   
                           bot.sendToTelegramPre("Errors please check the server");    
                           System.out.println(e);
                           e.printStackTrace();
                           
                        }
                        
                    }
                }
        }
        //
        else {
            System.out.println(nowDate + ": No new order");
            driver.quit();
        }
        
        }
        catch (Exception e) {
            bot.sendToTelegramPre("errors alert :"+e.toString());
            System.out.println("errors alert :"+e);
            driver.quit();
        }

    }

    public void appendDataFile(startApp.data newData, List<data> currentDatas,String invoice, List<String> currentInvoices, String yamlFileName) throws Exception {

        File file = new File("/home/dev/Kitchentool/EzDurian/Record");
        //get the config file and read the profile value, this is in windows
        File DataFile = new File(file.getPath()+"/data.yaml");

        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        
        currentDatas.add(newData);
        currentInvoices.add(invoice);
        
         //initiate datafile class
        startApp.dataFile datafile = new startApp.dataFile("",currentDatas,currentInvoices);
        // map the data to the yaml file
        om.writeValue(DataFile, datafile);
    }

    public dataFile checkDataAmount() throws Exception {

        File file = new File("/home/dev/Kitchentool/EzDurian/Record");

        File yamlFile = new File(file.getPath()+"/data.yaml");
        
        if (yamlFile.exists())
        {
            //get the yaml file and read the value, this is in windows
            System.out.println("File exist, path: " + yamlFile.getAbsolutePath());
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            dataFile savedData = om.readValue(yamlFile, dataFile.class);
            
            //return the file
            return savedData;
        }
        else
        {
            System.out.println("Creating new data.yaml file");

            String date = new SimpleDateFormat("dd-MMM-YYYY").format(new Date());
            //initiate datafile class
            dataFile datafile = new dataFile(date,new ArrayList<data>(),new ArrayList<String>());

            //create new data.yaml inside the file folder Record
            File tmpFile = new File(file, "data.yaml");
            tmpFile.createNewFile();
            // ObjectMapper is instantiated just like before
            ObjectMapper om = new ObjectMapper(new YAMLFactory());
            // map the data to the yaml file
            om.writeValue(tmpFile, datafile);

            return datafile;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class data {

        private int globalIndex = 0;

        private String index;
        private String invoice;
        private String name;
        private String number;
        private String adress;
        private String field1;
        private String field2;
        private String product;
        private String total;
        private String entered_at;
        private String confirmed_at;
        private String status;

        public data () {}
        public data (String index, String invoice, String name, String number, String adress, String field1,
                     String field2, String product, String total, String entered_at, String confirmed_at, String status)
        {
            this.index = index;
            this.invoice = invoice;
            this.name = name;
            this.number = number;
            this.adress = adress;
            this.field1 = field1;
            this.field2 = field2;
            this.product = product;
            this.total = total;
            this.entered_at = entered_at;
            this.confirmed_at = confirmed_at;
            this.status = status;
        }

        public void setGlobalIndex (int index) {this.globalIndex = index;}
        public int getGlobalIndex () {return globalIndex;}

        public void setIndex (String value) {this.index = value;}
        public String getIndex () { return index; }

        public void setInvoice (String value) {this.invoice = value;}
        public String getInvoice () { return invoice; }

        public void setName (String value) {this.name = value;}
        public String getName () { return name; }

        public void setNumber (String value) {this.number = value;}
        public String getNumber () { return number; }

        public void setAdress (String value) {this.adress = value;}
        public String getAdress () { return adress; }

        public void setField1 (String value) {this.field1 = value;}
        public String getField1 () { return field1; }

        public void setField2 (String value) {this.field2 = value;}
        public String getField2 () { return field2; }

        public void setProduct (String value) {this.product = value;}
        public String getProduct () { return product; }

        public void setTotal(String total) {
            this.total = total;
        }
        public String getTotal() {
            return total;
        }

        public void setEntered_at(String entered_at) {
            this.entered_at = entered_at;
        }
        public String getEntered_at() {
            return entered_at;
        }

        public void setConfirmed_at (String value) {this.confirmed_at = value;}
        public String getConfirmed_at () { return confirmed_at; }

        public void setStatus (String value) {this.status = value;}
        public String getStatus () { return status; }

        public String getTextMessage () {

            String textMessage="Whatsapp / SMS merchant\n" +
                    "delivery request form\n" +
                    "1. Sama-Sama Lokal by Maybank\n" +
                    "2. Restaurant / Gerai : ezydurian\n\n" +
                    "Order:\n" +
                    "1. Recipient name: "+"NP-"+ index +" "+ name + "\n" +
                    "2. Recipient contact number: "+ number + "\n" +
                    "3. Delivery address: "+ adress + "\n" +
                    "4. Pick up time: now \n\n" +
                    "Thanks team Maybank  :)";

            return textMessage;
        }

        public Action getTextAction (Actions action, WebElement textBox) {

            //create a series of action
            Action texting = action.moveToElement(textBox).sendKeys("Whatsapp / SMS merchant").keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("delivery request form").keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("1. Sama-Sama Lokal by Maybank").keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("2. Restaurant / Gerai : ezydurian").keyDown(Keys.SHIFT).sendKeys("\n\n").
                    keyUp(Keys.SHIFT).sendKeys("Order:").keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("1. Recipient name: "+"NP-"+index +" "+ name).keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("2. Recipient contact number: "+ number).keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("3. Delivery address: "+ adress).keyDown(Keys.SHIFT).sendKeys("\n").
                    keyUp(Keys.SHIFT).sendKeys("4. Pick up time: now ").keyDown(Keys.SHIFT).sendKeys("\n\n").
                    keyUp(Keys.SHIFT).sendKeys("Thanks team Maybank :)\n")
                    .build();

            return texting;

        }

    }
   public static class dataFile {

        private String date;
        private List<String> invoices;
        private List<data> datas;

        public dataFile () {}
        public dataFile (String date, List<data> datas, List<String> invoices) {
            this.date = date;
            this.datas = datas;
            this.invoices = invoices;
        }

        public void setDate(String release) {
            this.date = release;
        }

        public void setDatas(List<data> datas) {
            this.datas = datas;
        }

        public void setInvoices(List<String> invoices) {
            this.invoices = invoices;
        }

        public String getDate() {
            return date;
        }

        public List<data> getDatas() {
            return datas;
        }

        public List<String> getInvoices() {
            return invoices;
        }

    }

}
