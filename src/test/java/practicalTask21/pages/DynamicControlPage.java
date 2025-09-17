package practicalTask21.pages;


import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class DynamicControlPage extends BasePage {

    Locator checkbox = page.locator("[type='checkbox']");
    Locator removeButton = page.locator("button >> text=Remove");

    public DynamicControlPage(Page page) {
        super(page);
    }

    public void clickRemoveButton() {
        removeButton.click();
        removeButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
    }

    public boolean isCheckboxVisible() {
        return checkbox.isVisible();
    }
}
