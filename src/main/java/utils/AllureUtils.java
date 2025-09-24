package utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class AllureUtils {
    private static final Logger logger = LogManager.getLogger(AllureUtils.class);

    @Attachment(value = "Screenshot", type = "image/png")
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for Allure", e);
            return new byte[0];
        }
    }

    @Attachment(value = "Page Source", type = "text/html")
    public static String capturePageSource(WebDriver driver) {
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            logger.error("Failed to capture page source for Allure", e);
            return "Failed to capture page source";
        }
    }

    public static void attachScreenshot(WebDriver driver, String name) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure", e);
        }
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void step(String stepName) {
        Allure.step(stepName);
        logger.info("Allure step: {}", stepName);
    }

    public static void step(String stepName, Allure.ThrowableRunnableVoid runnable) {
        Allure.step(stepName, runnable);
        logger.info("Allure step completed: {}", stepName);
    }
}