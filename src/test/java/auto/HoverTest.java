package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HoverTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        Locator figures = page.locator(".figure");
        int count = figures.count();

        for (int i = 0; i < count; i++) {
            Locator figure = figures.nth(i);
            figure.hover();

            Locator profileLink = figure.locator("text=View profile");
            assertThat(profileLink).isVisible();
            assertThat(profileLink).hasText("View profile");

            profileLink.click();
            assertThat(page).hasURL("https://the-internet.herokuapp.com/users/%s".formatted(i+1));

            page.goBack();
        }
    }

    @AfterAll
    static void teardownClass() {
        browser.close();
        playwright.close();
    }


    @AfterEach
    void teardow() {
        context.close();
    }

}
