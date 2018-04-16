package com.only5.automation_framework.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverFactory {

    private static Map<String, WebDriver> drivers = new HashMap<String, WebDriver>();

    public static void createWebDriverInstance() throws MalformedURLException {
        String browserName = Configurations.BROWSER;
        String testType = Configurations.TEST_TYPE;
        createWebDriverInstance(browserName, testType);
    }

    public static void createWebDriverInstanceForBrowserStack(WebDriver driver) {
        String thread = Thread.currentThread().getName();
        drivers.put(thread, driver);
    }

    public static void createWebDriverInstance(String browserName, String testType) throws MalformedURLException {
        String thread = Thread.currentThread().getName();
        WebDriver driver = null;
        DesiredCapabilities capabilities = null;
        ChromeOptions options = null;

        if (testType.equalsIgnoreCase("WEB")) {
            switch (browserName.toUpperCase()) {
                case "CHROME":
                    System.out.println(Configurations.CHROME_DRIVER_EXE);
                    System.setProperty("webdriver.chrome.driver", Configurations.CHROME_DRIVER_EXE);
                    capabilities = DesiredCapabilities.chrome();
                    options = new ChromeOptions();
                    options.addArguments("test-type", "start-maximized", "no-default-browser-check", "--disable-extensions", "--start-fullscreen");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    capabilities.setPlatform(Platform.ANY);
                    options.setCapability(ChromeOptions.CAPABILITY, capabilities);
                    if (Configurations.REMOTE) {

                        driver = new RemoteWebDriver(new URL(Configurations.SELENIUM_GRID_URL), capabilities);
                    } else {
                        driver = new ChromeDriver(options);
                    }
                    drivers.put(thread, driver);
                    break;
                case "FIREFOX":
                    System.setProperty("webdriver.gecko.driver", Configurations.FIREFOX_DRIVER_EXE);
                    capabilities = DesiredCapabilities.firefox();
                    capabilities.setCapability("marionette", true);
                    FirefoxOptions firefoxOptions = new FirefoxOptions();

                    if (Configurations.REMOTE) {
                        driver = new RemoteWebDriver(new URL(Configurations.SELENIUM_GRID_URL), capabilities);
                    } else {
                        driver = new FirefoxDriver(firefoxOptions);
                    }
                    drivers.put(thread, driver);
                    break;
                case "IE":
                    System.setProperty("webdriver.ie.driver", Configurations.IE_DRIVER_EXE);
                    driver = new InternetExplorerDriver();

                    drivers.put(thread, driver);
                    break;
                default:
                    System.setProperty("webdriver.chrome.driver", Configurations.CHROME_DRIVER_EXE);
                    capabilities = DesiredCapabilities.chrome();
                    options = new ChromeOptions();
                    options.addArguments("test-type", "start-maximized", "no-default-browser-check", "--disable-extensions");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
                    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    capabilities.setPlatform(Platform.ANY);
                    options.setCapability(ChromeOptions.CAPABILITY, capabilities);
                    if (Configurations.REMOTE) {
                        driver = new RemoteWebDriver(new URL(Configurations.SELENIUM_GRID_URL + "&threadCount=10&browser=chrome"), capabilities);
                    } else {
                        driver = new ChromeDriver(options);
                    }
                    drivers.put(thread, driver);
            }
        }
    }

    public static WebDriver getDriver() {
        return drivers.get(Thread.currentThread().getName());
    }
}
