package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.time.Duration;
import java.time.Instant;

public class TestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final ThreadLocal<Instant> testStartTime = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        testStartTime.set(Instant.now());
        logger.info("Test started: {}.{}",
                   result.getTestClass().getName(),
                   result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = Duration.between(testStartTime.get(), Instant.now()).toMillis();
        logger.info("Test passed: {}.{} (Duration: {}ms)",
                   result.getTestClass().getName(),
                   result.getMethod().getMethodName(),
                   duration);
        testStartTime.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        long duration = Duration.between(testStartTime.get(), Instant.now()).toMillis();
        logger.error("Test failed: {}.{} (Duration: {}ms) - Error: {}",
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName(),
                    duration,
                    result.getThrowable().getMessage());
        testStartTime.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test skipped: {}.{} - Reason: {}",
                   result.getTestClass().getName(),
                   result.getMethod().getMethodName(),
                   result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
        testStartTime.remove();
    }
}