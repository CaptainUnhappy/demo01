package com.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class WebImg {
    public static InputStream extracted(String screenXpath, ChromeDriver driver) throws InterruptedException, IOException {
        //找图表
        WebElement img = driver.findElement(By.xpath(screenXpath));
//        Thread.sleep(3000);
        // 通过执行脚本解决Selenium截图不全问题
        long maxWidth = (long) driver.executeScript(
                "return Math.max(document.body.scrollWidth, document.body.offsetWidth, document.documentElement.clientWidth, document.documentElement.scrollWidth, document.documentElement.offsetWidth);");
        long maxHeight = (long) driver.executeScript(
                "return Math.max(document.body.scrollHeight, document.body.offsetHeight, document.documentElement.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);");
        Dimension targetSize = new Dimension((int) maxWidth, (int) maxHeight);
        driver.manage().window().setSize(targetSize);
        //获取窗口高度
        Object o1 = ((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight");
        int windowsHeight = Integer.parseInt(String.valueOf(o1));

        //图片位置
        Point location = img.getLocation();
        //图片y坐标
        int y = location.getY();
        //窗口滑动高度
        int move = y + img.getSize().height > windowsHeight ? y + img.getSize().height - windowsHeight : 0;
        //滑动窗口
        if (move > 0) {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + move + ")");
        }
//        Thread.sleep(2000);
        File imgPath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        return Files.newInputStream(captureElement(imgPath, img, move).toPath());
//        SendImageFile(Files.newInputStream(captureElement(imgPath, img, move).toPath()));
//        imgPath.deleteOnExit();
    }

    public static File captureElement(File filePath, WebElement element, int move) throws IOException {
        BufferedImage img = ImageIO.read(filePath);
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        Point point = element.getLocation();
        //从元素左上角坐标开始，按照元素的高宽对img进行裁剪为符合需要的图片
        BufferedImage dest = img.getSubimage(point.getX(), point.getY() - move, width, height);
        ImageIO.write(dest, "png", filePath);
        return filePath;
    }
}
