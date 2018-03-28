package com.only5.automation_framework.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Configurations {

    public static final String URL = Environments.valueOf(getProp().getProperty("only5.web.url")).getUrl();//"https://beta.only5.in/sign_in";
    public static final String BROWSER = getProp().getProperty("only5.browser");//"ANDROID";
    public static final boolean REMOTE = Boolean.parseBoolean(getProp().getProperty("only5.remote"));//false;
    public static String TEST_TYPE = getProp().getProperty("only5.test_type");//"MOBILE";
    public static long TIME_OUT_SECONDS = Long.parseLong(getProp().getProperty("only5.timeout.seconds"));
    public static String CHROME_DRIVER_EXE = getProp().getProperty("only5.chrome.driver");
    public static String FIREFOX_DRIVER_EXE = getProp().getProperty("only5.gecko.driver");
    public static String IE_DRIVER_EXE = getProp().getProperty("only5.ie.driver");

    private static Properties prop;
    private static HashMap<String, String> urlMap;
    public static String SELENIUM_GRID_URL = getProp().getProperty("only5.hub.url");

    private static Properties getProp() {

        if (prop == null) {
            prop = new Properties();
            InputStream input = null;

            try {
                input = new FileInputStream(new File(Res.getResource("system.properties").toURI()));
                prop.load(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return prop;
    }
}
