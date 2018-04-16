package com.only5.automation_framework.pages.web;

import com.only5.automation_framework.base.Configurations;
import com.only5.automation_framework.base.DriverFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {

    protected WebDriver driver;
    By logoImg = By.className("logo");
    By searchButton = By.className("varmo-icon-search");
    By searchTextBox = By.xpath("//div[contains(@class,'block-search') and contains(@class,'show')]//input[@id='search']");

    By cartSection = By.className("showcart");
    By cartSectionDropDown = By.xpath("//div[contains(@class,'minicart-wrapper') and contains(@class,'active')]//div[@role='dialog' and @style='display: block;']//div[@data-role='dropdownDialog']");
    By checkoutButton = By.id("btn-minicart-close");

    public boolean isLogoPresent() {
        log().info("Checking if the logo is present");
        return isElementPresent(logoImg);
    }

    public CheckoutPage clickCheckout() {
        log().info("Clicking on checkout");
        waitForElement(cartSectionDropDown).findElement(checkoutButton).click();
        return new CheckoutPage();
    }

    public int getCountOfItems() {
        log().info("Getting the count of items added to the cart");
        if (waitForElementGone(By.xpath("//div[@data-block='minicart']//*[@class='loading-mask']"))) {
            return Integer.parseInt(waitForElement(cartSection).findElement(By.className("counter-number")).getText().trim());
        } else {
            throw new AssertionError("The loader image is still displayed!!! Something is wrong. Please check!!!");
        }
    }

    public BasePage clickCart() {
        log().info("Clicking on cart");
        waitForElement(cartSection).click();
        return this;
    }

    public BasePage clickSearchButton() {
        log().info("Clicking Search Button...");
        waitForElement(searchButton).click();
        return this;
    }

    public boolean isSearchTextBoxPresent() {
        log().info("Waiting for the Search Textbox to appear");
        return isElementPresent(searchTextBox);
    }

    public <T extends BasePage> T enterSearchTextAndEnter(String searchText, Class<T> clazz) {
        log().info("Entering search text: " + searchText + " into the search textbox");
        Actions actions = new Actions(driver);
        actions.sendKeys(waitForElement(searchTextBox), searchText).sendKeys(Keys.ENTER).build().perform();
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new AssertionError("Unable to create the instance of the class" + clazz.getName());
        }
    }

    public boolean isCartSectionDisplayed() {
        return isElementPresent(cartSectionDropDown);
    }

    public boolean waitForElementWithCondition(Function function) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.pollingEvery(5, TimeUnit.SECONDS)
                .until(function);
        return true;
    }

    public BasePage() {
        this.driver = DriverFactory.getDriver();
    }

    public Logger log() {
        Logger log = LoggerFactory.getLogger(this.getClass());
        return log;
    }

    public WebElement waitForElementVisible(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.elementToBeClickable(by));
        return waitForElement(by);
    }

    public WebElement waitForElement(By by) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(12, TimeUnit.SECONDS)
                .pollingEvery(4, TimeUnit.SECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver t) {
                        try {
                            return hasElement(by);
                        } catch (UnhandledAlertException ex) {
                            t.switchTo().alert().accept();
                            t.switchTo().defaultContent();
                            return false;
                        }
                    }
                });
        return driver.findElement(by);
    }

    public WebElement waitForElement(WebElement element) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver t) {
                        try {
                            return element.isDisplayed();
                        } catch (UnhandledAlertException ex) {
                            t.switchTo().alert().accept();
                            t.switchTo().defaultContent();
                            return false;
                        }
                    }
                });
        return element;
    }

    public WebElement waitForElement(By by, int waitInSeconds) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(waitInSeconds, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver t) {
                        return ((SearchContext) t).findElements(by).size() > 0;
                    }

                });
        return driver.findElement(by);
    }

    public WebElement waitForElementToChangeText(By by) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        String text = driver.findElement(by).getText();
        wait.withTimeout(20, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver t) {
                        return !t.findElement(by).getText().equals(text);
                    }

                });
        return driver.findElement(by);
    }

    public List<WebElement> waitForElementsBy(By by) {
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait
                .ignoring(NoSuchElementException.class
                );
        wait
                .ignoring(StaleElementReferenceException.class
                );
        wait.until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver t) {
                return t.findElements(by).size() > 0;
            }
        });
        return driver.findElements(by);
    }

    public List<WebElement> waitForElementsBy(WebElement element, By by) {
        WebDriverWait wait = new WebDriverWait(driver, 60);
        wait
                .ignoring(NoSuchElementException.class
                );
        wait
                .ignoring(StaleElementReferenceException.class
                );
        wait.until((WebDriver t) -> element.findElements(by).size() > 0);
        return driver.findElements(by);
    }

    public void moveToElement(By by) {
        Actions actions = new Actions(driver);
        actions.moveToElement(waitForElement(by)).build().perform();
    }

    public void selectItemByValue(WebElement element, String itemToSelect) {
        getSelect(element).selectByValue(itemToSelect);
    }

    public void selectItemByText(WebElement element, String text) {
        getSelect(element).selectByVisibleText(text);
    }

    public void selectItemByIndex(WebElement element, int index) {
        getSelect(element).selectByIndex(index);
    }

    public void clickButton(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasElement(By by) {
        return countElements(by) != 0;
    }

    public boolean hasNoElementAsExpected(By by) {
        WebElement element;
        element = new WebDriverWait(driver, 5).until(ExpectedConditions
                .presenceOfElementLocated(by));
        return element == null || !element.isDisplayed();
    }

    public String getRandomString() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String getRandomNumeric() {
        return RandomStringUtils.randomNumeric(3);
    }

    public JavascriptExecutor getJavaScriptExecutor() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js;
    }

    public boolean waitForElementGone(By by) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        try {
            wait.withTimeout(30, TimeUnit.SECONDS)
                    .pollingEvery(1, TimeUnit.SECONDS)
                    .until(new Function<WebDriver, Boolean>() {
                        @Override
                        public Boolean apply(WebDriver t) {
                            return !(((SearchContext) t).findElements(by).size() > 0);
                        }
                    });
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    public void clickElementWithJavascript(WebElement element) {
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click()", element);
        }
    }

    public void handledSleep(int sleepInSeconds) {
        Calendar cal = Calendar.getInstance();
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.SECOND, sleepInSeconds);
        while (cal1.after(cal)) {
            cal = Calendar.getInstance();
        }
    }

    public void waitTillMultipleTabOpens() {
        Set<String> allWindows = driver.getWindowHandles();
        while (allWindows.size() == 1) {
            allWindows = driver.getWindowHandles();
        }
    }

    public boolean verifyElementSelected(WebElement element, boolean selected) {
        return (new WebDriverWait(driver, 5)).until(ExpectedConditions
                .elementSelectionStateToBe(element, selected));
    }

    public void switchToLastTab() {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(browserTabs.size() - 1));
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1440, 900));
    }

    public void switchToLastTabAndCloseFirstTab() {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        if (browserTabs.size() > 1) {
            driver.close();
        }
        driver.switchTo().window(browserTabs.get(browserTabs.size() - 1));
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new Dimension(1440, 900));
    }

    public void switchToFirstTab() {
        Set<String> handles = driver.getWindowHandles();
        for (int i = handles.size(); i > 1; i--) {
            driver.switchTo().window(
                    handles.toArray(new String[handles.size()])[i - 1]);
            driver.close();
        }
        driver.switchTo().window(
                handles.toArray(new String[handles.size()])[0]);
    }

    public void closeTab() {
        driver.close();
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(browserTabs.size() - 1));
    }

    public void scrollToElementAndClick(By by) {
        scrollToElement(by);
        waitForElement(by).click();
    }

    public void scrollToElement(By by) {
        int scrollBy = waitForElement(by).getLocation().y + 25;
        getJavaScriptExecutor().executeScript(
                "window.scrollBy(0," + scrollBy + ");");
    }

    public boolean isFileOpened(File file) {
        boolean res = false;
        FileLock lock = null;
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();
            // Get an exclusive lock on the whole file
            lock = channel.lock();

            //The file is not already opened
            lock = channel.tryLock();
        } catch (OverlappingFileLockException | IOException e) {
            // File is open by someone else
            res = true;
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
                if (lock != null) {
                    lock.release();
                    lock.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return res;
    }

    public String getTextFromElement(By by) {
        return waitForElement(by).getText();
    }

    public void selectRadioButtonByValue(By radioGroup, String ValueToSelect) {
        // Find the radio group element
        List<WebElement> radioLabels = driver.findElements(
                radioGroup);
        for (int i = 0; i < radioLabels.size(); i++) {
            if (radioLabels.get(i).getText().trim()
                    .equalsIgnoreCase(ValueToSelect.trim())) {
                radioLabels.get(i).click();
                break;
            }
        }
    }

    public Select getSelect(WebElement element) {
        Select select = new Select(element);
        return select;
    }

    public int countElements(By by) {
        int result = 0;
        long currentWaitMillis = Configurations.TIME_OUT_SECONDS;
        try {
            if (currentWaitMillis > 0) {
                driver.manage().timeouts()
                        .implicitlyWait(5, TimeUnit.MILLISECONDS);
            }
            result = driver.findElements(by).size();
        } finally {
            driver.manage().timeouts()
                    .implicitlyWait(currentWaitMillis, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    public void sendValuesToWebElement(WebElement element, String value) {
        element.sendKeys(value);
    }

    public boolean isDisabled(WebElement element) {
        return element.getAttribute("disabled").equalsIgnoreCase("disabled");
    }

    public boolean isEnabled(WebElement element) {
        return element.getAttribute("enabled").equalsIgnoreCase("enabled");
    }

    public boolean isElementPresent(By by) {
        return isElementPresent(by, Configurations.TIME_OUT_SECONDS);
    }

    public boolean isElementPresent(By by, long timeOut) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        try {
            wait.withTimeout(timeOut, TimeUnit.SECONDS)
                    .pollingEvery(2, TimeUnit.SECONDS)
                    .until((WebDriver t) -> {
                        try {
                            boolean isPresent = false;
                            if (countElements(by) > 0) {
                                WebDriverWait wait1 = new WebDriverWait(driver, timeOut);
                                WebElement element = wait1.until(ExpectedConditions.visibilityOfElementLocated(by));
                                isPresent = element.isDisplayed();
                            }
                            return isPresent;
                        } catch (UnhandledAlertException ex) {
                            t.switchTo().alert().accept();
                            t.switchTo().defaultContent();
                            return false;
                        }
                    });
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<WebElement> waitForElements(WebElement element, By by) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver);
        wait.withTimeout(10, TimeUnit.SECONDS)
                .pollingEvery(4, TimeUnit.SECONDS)
                .until(new Function<WebDriver, Boolean>() {
                    @Override
                    public Boolean apply(WebDriver t) {
                        try {
                            return element.findElements(by).size() > 0;
                        } catch (UnhandledAlertException ex) {
                            t.switchTo().alert().accept();
                            t.switchTo().defaultContent();
                            return false;
                        }
                    }
                });
        return element.findElements(by);
    }

    public void checkboxSelection(WebElement element, boolean value) {
        int val = Boolean.compare(Boolean.parseBoolean(element.getAttribute("value")), value);
        if (val == 0) {
        } else {
            element.click();
        }
    }

}
