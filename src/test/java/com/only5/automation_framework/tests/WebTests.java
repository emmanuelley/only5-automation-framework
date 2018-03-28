package com.only5.automation_framework.tests;

import com.only5.automation_framework.base.Configurations;
import com.only5.automation_framework.pages.web.HomePage;
import com.only5.automation_framework.workflow.Only5WorkflowImpl;
import com.only5.automation_framework.workflow.Only5Workflows;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WebTests extends BrowserStackTestNGTest {

    @Test
    public void testCreationOfAWorkflow() {
        Only5Workflows nw = new Only5WorkflowImpl();
        HomePage homePage = nw.navigateToTheUrl(Configurations.URL);

        assertTrue(homePage.isLogoPresent(), "Home Page is not loaded!!!");
    }

    @DataProvider(name = "testData", parallel = false)
    public static Object[][] getTestData() {
        return new Object[1][1];
    }
}
