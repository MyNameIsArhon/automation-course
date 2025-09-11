package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicControlsTest {

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
    void testDynamicCheckbox() {

        page.navigate("https://the-internet.herokuapp.com/dynamic_controls",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        Locator checkbox = page.locator("[type='checkbox']");
        Locator remove = page.locator("button >> text=Remove");
        Locator add = page.locator("button >> text=Add");
        Locator finalText = page.locator("text=It's gone!");

        remove.click();
        checkbox.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
        assertThat(finalText).isVisible();

        add.click();
        assertThat(checkbox).isVisible();
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
