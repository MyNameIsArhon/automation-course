package auto;

import com.github.javafaker.Faker;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FakerGenerTest {

    Faker faker = new Faker();

    @Test
    void testFakerGenerate() {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            String expectedName = faker.name().fullName();
            Locator actualName = page.locator(".large-10.columns");

            page.route("**/dynamic_content", route -> {
                route.fulfill(new Route.FulfillOptions()
                        .setBody("<div class='large-10 columns'>" + expectedName + "</div>"));
                    });

            page.navigate("https://the-internet.herokuapp.com/dynamic_content");
            assertThat(actualName).hasText(expectedName);
        }
    }
}
