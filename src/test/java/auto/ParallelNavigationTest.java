package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {

    @ParameterizedTest
    @CsvSource({
            "chromium, /",
            "chromium, /login",
            "chromium, /dropdown",
            "firefox, /",
            "firefox, /login",
            "firefox, /dropdown"
    })
    void testPageLoad(String browserType, String path) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType type = switch (browserType.toLowerCase()) {
                case "chromium" -> playwright.chromium();
                case "firefox" -> playwright.firefox();
                default -> throw new IllegalArgumentException("Неподдерживаемый браузер: " + browserType);
            };
            try (Browser browser = type.launch(new BrowserType.LaunchOptions().setHeadless(true))) {
                try (BrowserContext context = browser.newContext()) {
                    Page page = context.newPage();
                    page.navigate("https://the-internet.herokuapp.com" + path);
                    assertThat(page).hasURL("https://the-internet.herokuapp.com" + path);
                }
            }
        }
    }
}
