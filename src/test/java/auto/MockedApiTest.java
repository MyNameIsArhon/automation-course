package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedApiTest {
    static Playwright playwright;
    static Browser browser;
    private BrowserContext context;
    private Page page;

    private static ApiButtonsService apiService;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();

        apiService = mock(ApiButtonsService.class);
        when(apiService.getCountButtons("row")).thenReturn("{\"count\": \"5\"}");
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
    }


    @Test
    void testUserProfileWithMockedApi() {

        String buttons = apiService.getCountButtons("row");
        long startTime = System.currentTimeMillis();
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        page.evaluate("(data) => { window.buttons = data; }", buttons);
        Object result = page.evaluate("() => window.buttons");
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 3000) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("slow-login-trace.zip")));
        }
        assertTrue(duration < 3000,
                "Ожидалось, что тест пройдет менее чем за 3 сек., но был пройден за %.2f".formatted(duration / 1000.0));
        assertNotNull(result);
        assertTrue(result.toString().contains("5"));
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
