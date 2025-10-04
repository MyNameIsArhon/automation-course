package practicalTask31.newVersion.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {

    private final Locator usernameField = page.locator("#username");
    private final Locator passwordField = page.locator("#password");
    private final Locator submitButton = page.locator("button[type='submit']");
    private final Locator successText = page.locator(".flash.success");
    private final Locator errorText = page.locator(".flash.error");

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage login(String username, String password) {
        usernameField.fill(username);
        passwordField.fill(password);
        submitButton.click();
        return this;
    }

    public Locator getSuccessText() {
        return successText;
    }

    public Locator getErrorText() {
        return errorText;
    }
}
