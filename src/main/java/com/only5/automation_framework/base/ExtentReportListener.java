package com.only5.automation_framework.base;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class ExtentReportListener implements IReporter{

    private ExtentReports extent;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        extent = new ExtentReports(outputDirectory + File.separator + "html" + File.separator + "index.html", true);
        try {
            extent.loadConfig(new File(Res.getResource("extent-config.xml").toURI()));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        extent.addSystemInfo("Environment", "SIT");

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();

                buildTestNodes(context.getPassedTests(), LogStatus.PASS);
                buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
                buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
            }
        }

        extent.flush();
        //extent.close();
    }

    private void buildTestNodes(IResultMap tests, LogStatus status) {
        ExtentTest test;

        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
                test = extent.startTest(result.getMethod().getMethodName());
                test.getTest().setStartedTime(getTime(result.getStartMillis()));
                test.getTest().setEndedTime(getTime(result.getEndMillis()));
                test.assignCategory(result.getTestContext().getName());
                test.log(LogStatus.INFO, "Class Name:    " + result.getTestClass().getName());
                for (Object temp : result.getParameters()) {
                    if (temp != null) {
                        test.log(LogStatus.INFO, "Data Used:  " + temp);
                        test.getTest().setName(test.getTest().getName() + "  (" + temp + ")");
                    }
                }
                File file = new File("");
                if (result.getStatus() == ITestResult.FAILURE) {
                    test.log(LogStatus.FAIL, "Click on the snapshot below to zoom it: " + test.addScreenCapture("../screenshots/" + result.getName() + "_" + result.getStartMillis() + ".png"));
                } else if (result.getStatus() == ITestResult.SUCCESS) {
                    test.log(LogStatus.PASS, "Click on the snapshot below to zoom it: " + test.addScreenCapture("../screenshots/" + result.getName() + "_" + result.getStartMillis() + ".png"));
                }
                /*for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);*/

                String message = "Test " + status.toString().toLowerCase() + "ed";

                if (result.getThrowable() != null) {
                    message = result.getThrowable().getMessage();
                }

                test.log(status, message);
                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable());
                }

                extent.endTest(test);
            }
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
