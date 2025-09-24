package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import config.ConfigurationManager;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.ScreenshotUtils;
import drivers.WebDriverFactory;

import java.io.File;

public class ExtentReportListener implements ITestListener {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static ConfigurationManager config = ConfigurationManager.getInstance();

    @Override
    public void onStart(org.testng.ITestContext context) {
        String reportPath = config.getReportsPath() + "/ExtentReport.html";
        File reportDir = new File(config.getReportsPath());
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("DemoWebShop Automation Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // System Information
        extent.setSystemInfo("Application", "DemoWebShop");
        extent.setSystemInfo("Environment", config.getEnvironment());
        extent.setSystemInfo("Browser", config.getBrowser());
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName(),
                                          result.getMethod().getDescription());
        extentTest.set(test);

        // Add test groups/categories
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            for (String group : groups) {
                extentTest.get().assignCategory(group);
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTest.get().log(Status.PASS, "Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        extentTest.get().log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());

        // Capture screenshot on failure
        try {
            String screenshotPath = ScreenshotUtils.captureScreenshot(
                WebDriverFactory.getDriver(),
                result.getMethod().getMethodName() + "_failed"
            );
            if (screenshotPath != null) {
                extentTest.get().addScreenCaptureFromPath(screenshotPath);
            }
        } catch (Exception e) {
            extentTest.get().log(Status.WARNING, "Failed to capture screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
    }

    @Override
    public void onFinish(org.testng.ITestContext context) {
        extent.flush();
    }

    public static ExtentTest getExtentTest() {
        return extentTest.get();
    }
}