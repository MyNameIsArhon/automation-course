package auto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws Exception {
        APIResponse response = requestContext.get("/posts/1");
        assertEquals(200, response.status());

        if (response.ok()) {
            PostResponseModel actualResult = objectMapper.readValue(response.text(), PostResponseModel.class);
            String expectedTitle = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";
            String expectedBody = "quia et suscipit\n" +
                    "suscipit recusandae consequuntur expedita et cum\n" +
                    "reprehenderit molestiae ut ut quas totam\n" +
                    "nostrum rerum est autem sunt rem eveniet architecto";
            Assertions.assertAll(
                    () -> assertEquals(1, actualResult.getUserId(), "Ожидайлся другой userId"),
                    () -> assertEquals(1, actualResult.getId(), "Ожидайлся другой id"),
                    () -> assertEquals(expectedTitle, actualResult.getTitle(), "Ожидайлся другой title"),
                    () -> assertEquals(expectedBody, actualResult.getBody(), "Ожидайлся другой body")
            );
        }
        response.dispose();
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }

    static class PostResponseModel {

        private int userId;
        private int id;
        private String title;
        private String body;

        public int getUserId() {
            return userId;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
    }
}
