package com.example;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.QuoteReply;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class rise extends SimpleListenerHost {
    static String[] commandList = new String[]{"rise"};

    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) {
        String s = event.getMessage().contentToString();
        if (s.startsWith(commandList[0])) {
            s = s.replace(commandList[0], "");
            if (!s.trim().equals("")) {
//                setEvent(event);
                try {
//            System.out.println(s);
                    event.getSubject().sendMessage("正在查询 " + s);
                    Search(s, event);
                } catch (Exception e) {
//				throw new RuntimeException(e);
                    sendError(e,event);
                }
            }
        }
        return ListeningStatus.LISTENING;
    }

    public static void sendError(Exception e,MessageEvent event) {
        event.getSubject().sendMessage(String.valueOf(e));
    }


    public static void Search(String input, MessageEvent event) throws Exception {
        String u = "https://mhrise.kiranico.com/zh";
        System.setProperty("webdriver.chrome.driver", "D:/Downloads/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--log-level=3");
//		options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");


        ChromeDriver driver = new ChromeDriver(options);
        driver.get(u);
        Thread.sleep(100);
        driver.findElement(By.xpath("/html/body/header/div/div/div[3]/button")).click();
        Thread.sleep(200);
        driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/input")).sendKeys(input);
        Thread.sleep(2000);
        String has = driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/div[2]/p")).getText();
        if (has.startsWith("No results found for")) {
//            System.out.println("No results found for "+input);
            event.getSubject().sendMessage(new QuoteReply(event.getSource()).plus("可能输入有误"));
        } else {
            driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/ul/a[1]")).click();
            String url = driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/nav/div/div[1]/div[2]/a")).getAttribute("href");
//		    System.out.println(url);
            driver.get(url);
            ((JavascriptExecutor) driver).executeScript("document.getElementsByClassName(\"sticky top-0 z-10 bg-white dark:bg-[#161B22] dark:backdrop-blur dark:[@supports(backdrop-filter:blur(0))]:bg-[#161B22]/75\")[0].outerHTML = null");

            Image image = Contact.uploadImage(event.getSubject(), WebImg.extracted("/html/body/div[4]/div[2]/article",driver));
            event.getSubject().sendMessage(image.plus(new At(event.getSender().getId())));
        }
        driver.quit();
    }

}
