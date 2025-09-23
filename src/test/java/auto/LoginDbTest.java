package auto;

import com.example.config.DbConfig;
import com.microsoft.playwright.*;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginDbTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private Connection connection;
    private static DbConfig dbConfig;
    private static String createUser = "INSERT INTO users (username, password) VALUES (?, ?)";

    @BeforeAll
    static void loadConfig() {
        dbConfig = ConfigFactory.create(DbConfig.class, System.getProperties());
    }

    @BeforeEach
    void setup() throws SQLException {

        connection = DriverManager.getConnection(
                dbConfig.dbUrl(),
                dbConfig.dbUser(),
                dbConfig.dbPassword()
        );

        try (PreparedStatement stmt = connection.prepareStatement(createUser)) {
            stmt.setString(1, "tomsmith");
            stmt.setString(2, "SuperSecretPassword!");
            stmt.executeUpdate();
        }

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = browser.newPage();
    }

    @Test
    void testLoginWithDbUser() throws SQLException {
        String username = null;
        String password = null;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT username, password FROM users WHERE username = 'tomsmith'")) {

            if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }
        }

        assertNotNull(username, "Username not found in DB");
        assertNotNull(password, "Password not found in DB");

        page.navigate("https://the-internet.herokuapp.com/login");
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("button[type='submit']").click();

        assertTrue(page.locator(".flash.success").isVisible());
        assertTrue(page.url().endsWith("/secure"));
    }

    @AfterEach
    void teardown() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM users WHERE username = 'tomsmith'"
            );
        }

        if (connection != null) connection.close();
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
