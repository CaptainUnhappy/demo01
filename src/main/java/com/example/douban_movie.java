package com.example;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.InputStream;
import java.util.Collections;

public class douban_movie extends SimpleListenerHost {

    // 注册事件监听器，当好友消息事件发生时调用onEvent方法
    @EventHandler
    private ListeningStatus onEvent(MessageEvent event) {
        // 获取消息内容
        String s = event.getMessage().contentToString().trim();
        if(s.startsWith("豆瓣id")){
            String id = s.replace("豆瓣id", "").trim();
            String baseUrl = "https://movie.douban.com/subject/";
            event.getSubject().sendMessage("豆瓣 "+id);
            try {
                Image image = Contact.uploadImage(event.getSubject(), ChromeDriverImageToInputStream(baseUrl + id));
                event.getSubject().sendMessage(image);
            } catch (Exception e) {
                event.getSubject().sendMessage(e.toString());
            }
        }
        return ListeningStatus.LISTENING;
    }

    private static InputStream ChromeDriverImageToInputStream(String url) throws Exception {
//        String url = "https://movie.douban.com/subject/35797709/";
        ChromeDriver driver = CreateDriverBystr(url);

        InputStream inputStream;
        try {
            driver.executeScript("          var node = document.querySelector(\"#content > h1\");\n" +
                                 "                var list=document.getElementsByClassName('article')[0];\n" +
                                 "                list.insertBefore(node,list.firstChild);\n" +
                                 "                list.style.padding='10px';\n" +
                                 "                                \n" +
                                 "                document.querySelector(\"#interest_sect_level\").remove();\n" +
                                 "                document.querySelector(\"#content > div > div.article > div.indent.clearfix > div.gtleft\").remove();\n" +
                                 "                document.querySelector(\"#content > div > div.article > div.indent.clearfix > div.rec-sec\").remove();\n" +
                                 "                document.querySelector(\"#comments-section\").remove();\n" +
                                 "                document.querySelector(\"#reviews-wrapper\").remove();\n" +
                                 "                document.querySelector(\"#content > div > div.article > div.section-discussion\").remove();");
            inputStream = WebImg.extracted(driver.findElement(By.className("article")), driver);
//            FileUtils.copyFile(file, new File("Screenshot.png"));
        } catch (Exception e) {
            throw new Exception("js error/可能是id错误");
        }
        driver.quit();
        return inputStream;
    }

    private static ChromeDriver CreateDriverBystr(String input_url) {
        ChromeDriver driver;
        System.setProperty("webdriver.chrome.driver", "D:/Downloads/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--log-level=OFF");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-data-dir=" + System.getProperty("java.io.tmpdir"));
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-logging"));

        driver = new ChromeDriver(options);
        driver.get(input_url);
        return driver;
    }
}
