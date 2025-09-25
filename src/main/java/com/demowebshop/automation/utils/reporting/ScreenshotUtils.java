package com.demowebshop.automation.utils.reporting;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling screenshot operations
 */
public class ScreenshotUtils {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    static {
        // Create screenshots directory if it doesn't exist
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
    }

    /**
     * Take screenshot and save to file
     * @param driver WebDriver instance
     * @param testName Name of the test
     * @return Path to saved screenshot
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String fileName = String.format("%s_%s.png", testName, timestamp);
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            logger.info("Screenshot saved: {}", filePath);
            return filePath;

        } catch (IOException e) {
            logger.error("Failed to take screenshot: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while taking screenshot: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Take screenshot with custom file name
     * @param driver WebDriver instance
     * @param fileName Custom file name (without extension)
     * @return Path to saved screenshot
     */
    public static String takeScreenshotWithCustomName(WebDriver driver, String fileName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String fullFileName = String.format("%s_%s.png", fileName, timestamp);
            String filePath = SCREENSHOT_DIR + File.separator + fullFileName;

            File destFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destFile);

            logger.info("Screenshot saved with custom name: {}", filePath);
            return filePath;

        } catch (IOException e) {
            logger.error("Failed to take screenshot with custom name: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Take screenshot as byte array (useful for reports)
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] takeScreenshotAsBytes(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to take screenshot as bytes: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
}