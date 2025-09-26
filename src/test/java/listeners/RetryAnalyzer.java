package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Retry analyzer for handling flaky tests in parallel execution
 * Specifically designed to handle timeout exceptions from high-load parallel execution
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRY_COUNT = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            Throwable throwable = result.getThrowable();

            // Retry only for specific timeout exceptions during parallel execution
            if (throwable != null && shouldRetry(throwable)) {
                retryCount++;
                logger.warn("Retrying test {} due to timeout/parallel execution issue. Attempt {}/{}",
                    result.getMethod().getMethodName(), retryCount, MAX_RETRY_COUNT);

                // Add a small delay before retry to reduce resource contention
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the test should be retried based on the exception type
     * @param throwable The exception that caused the test failure
     * @return true if the test should be retried
     */
    private boolean shouldRetry(Throwable throwable) {
        String message = throwable.getMessage();

        // Retry for timeout-related exceptions common in parallel execution
        return message != null && (
            message.contains("timeout: Timed out receiving message from renderer") ||
            message.contains("TimeoutException") ||
            message.contains("WebDriverException") ||
            message.contains("timeout") ||
            message.contains("Session not created") ||
            message.contains("chrome not reachable")
        );
    }

    /**
     * Reset retry count for the next test
     */
    public void reset() {
        retryCount = 0;
    }
}