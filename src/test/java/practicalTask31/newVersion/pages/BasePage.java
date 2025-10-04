package practicalTask31.newVersion.pages;

import com.microsoft.playwright.Page;

public class BasePage {

    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }
}
