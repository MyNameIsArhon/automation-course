package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicLoadingTraceTest {

    @Test
    void testDynamicLoadingWithTrace() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true));

            page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1",
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

            page.click("button");
            Locator finishElement = page.locator("#finish");
            finishElement.waitFor();

            assertThat(finishElement).hasText("Hello World!");

            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("trace-dynamic-loading.zip")));
        }
    }
}
