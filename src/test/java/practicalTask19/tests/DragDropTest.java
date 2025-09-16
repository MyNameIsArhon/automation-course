package practicalTask19.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import practicalTask19.components.DragDropArea;
import practicalTask19.pages.DragDropPage;

public class DragDropTest {

    static Playwright playwright;
    static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    public static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
    }

    @BeforeEach
    public void setup() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testDragAndDrop() {
        DragDropArea dragDropArea = new DragDropPage(page)
                .open()
                .dragDropArea();
        String initialElementA = dragDropArea.getTextA();
        String initialElementB = dragDropArea.getTextB();

        dragDropArea.dragAToB();

        String newElementA = dragDropArea.getTextA();
        String newElementB = dragDropArea.getTextB();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(initialElementA)
                .as("Ожидалось значение: 'A', но получено: '%s'", initialElementA)
                .isEqualTo("A");
        softly.assertThat(initialElementB)
                .as("Ожидалось значение: 'B', но получено: '%s'", initialElementB)
                .isEqualTo("B");
        softly.assertThat(newElementA)
                .as("Ожидалось значение: 'B', но получено: '%s'", newElementA)
                .isEqualTo("B");
        softly.assertThat(newElementB)
                .as("Ожидалось значение: 'A', но получено: '%s'", newElementB)
                .isEqualTo("A");
        softly.assertAll();
    }

    @AfterEach
    public void tearDown() {
        page.close();
        context.close();
    }

    @AfterAll
    public static void tearDownClass() {
        browser.close();
        playwright.close();
    }
}
