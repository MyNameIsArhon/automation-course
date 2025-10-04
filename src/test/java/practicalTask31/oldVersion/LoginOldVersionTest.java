package practicalTask31.oldVersion;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginOldVersionTest {

    @Test
    public void testLogin() {

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            page.navigate("https://the-internet.herokuapp.com/login");
            page.locator("#username").fill("tomsmith");
            page.locator("#password").fill("SuperSecretPassword!");
            page.locator("button[type='submit']").click();
            assertThat(page.locator(".flash.success")).isVisible();
        }
    }

    @Test
    public void testLoginIncorrectUsername() {

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            page.navigate("https://the-internet.herokuapp.com/login");
            page.locator("#username").fill("test");
            page.locator("#password").fill("SuperSecretPassword!");
            page.locator("button[type='submit']").click();
            assertThat(page.locator(".flash.error")).isVisible();
            assertThat(page.locator(".flash.error")).containsText("Your username is invalid!");
        }
    }

    @Test
    public void testLoginIncorrectValues() {

        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            page.navigate("https://the-internet.herokuapp.com/login");
            page.locator("#username").fill("tomsmith");
            page.locator("#password").fill("test");
            page.locator("button[type='submit']").click();
            assertThat(page.locator(".flash.error")).isVisible();
            assertThat(page.locator(".flash.error")).containsText("Your password is invalid!");
        }
    }
}
