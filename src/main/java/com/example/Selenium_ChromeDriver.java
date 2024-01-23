package com.example;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

public class Selenium_ChromeDriver {
    public static ChromeDriver CreateDriverBystr(String input_url) {
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
