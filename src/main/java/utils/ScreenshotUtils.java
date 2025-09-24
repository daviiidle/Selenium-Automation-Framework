package utils;

import config.ConfigurationManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    public static String captureScreenshot(WebDriver driver, String testName) {
        if (!config.isScreenshotOnFailure() || driver == null) {
            return null;
        }

        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            if (screenshot == null) {
                logger.warn("Driver does not support taking screenshots");
                return null;
            }
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_" + timestamp + ".png";
            String screenshotPath = config.getScreenshotsPath() + "/" + fileName;

            File destFile = new File(screenshotPath);
            destFile.getParentFile().mkdirs();

            FileUtils.copyFile(sourceFile, destFile);

            logger.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath;

        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    public static String captureElementScreenshot(WebDriver driver, WebElement element, String testName) {
        try {
            File sourceFile = element.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_element_" + timestamp + ".png";
            String screenshotPath = config.getScreenshotsPath() + "/" + fileName;

            File destFile = new File(screenshotPath);
            destFile.getParentFile().mkdirs();

            FileUtils.copyFile(sourceFile, destFile);

            logger.info("Element screenshot captured: {}", screenshotPath);
            return screenshotPath;

        } catch (IOException e) {
            logger.error("Failed to capture element screenshot for test: {}", testName, e);
            return null;
        }
    }
}