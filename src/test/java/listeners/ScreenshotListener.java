package listeners;

import drivers.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;

public class ScreenshotListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(ScreenshotListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("Test failed: {}", result.getMethod().getMethodName());

        try {
            String testName = result.getTestClass().getName() + "_" + result.getMethod().getMethodName();
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                WebDriverFactory.getDriver(),
                testName + "_failure"
            );

            if (screenshotPath != null) {
                logger.info("Screenshot captured for failed test: {}", screenshotPath);
                // Set screenshot path as system property for other listeners
                System.setProperty("screenshot.path", screenshotPath);
            }
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: {}", result.getMethod().getMethodName(), e);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.debug("Test passed: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Test skipped: {}", result.getMethod().getMethodName());
    }
}