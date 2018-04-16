package com.only5.automation_framework.tests;

import com.only5.automation_framework.base.Configurations;
import com.only5.automation_framework.data.Product;
import com.only5.automation_framework.pages.web.BasePage;
import com.only5.automation_framework.pages.web.CatalogSearchPage;
import com.only5.automation_framework.pages.web.CheckoutPage;
import com.only5.automation_framework.workflow.Only5WorkflowImpl;
import com.only5.automation_framework.workflow.Only5Workflows;
import java.util.List;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WebTests extends BrowserStackTestNGTest {

    @Test
    public void testCreationOfAWorkflow() {
        Only5Workflows nw = new Only5WorkflowImpl();
        BasePage basePage = nw.navigateToTheUrl(Configurations.URL);

        assertTrue(basePage.isLogoPresent(), "Home Page is not loaded!!!");
        CatalogSearchPage searchPage = nw.searchByKeyword(basePage, "test");

        assertTrue(searchPage.isProductsGridSectionPresent(), "Search Page is not loaded!!!");
        List<Product> products = searchPage.getAllProductsFromTheList();
        for (Product product : products) {
            product.getAddToCartButton().click();
        }

        assertEquals(searchPage.getCountOfItems(), products.size(), "The number of items added do not match");
        CheckoutPage checkoutPage = nw.proceedToCheckout(searchPage);

    }

    @DataProvider(name = "testData", parallel = false)
    public static Object[][] getTestData() {
        return new Object[1][1];
    }
}
