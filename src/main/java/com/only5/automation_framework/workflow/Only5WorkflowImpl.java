package com.only5.automation_framework.workflow;

import com.only5.automation_framework.base.DriverFactory;
import com.only5.automation_framework.pages.web.HomePage;

public class Only5WorkflowImpl implements Only5Workflows {

    @Override
    public HomePage navigateToTheUrl(String url) {
        DriverFactory.getDriver().get(url);
        return new HomePage();
    }

}
