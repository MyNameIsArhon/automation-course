package auto;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Тесты для the-internet.herokuapp.com")
@Feature("Работа с JavaScript-алертами")
public class AdvancedReportingTest {

    private static ExtentReports extent;
    private Browser browser;
    private Playwright playwright;
    private BrowserContext context;
    private Page page;
    private ExtentTest test;

    @BeforeAll
    static void setupExtent() {
        ExtentSparkReporter reporter = new ExtentSparkReporter("target/extent-report/extent-report.html");
        reporter.config().setDocumentTitle("Playwright Report");
        extent = new ExtentReports();
        extent.attachReporter(reporter);
    }

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка JS Alert")
    @Description("Тест взаимодействия с JS Alert и проверка результата")
    @Severity(SeverityLevel.NORMAL)
    void testJavaScriptAlert() {
        test = extent.createTest("Тест страницы взаимодействия с JS Alert");
        try {
            navigateToAlertsPage();
            String alertMessage = foJsAlert();
            verifyResultText();
            captureSuccessScreenshot();
            logExtent(Status.PASS, "Тест успешно завершен с сообщением: " + alertMessage);
        } catch (AssertionFailedError error) {
            forTestFailure();
        }
    }

    @Step("Открыть страницу с алертами")
    private void navigateToAlertsPage() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        assertEquals("JavaScript Alerts", page.locator("h3").textContent(),
                "Страница должна содержать заголовок 'JavaScript Alerts'");
        logExtent(Status.INFO, "Страница с алертами загружена");
    }

    @Step("Обработать JS Alert")
    private String foJsAlert() {
        CompletableFuture<String> alertMessageFuture = new CompletableFuture<>();

        page.onDialog(dialog -> {
            String message = dialog.message();
            alertMessageFuture.complete(message);
            System.out.println("Alert text: " + dialog.message());
            dialog.accept();
        });

        page.click("button[onclick='jsAlert()']");
        logExtent(Status.INFO, "Клик по кнопке JS Alert выполнен");

        try {
            return alertMessageFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logExtent(Status.WARNING, "Ошибка при обработке алерта: " + e.getMessage());
            return "Не удалось получить текст алерта";
        }
    }

    @Step("Проверить текст результата")
    private void verifyResultText() {
        page.waitForCondition(() ->
                page.locator("#result").textContent().contains("successfully"));

        String resultText = page.locator("#result").textContent();
        assertEquals("You successfully clicked an alert", resultText,
                "Текст результата должен соответствовать ожидаемому");
        logExtent(Status.INFO, "Результирующий текст проверен: " + resultText);
    }

    private void captureSuccessScreenshot() {
        try {
            String screenshotName = "success-screenshot.png";
            Path screenshotPath = Paths.get("target/extent-report", screenshotName);
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Files.write(screenshotPath, screenshot);

            test.pass("Скриншот успешного выполнения",
                    MediaEntityBuilder.createScreenCaptureFromPath(String.valueOf(screenshotPath)).build());
            logExtent(Status.INFO, "Скриншот успешного выполнения сохранен в: " + screenshotPath);
        } catch (IOException e) {
            logExtent(Status.WARNING, "Не удалось сохранить скриншот: " + e.getMessage());
        }
    }

    private void forTestFailure() {
        byte[] failureScreenshot = page.screenshot();

        try (InputStream failureStream = new ByteArrayInputStream(failureScreenshot)) {
            Allure.addAttachment("Ошибка теста", "image/png", failureStream, ".png");
        } catch (Exception ex) {
            logExtent(Status.WARNING, "Не удалось добавить скриншот ошибки в Allure: " + ex.getMessage());
        }

    }

    private void logExtent(Status status, String message) {
        test.log(status, message);
    }

    @AfterEach
    void tearDown() {
        playwright.close();
    }

    @AfterAll
    static void tearDownClass() {
        extent.flush();
    }
}
