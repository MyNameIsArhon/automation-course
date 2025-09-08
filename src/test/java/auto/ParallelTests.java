package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class ParallelTests {

    @Test
    void testAddElement() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {
            page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

            page.click("[onclick='addElement()']");
            Assertions.assertTrue(page.locator("[onclick='deleteElement()']").isVisible());
        }
    }

    @Test
    void testRemoveElement() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {
            page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

            page.click("[onclick='addElement()']");
            page.click("[onclick='deleteElement()']");
            Assertions.assertFalse(page.locator("[onclick='deleteElement()']").isVisible());
        }
    }
}