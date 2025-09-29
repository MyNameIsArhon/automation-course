package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MobileDynamicControlsTest {

    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch();
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {

        long startTime = System.currentTimeMillis();
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        Locator enableButton = page.locator("button:text('Enable')");
        Locator inputField = page.locator("[type='text']");
        enableButton.click();
        page.waitForCondition(inputField::isEnabled);
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 3000) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("slow-login-trace.zip")));
        }
        assertTrue(duration < 3000,
                "Ожидалось, что тест пройдет менее чем за 3 сек., но был пройден за %.2f".formatted(duration / 1000.0));
        assertTrue(inputField.isEnabled());
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
