package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicLoadingTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @Test
    void testDynamicLoading() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        page = context.newPage();

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        page.onResponse(response -> {
            if (response.status() == 200)
                System.out.println("Запрос прошел успешно");
        });

        page.click("button");
        Locator finishText = page.locator("#finish");
        finishText.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        assertThat(finishText).hasText("Hello World!");

        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace-success.zip")));
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}
