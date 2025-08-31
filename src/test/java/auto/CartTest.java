package auto;

import base.BaseTest;
import com.microsoft.playwright.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartTest {

    Playwright playwright;
    Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/")));
        page = context.newPage();
    }

    @Test
    void testCartActions() {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        // Добавление элемента
        page.click("[onclick='addElement()']");
        page.locator("#content").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_add.png")));

        // Удаление элемента
        page.click("[onclick='deleteElement()']");
        page.locator("#content").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("cart_after_remove.png")));
    }

    private Path getTimestampPath(String filename) {
        return Paths.get(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + "/" + filename);
    }

    private boolean testFailed(TestInfo testInfo) {
        return testInfo.getTags().contains("failed");
    }

    @AfterEach
    void teardown(TestInfo testInfo) {
        if (testFailed(testInfo)) {
            byte[] screenshot = page.screenshot();
            Allure.addAttachment(
                    "Screenshot on Failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        }
        context.close();
        browser.close();
        playwright.close();
    }
}
