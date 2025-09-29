package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        long startTime = System.currentTimeMillis();
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
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration > 3000) {
                        context.tracing().stop(new Tracing.StopOptions()
                                .setPath(Paths.get("slow-login-trace.zip")));
                    }
                    assertTrue(duration < 3000,
                            "Ожидалось, что тест пройдет менее чем за 3 сек., но был пройден за %.2f".formatted(duration / 1000.0));
                    assertThat(page).hasURL("https://the-internet.herokuapp.com" + path);
                }
            }
        }
    }
}
