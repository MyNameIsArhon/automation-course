package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

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

        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        page.evaluate("(data) => { window.buttons = data; }", buttons);

        Object result = page.evaluate("() => window.buttons");
        assertNotNull(result);
        assertTrue(result.toString().contains("5"));
    }

    @AfterEach
    void tearDown() {
        if(page != null) page.close();
        if(context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if(browser != null) browser.close();
        if(playwright != null) playwright.close();
    }
}
