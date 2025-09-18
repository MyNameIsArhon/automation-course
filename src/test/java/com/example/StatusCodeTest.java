package com.example;

import com.example.config.EnvironmentConfig;
import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;


@Execution(ExecutionMode.CONCURRENT)
public class StatusCodeTest {

    private static final EnvironmentConfig config = ConfigFactory.create(EnvironmentConfig.class, System.getenv());

    @ParameterizedTest
    @CsvSource({
            "200, status_codes/200",
            "301, status_codes/301",
            "404, status_codes/404",
            "500, status_codes/500"
    })
    public void testStatusCode(int statusCode, String path) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch();
             BrowserContext context = browser.newContext();
             Page page = context.newPage()) {

            Response response = page.navigate(config.baseUrl() + path);
            assertThat(response.status())
                    .as("Ожидался статус код: %d", statusCode)
                    .isEqualTo(statusCode);
        }
    }
}
