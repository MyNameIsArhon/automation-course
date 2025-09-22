package auto;

import com.example.config.EnvironmentConfig;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class StatusCodeCombinedTest {

    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;
    private static final Path SCREENSHOTS_DIR = Paths.get("screenshots");
    private static final EnvironmentConfig config = ConfigFactory.create(EnvironmentConfig.class, System.getenv());

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.baseUrl()));
        browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        page = context.newPage();
        page.navigate(config.baseUrl() + "status_codes",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 404})
    public void testStatusCodesCombined(int statusCode) {
        int actualApiStatusCode = getApiStatusCode(statusCode);
        int actualUiStatusCode = getUiStatusCode(statusCode);

        assertThat(actualUiStatusCode)
                .as("Ожидалось, что статус код будет: %d, но получен -  %d", actualApiStatusCode, actualUiStatusCode)
                .isEqualTo(actualApiStatusCode);
    }

    private int getApiStatusCode(int statusCode) {
        return apiRequest.get("/status_codes/%d".formatted(statusCode)).status();
    }

    private int getUiStatusCode(int statusCode) {
        try {
            Locator link = page.getByText(String.valueOf(statusCode));
            Response response = page.waitForResponse((res -> res.url().endsWith("/status_codes/%d".formatted(statusCode))),
                    link::click);
            return response.status();
        } catch (Exception e) {
            String screenshotName = String.format("error_status_code_%d_%s.png",
                    statusCode, System.currentTimeMillis());
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(SCREENSHOTS_DIR.resolve(screenshotName))
                    .setFullPage(true));

            throw new RuntimeException("UI проверка упала для кода " + statusCode, e);
        }
    }

    @AfterEach
    void tearDown() {
        page.close();
        apiRequest.dispose();
        browser.close();
        playwright.close();
    }
}
