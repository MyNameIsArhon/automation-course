package reportExtension;

import base.BaseTest;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CustomReportExtension implements TestWatcher, BeforeEachCallback, AfterEachCallback {
    private static final List<TestResult> results = new ArrayList<>();
    private long startTime;

    @Override
    public void beforeEach(ExtensionContext context) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterEach(ExtensionContext context) {
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        results.add(new TestResult(
                context.getDisplayName(),
                "Passed",
                System.currentTimeMillis() - startTime,
                null,
                null
        ));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String screenshotPath = "screenshots/" + context.getDisplayName() + ".png";
        Page page = ((BaseTest) context.getRequiredTestInstance()).getPage();
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));

        results.add(new TestResult(
                context.getDisplayName(),
                "Failed",
                System.currentTimeMillis() - startTime,
                cause.getMessage(),
                screenshotPath
        ));
    }

    public static List<TestResult> getResults() {
        return results;
    }
}

class TestResult {
    String name;
    String status;
    long duration;
    String error;
    String screenshot;

    public TestResult(String name, String status, long duration, String error, String screenshot) {
        this.name = name;
        this.status = status;
        this.duration = duration;
        this.error = error;
        this.screenshot = screenshot;
    }
}
