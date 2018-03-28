# Test Automation Framework
This document talks about how to use the Test Automation Framework for testing the Web, Mobile and API application.

## Introduction
The Test Automation Framework allows you to create the tests for:
* Web Applications which can be run on following browsers:
  * Chrome
  * Firefox
  * Edge Driver
  * Internet Explorer 11
* Mobile Apps including:
  * Android Web
  * iOS Web
* RESTFul APIs

## Framework Block Diagram
![Framework Block Diagram](/images/Framework_Design.PNG)

## Pre-requisite Tools and Technologies
* Java JDK 1.8 Above
* Java IDE like Eclipse, Netbeans or IntelliJ IDEA
* Maven Environment (Should be present in Netbeans and IntelliJ IDEA out of the box. For Eclipse, plugin might be required to be installed)
* GIT SCM for pull and pushing the code

## Technology Stack
* Java JDK 1.8 above
* Maven 3.5 above
* TestNG 6.11
* Rest-Assured 3.0.3
* Appium 4.1.2
* Mongo Driver 2.14.3
* Selenium WebDriver 3.4

## Setup Info
* Download this zip file and Unzip it
* Download the Web Drivers for Chrome, Firefox and IE in the following path:
 * Chrome:	https://sites.google.com/a/chromium.org/chromedriver/downloads
 * Edge:	https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
 * Firefox:	https://github.com/mozilla/geckodriver/releases
* Place the respective distributions in the folder of your choice. After this, follow the following steps:
	* Navigate to your .m2 folder.
		* For Windows, this should be under C:\Users\<Your_Account_Name>\\.m2
		* For Linux, this should be under ~/.m2 folder.
	* Create a file settings.xml
	* Next the following script inside your code:
	```
	<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
		<profiles>
			<profile>
				<id>local-properties</id>
				<activation>
					<activeByDefault>true</activeByDefault>
				</activation>
				<properties>
					<only5.chrome.driver>PATH_TO_THE_EXECUTABLE</only5.chrome.driver>
					<only5.gecko.driver>PATH_TO_THE_EXECUTABLE</only5.gecko.driver>
					<nishaanx.ie.driver>PATH_TO_THE_EXECUTABLE</nishaanx.ie.driver>
				</properties>
			</profile>
		</profiles>
		<activeProfiles>
			<activeProfile>local-properties</activeProfile>
		</activeProfiles>
	</settings>
	```
* Run the command:
```
mvn clean install
```

## Creating the Page Objects
The test automation framework is designed with an objective to reduce timeout failures and at the same time provide you with functions that can be easy to use. For this we have developed the BasePage class which provides you all the functions that you might want to easily complete your test automation scripts. All the Page Objects will be based on this BasePage Class.

To create a new Page, here are the steps:
* Create a class with the following naming convention <PageName>Page.java example: LoginPage.java
* Next, extend the class to BasePage
* After that create the WebElements on the Page using the By of that page.
* After creating the WebElements, the next step is to create the functions for each of the By element.
* Ensure that,
 * if the action performed on the WebElement results the same page, then return the same page for that method
 * if the action performed on the WebElement results in the navigation to another Page, then return that page for that method
 * if the action performed on the WebElements is decided based on the inputs like invalid login entries or valid login entries which can have different resultant page, then the based on the Class parameter, the method should return that particular page.
 * if the action peerformed on the WebElement results in fetching of a value or status, then it should return any of the primitive datatypes like String, boolean, integers or any number formats.

   Here is an example of a page with the above mentioned guidelines:
   ```java
   public class LoginPage extends BasePage {

    final By loginUserName = By.id("loginUsername");

    final By loginPassword = By.id("loginPassword");

    final By signInButton = By.xpath("//input[@value='Sign in']");

    final By errorMsg = By.className("alert-danger");

    public LoginPage enterUsername(String username) {
        waitForElement(loginUserName).sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        waitForElement(loginPassword).sendKeys(password);
        return this;
    }

    public <T extends BasePage> T clickSubmit(Class<T> clazz) {
        try {
            waitForElement(signInButton).click();
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Unable to create an instance of the page");
        }
    }
   ```
   Here is another example:
   ```java
    public class SignInPage extends BasePage {

     private final By corporateLoginButton = By.xpath("//button[@data-provider='corporate-login']");
     private final By errorMessage = By.className("alert-danger");

     public LoginPage clickCorporateLoginButton() {
        waitForElement(corporateLoginButton).click();
        return new LoginPage();
     }

     public String getErrorMessage(){
        return waitForElement(errorMessage).getText();
     }
   }
   ```
