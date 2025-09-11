package auto;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {

    Playwright playwright;
    APIRequestContext request;
    static String testFileName = "test_image.png";
    static File testFile;

    @BeforeAll
    static void setupClass() throws Exception {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, 0xFF0000);
        testFile = new File(testFileName);
        ImageIO.write(image, "png", testFile);
    }

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        request = playwright.request().newContext();
    }

    @Test
    void testFileUploadAndDownload() throws IOException {
        APIResponse uploadResponse = request.post(
                "https://httpbin.org/post",
                RequestOptions.create().setMultipart(
                        FormData.create().set("file", testFile.toPath())
                )
        );

        String responseBody = uploadResponse.text();
        assertTrue(responseBody.contains("data:image/png;base64"));

        String base64Data = responseBody.split("\"file\": \"")[1].split("\"")[0];
        byte[] receivedBytes = Base64.getDecoder().decode(base64Data.split(",")[1]);
        byte[] testFileContent = Files.readAllBytes(Path.of(testFileName));
        assertArrayEquals(testFileContent, receivedBytes);

        APIResponse downloadResponse = request.get("https://httpbin.org/image/png");
        String contentType = downloadResponse.headers().get("content-type");
        assertEquals("image/png", contentType);

        byte[] content = downloadResponse.body();
        assertEquals(0x89, content[0] & 0xFF);
        assertEquals(0x50, content[1] & 0xFF);
        assertEquals(0x4E, content[2] & 0xFF);
        assertEquals(0x47, content[3] & 0xFF);
        assertEquals(0x0D, content[4] & 0xFF);
        assertEquals(0x0A, content[5] & 0xFF);
        assertEquals(0x1A, content[6] & 0xFF);
        assertEquals(0x0A, content[7] & 0xFF);
    }

    @AfterEach
    void tearDown() {
        request.dispose();
        playwright.close();
    }

    @AfterAll
    static void teardownClass() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}
