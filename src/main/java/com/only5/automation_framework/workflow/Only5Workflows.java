package com.only5.automation_framework.workflow;

import com.only5.automation_framework.pages.web.BasePage;
import com.only5.automation_framework.pages.web.CatalogSearchPage;
import com.only5.automation_framework.pages.web.CheckoutPage;

public interface Only5Workflows {

    public BasePage navigateToTheUrl(String url);

    public CatalogSearchPage searchByKeyword(BasePage basePage, String keyword);

    public CheckoutPage proceedToCheckout(CatalogSearchPage searchPage);

}
