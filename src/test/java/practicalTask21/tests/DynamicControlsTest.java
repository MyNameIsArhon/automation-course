package practicalTask21.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import practicalTask21.TestContext;
import practicalTask21.pages.DynamicControlPage;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DynamicControlsTest {

    private TestContext context;
    private DynamicControlPage dynamicControlPage;

    @BeforeEach
    public void setUp() {
        context = new TestContext();
        dynamicControlPage = new DynamicControlPage(context.getPage());
        context.getPage().navigate("https://the-internet.herokuapp.com/dynamic_controls");
    }

    @Test
    public void testCheckboxRemoval() {
        dynamicControlPage.clickRemoveButton();
        assertFalse(dynamicControlPage.isCheckboxVisible());
    }

    @AfterEach
    public void tearDown() {
        context.close();
    }
}
