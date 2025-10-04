package practicalTask31.newVersion.tests;

import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static practicalTask31.newVersion.constants.ErrorMessages.INVALID_PASSWORD_ERROR;
import static practicalTask31.newVersion.constants.ErrorMessages.INVALID_USERNAME_ERROR;

public class LoginNewVersionTest extends BaseTest {

    @Test
    public void testLogin() {

        Locator result = openLoginPage()
                .login("tomsmith", "SuperSecretPassword!")
                .getSuccessText();
        assertThat(result).isVisible();
    }

    @ParameterizedTest
    @MethodSource("loginProvider")
    public void testLoginIncorrectValues(String username, String password, String errorText) {

        Locator result = openLoginPage()
                .login(username, password)
                .getErrorText();
        assertThat(result).isVisible();
        assertThat(result).containsText(errorText);
    }

    private static Stream<Arguments> loginProvider() {
        return Stream.of(
                Arguments.of("test", "SuperSecretPassword!", INVALID_USERNAME_ERROR),
                Arguments.of("tomsmith", "test", INVALID_PASSWORD_ERROR)
        );
    }
}
