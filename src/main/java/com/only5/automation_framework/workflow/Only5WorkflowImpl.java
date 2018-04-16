package com.only5.automation_framework.workflow;

import com.only5.automation_framework.base.DriverFactory;
import com.only5.automation_framework.pages.web.BasePage;
import com.only5.automation_framework.pages.web.CatalogSearchPage;
import com.only5.automation_framework.pages.web.CheckoutPage;

public class Only5WorkflowImpl implements Only5Workflows {

    @Override
    public BasePage navigateToTheUrl(String url) {
        DriverFactory.getDriver().get(url);
        return new BasePage();
    }

    @Override
    public CatalogSearchPage searchByKeyword(BasePage basePage, String keyword) {
        basePage = basePage.clickSearchButton();
        if (basePage.isSearchTextBoxPresent()) {
            return basePage.enterSearchTextAndEnter(keyword, CatalogSearchPage.class);
        }
        throw new AssertionError("Search Textbox is not displayed. See Screenshot");
    }

    @Override
    public CheckoutPage proceedToCheckout(CatalogSearchPage searchPage) {
        BasePage basePage = searchPage.clickCart();
        if (basePage.isCartSectionDisplayed()) {
            return basePage.clickCheckout();
        }
        throw new AssertionError("The Cart section is not displayed.");
    }

}
