package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class NewLoginTest {

    static private Playwright playwright;
    static private Browser browser;
    private BrowserContext context;
    private Page page;
    static private List<Cookie> allAuthCookies;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        try (Page page = browser.newPage()) {
            allAuthCookies = login(page);
        }
    }

    @BeforeEach
    void setup() {
        context = browser.newContext();
        context.addCookies(allAuthCookies);
        page = context.newPage();
    }

    @Test
    void testSecure() {

        page.navigate("https://the-internet.herokuapp.com/secure");
        assertThat(page.locator("h2")).hasText("Secure Area");
    }

    private static List<Cookie> login(Page page) {
        try {
            page.navigate("https://the-internet.herokuapp.com/login");
            page.locator("#username").fill("tomsmith");
            page.locator("#password").fill("SuperSecretPassword!");
            page.locator("button[type='submit']").click();
            return page.context().cookies("https://the-internet.herokuapp.com");
        } catch (Exception e) {
            System.out.println("Ошибка входа: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @AfterEach
    void teardown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();

    }
}
