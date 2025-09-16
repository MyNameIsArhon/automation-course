package practicalTask19.pages;

import com.microsoft.playwright.Page;
import practicalTask19.components.DragDropArea;

public class DragDropPage extends BasePage {

    private DragDropArea dragDropArea;

    public DragDropPage(Page page) {
        super(page);
    }

    public DragDropPage open() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
        return this;
    }

    public DragDropArea dragDropArea() {
        if (dragDropArea == null) {
            dragDropArea = new DragDropArea(page);
        }
        return dragDropArea;
    }
}
