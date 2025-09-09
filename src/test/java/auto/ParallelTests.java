package auto;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelTests {

    private static final Browser.NewContextOptions CONTEXT_OPTIONS = new Browser.NewContextOptions()
            .setViewportSize(1920, 1080);

    @Test
    void BrowserContext() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext(CONTEXT_OPTIONS);
             Page page = context.newPage()) {

            page.navigate("https://the-internet.herokuapp.com/login");
            assertEquals("Login Page", page.locator("h2").innerText());
        }
    }

    @Test
    void testAddRemoveElements() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext(CONTEXT_OPTIONS);
             Page page = context.newPage()) {

            page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
            page.click("button:text('Add Element')");
            assertTrue(page.isVisible("button.added-manually"));
        }
    }
}