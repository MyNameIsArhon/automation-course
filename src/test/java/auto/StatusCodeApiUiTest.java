package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusCodeApiUiTest {

    private static Playwright playwright;
    private static APIRequestContext apiRequest;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();

        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com"));
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
        page.navigate("https://the-internet.herokuapp.com/status_codes",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }

    @ParameterizedTest
    @CsvSource({
            "200",
            "404"
    })
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
        Response response = page.waitForResponse((res -> res.url().endsWith("/status_codes/%d".formatted(statusCode))),
                () -> page.getByText(String.valueOf(statusCode)).click());
        return response.status();
    }

    @AfterEach
    void tearDown() {
        page.close();
        context.close();
    }

    @AfterAll
    static void tearDownClass() {
        apiRequest.dispose();
        browser.close();
        playwright.close();
    }
}

