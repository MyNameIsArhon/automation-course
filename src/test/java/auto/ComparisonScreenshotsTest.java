package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ComparisonScreenshotsTest {

    Playwright playwright;
    Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHomePageVisual() throws IOException {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        Path actual = Paths.get("actual.png");
        page.screenshot(new Page.ScreenshotOptions().setPath(actual));
        long mismatch = Files.mismatch(actual, Paths.get("src/test/java/auto/expected.png"));
        assertThat(mismatch).isEqualTo(-1);
    }

    @AfterEach
    void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
