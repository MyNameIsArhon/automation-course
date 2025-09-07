package auto;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.ByteArrayInputStream;

@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
@ExtendWith(CheckboxTest.ScreenshotOnFailure.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора/снятия чекбоксов")
    @Severity(SeverityLevel.CRITICAL)
    void testCheckboxes() {
            navigateToCheckboxesPage();
            verifyInitialState();
            toggleCheckboxes();
            verifyToggledState();
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
    }

    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        Assertions.assertAll(
                () -> Assertions.assertFalse(page.locator("#checkboxes input").first().isChecked()),
                () -> Assertions.assertTrue(page.locator("#checkboxes input").last().isChecked())
        );
    }

    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        page.locator("#checkboxes input").first().click();
        page.locator("#checkboxes input").last().click();
    }

    @Step("Проверка измененного состояния чекбоксов")
    private void verifyToggledState() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(page.locator("#checkboxes input").first().isChecked()),
                () -> Assertions.assertFalse(page.locator("#checkboxes input").last().isChecked())
        );
    }

    static class ScreenshotOnFailure implements TestWatcher {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            CheckboxTest instance = (CheckboxTest) context.getRequiredTestInstance();
            try {
                byte[] screenshot = instance.page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment(
                        "Скриншот при падении: " + context.getDisplayName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
            } catch (Exception ignored) {}
            closeResources(context);
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            closeResources(context);
        }

        private void closeResources(ExtensionContext context) {
            CheckboxTest instance = (CheckboxTest) context.getRequiredTestInstance();
            if (instance.context != null) instance.context.close();
            if (instance.browser != null) instance.browser.close();
            if (instance.playwright != null) instance.playwright.close();
            System.out.println("все закрыли");
        }
    }
}

