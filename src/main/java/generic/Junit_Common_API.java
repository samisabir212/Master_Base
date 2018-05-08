package generic;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;

import utilMaster.WebEventListener;

public class Junit_Common_API {

	public static WebDriver driver = null;
	public static EventFiringWebDriver e_driver;
	public static WebEventListener eventListener;
	public static Properties prop;
	public static Logger log = Logger.getLogger(Junit_Common_API.class);

	private String saucelabs_username = "";
	private String browserstack_username = "";
	private String saucelabs_accesskey = "";
	private String browserstack_accesskey = "";

	@Before
	public void setUp() throws IOException {

		log.info(
				"****************************** Starting test cases execution  *****************************************");
		log.debug("lauching test");

		String useCloudEnv = getConfig_PropertiesFile().getProperty("useCloudEnv");
		String cloudEnvName = getConfig_PropertiesFile().getProperty("cloudEnvName");
		String useGrid = getConfig_PropertiesFile().getProperty("true");
		if (useCloudEnv.equalsIgnoreCase("true")) {
			if (cloudEnvName.equalsIgnoreCase("browserstack")){
				cloudEnv();
			}else if (cloudEnvName.equalsIgnoreCase("saucelabs")) {
				cloudEnv();
			}
			
		}else if(useGrid.equalsIgnoreCase("true")){
			gridEnv();
		}else {
			localEnv();
		}

		// localEnv();

		e_driver = new EventFiringWebDriver(driver);
		eventListener = new WebEventListener();
		e_driver.register(eventListener);
		driver = e_driver;

		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(35, TimeUnit.SECONDS);
		driver.get(prop.getProperty("url"));

	}

	public WebDriver cloudEnv() throws MalformedURLException {
		
		String browserName = getConfig_PropertiesFile().getProperty("browserName");
		String browserVersion = getConfig_PropertiesFile().getProperty("browserVersion");
		String os = getConfig_PropertiesFile().getProperty("os");
		String os_version = getConfig_PropertiesFile().getProperty("os_version");
		String cloudEnvName = getConfig_PropertiesFile().getProperty("cloudEnvName");


		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability("browser", browserName);
		cap.setCapability("browser_version", browserVersion);
		cap.setCapability("os", os);
		cap.setCapability("os_version", os_version);
		if (cloudEnvName.equalsIgnoreCase("Saucelabs")) {
			driver = new RemoteWebDriver(
					new URL("http://" + saucelabs_username + ":" + saucelabs_accesskey + "@ondemand.saucelabs.com:80/wd/hub"), cap);
		} else if (cloudEnvName.equalsIgnoreCase("Browserstack")) {
			cap.setCapability("resolution", "1024x768");
			driver = new RemoteWebDriver(
					new URL("http://" + browserstack_username + ":" + browserstack_accesskey + "@hub-cloud.browserstack.com/wd/hub"), cap);
		}
		return driver;
		

	}

	public WebDriver localEnv() throws IOException {


		String browserName = getConfig_PropertiesFile().getProperty("browserName");
		String os = getConfig_PropertiesFile().getProperty("os");

		if (browserName.equalsIgnoreCase("chrome")) {
			if (os.equalsIgnoreCase("Mac")) {
				System.setProperty("webdriver.chrome.driver",
						"/Users/sami/Desktop/RocketLauncher/Mac/Drivers/chromedriver2_26");
			} else if (os.equalsIgnoreCase("Win")) {
				System.setProperty("webdriver.chrome.driver", "../Generic/driver/chromedriver.exe");
			}

			driver = new ChromeDriver();
		} else if (browserName.equalsIgnoreCase("firefox")) {
			if (os.equalsIgnoreCase("Mac")) {
				System.setProperty("webdriver.gecko.driver",
						"/Users/sami/Desktop/RocketLauncher/Mac/Drivers/geckodriver_V_19_1");
			} else if (os.equalsIgnoreCase("Win")) {
				System.setProperty("webdriver.gecko.driver", "../Generic/driver/geckodriver.exe");
			}
			driver = new FirefoxDriver();

		} else if (browserName.equalsIgnoreCase("ie")) {
			System.setProperty("webdriver.ie.driver", "../Generic/driver/IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}

		return driver;

	}

	public WebDriver gridEnv() throws MalformedURLException {
		
		// passing node url to remote driver
		String nodeURL = getConfig_PropertiesFile().getProperty("nodeCloudURL");

		// WebDriver driver = null;

		DesiredCapabilities caps = new DesiredCapabilities();

		// Platforms
		String platform = getConfig_PropertiesFile().getProperty("platform");
		if (platform.equalsIgnoreCase("Windows")) {
			caps.setPlatform(org.openqa.selenium.Platform.WINDOWS);
		}
		if (platform.equalsIgnoreCase("MAC")) {
			caps.setPlatform(org.openqa.selenium.Platform.MAC);
		} 
		if (platform.equalsIgnoreCase("Linux")) {
			caps.setPlatform(org.openqa.selenium.Platform.LINUX);
		}

		// Browsers
		String browserName = getConfig_PropertiesFile().getProperty("browserName");
		if (browserName.equalsIgnoreCase("chrome")) {
			caps = DesiredCapabilities.chrome();
		}
		if (browserName.equalsIgnoreCase("firefox")) {
			caps = DesiredCapabilities.firefox();
		}
		// Version
		String browserVersion = getConfig_PropertiesFile().getProperty("browserVersion");
		caps.setVersion(browserVersion);

		driver = new RemoteWebDriver(new URL(nodeURL), caps);
		
		return driver;

	}


	@After public void tearDown() {
		System.out.println("quit");
		driver.quit();
	}

	/*******************************
	 * ACTION METHODS
	 ****************************************/

	/*
	 * ~~PROPERTIES FILE
	 * 
	 */
	public Properties getConfig_PropertiesFile() {

		File file = new File("/Users/sami/git/EclipseWorkSpace/Master/src/main/java/properties/config.properties");

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties prop = new Properties();

		// load properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;

	}

	/*******************************
	 * MAXIMIZE WINDOWS FOR DIFFERENT BROWSERS
	 ****************************************/

	public static void maximize_IEandFirefox_Browsers() {

		driver.manage().window().maximize();

	}

	public static void maximize_ToolKit() {

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenResolution = new Dimension((int) toolkit.getScreenSize().getWidth(),
				(int) toolkit.getScreenSize().getHeight());

		driver.manage().window().setSize(screenResolution);

	}

	/***********************************************************************/

	/*******************************
	 * CLICKING ACTIONS
	 ****************************************/

	// click Webelement POM pageFactory ---> **make sure WebElement in POM design is
	// static**
	public static void click(WebElement element) {
		element.click();
	}

	// click by locator
	public static void click(By locator) {

		driver.findElement(locator).click();

	}

	/*******************************
	 * JAVA SCRIPT ACTIONS CLASS CLICKING
	 ****************************************/

	// ~POM~ javascript execute click
	public static void clickJS_ExecuteAction(WebElement element) {

		JavaScriptExecutor js = (JavaScriptExecutor) driver;
		((RemoteWebDriver) js).executeScript("arguments[0].click", element);

	}

	public static void clickJavaScriptActionsClick(By locator) {

		WebElement element = driver.findElement(locator);
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();

		/*
		 * 1. The element is not visible to click. 2. The page is getting refreshed
		 * before it is clicking the element. 3. The element is clickable but there is a
		 * spinner/overlay on top of it
		 * 
		 * in some cases this will happen and we have to us the Javascript Actions class
		 */
	}

	/***********************************************************************/

	/********** TYPE SEND KEYS (typing in fields) *********/

	public static void typeBy(By locator, String value) {

		driver.findElement(locator).sendKeys(value);

	}

	public static void typeByCss(String locator, String value) {

		driver.findElement(By.cssSelector(locator)).sendKeys(value);
	}

	// typeing by id locator
	public static void typeByID(String locator, String value) {

		driver.findElement(By.id(locator)).sendKeys(value);
	}

	// type by id and enter key
	public static void typeByIdEnter(String locator, String value) {

		driver.findElement(By.id(locator)).sendKeys(value, Keys.ENTER);
	}

	// type by xpath and ENTER key
	public static void typeByXpathEnter(String locator, String value) {

		driver.findElement(By.xpath(locator)).sendKeys(value, Keys.ENTER);
	}

	// type by css and ENTER key
	public static void typeByCssEnter(String locator, String value) {
		driver.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER);
	}

	// type by xpath
	public static void typeByXpath(String locator, String value) {
		driver.findElement(By.xpath(locator)).sendKeys(value);
	}

	// ?????
	public static void takeEnterKeys(String locator) {
		driver.findElement(By.cssSelector(locator)).sendKeys(Keys.ENTER);
	}

	/***************************
	 * Select from dropdown list
	 ***************************************/

	public static void selectDropListByIndex(By locator, int index) {

		Select DropDownList = new Select(driver.findElement(locator));
		DropDownList.selectByIndex(index);
	}

	public static void selectOptionByVisibleTextElementOutside(WebElement element, String value) {
		Select select = new Select(element);
		select.selectByVisibleText(value);
	}

	public static void selectOptionByVisibleText(By locator, String value) {
		WebElement object = driver.findElement(locator);
		Select select = new Select(object);
		select.selectByVisibleText(value);
	}

	/*****************************
	 * CLEAR INPUT FIELD
	 *************************************/

	public static void clearInputField(By locator) {

		driver.findElement(locator).clear();
	}

	// pass the locator and pass the type of locator and it will automatically
	// generate
	public WebElement getElement(String locator, String type) {

		type = type.toLowerCase();

		if (type.equals("id")) {
			System.out.println("Element found with id: " + locator);// you can change it and make it print ID by
																	// changing locator to type
			return this.driver.findElement(By.id(locator));
		} else if (type.equals("xpath")) {
			System.out.println("Element found with xpath: " + locator);
			return this.driver.findElement(By.xpath(locator));
		} else if (type.equals("css")) {
			System.out.println("Element found with xpath: " + locator);
			return this.driver.findElement(By.cssSelector(type));
		} else if (type.equals("linktext")) {
			System.out.println("Element found with xpath: " + locator);
			return this.driver.findElement(By.linkText(locator));
		} else if (type.equals("partiallinktext")) {
			System.out.println("Element found with xpath: " + locator);
			return this.driver.findElement(By.partialLinkText(type));
		} else {
			System.out.println("Locator type not supported");
			return null;
		}
	}

	// get Links
	public static void getLinks(String locator) {
		driver.findElement(By.linkText(locator)).findElement(By.tagName("a")).getText();
	}

	public static List<String> getTextFromWebElements(String locator) {

		List<WebElement> element = new ArrayList<WebElement>();
		List<String> text = new ArrayList<String>();
		element = driver.findElements(By.cssSelector(locator));
		for (WebElement web : element) {
			text.add(web.getText());
		}

		return text;
	}

	// verifying >>>>>>><<<<<<<<<<<<>>>>>>>>>>>><<<<<<<<<<<<<>>>>>>>

	public static void verifyRadioButtonSelection(String locator) {
		WebElement roundTripRadioBtn = driver.findElement(By.id(locator));

		boolean radioButton = roundTripRadioBtn.isSelected();

		System.out.println(radioButton);

		if (radioButton = true) {
			System.out.println("(Passed) Radio Button is selected");

		} else {
			System.out.println("(failed) Radio button not selected ");
		}

	}

	public static void verifyTextFieldisDisplayed(String locator) {

		WebElement textField = driver.findElement(By.id(locator));
		boolean textFieldObject = textField.isDisplayed();

		if (textFieldObject = true) {
			System.out.println("(Pass) text field is present");

		} else {

			System.out.println("(Fail) Text field is not present");

		}
	}

	// verify a button is present
	public static void verifyButtonIsPresent(String locator, String True, String False) {
		WebElement button = driver.findElement(By.xpath(locator));
		boolean verifyButton = button.isDisplayed();

		if (verifyButton = true) {
			System.out.println(True);

		} else {
			System.out.println(False);

		}
	}

	public static void verifyURL(String ExpectedURL) {

		String url = driver.getCurrentUrl();

		if (url.equals(ExpectedURL)) {
			System.out.println("verify url :: Passed");
		} else {
			System.out.println("verify url :: Failed");
		}

	}

	public static void verifyTitle(String ExpectedTitle) {

		String title = driver.getTitle();

		if (title.equals(ExpectedTitle)) {
			System.out.println("verify title :: Passed");
		} else {
			System.out.println("verify title :: Failed");
		}

	}

	public static void verifyDisplayedElement(boolean element) {

		if (element == true) {
			System.out.println("element is displayed:: pass");
		} else {
			System.out.println("element is not displayed:: fail");
		}

	}

	public static String getCurrentPageUrl() {

		String url = driver.getCurrentUrl();

		System.out.println(url.toString());

		return url;
	}

	// ***********************************************

	/* sleep */
	public static void sleepFor(int sec) throws InterruptedException {
		Thread.sleep(sec * 1000);
	}

	// *********************DROP DOWN LIST**************************

	// get list of dropdown option1
	public static void getDropDownList(String locator) {

		// if this doesnt work, use getAllOptions() method
		List<WebElement> options = driver.findElements(By.xpath(locator));

		List<String> text = new ArrayList<String>();
		for (int i = 1; i < options.size(); i++) {
			text.add(options.get(i).getText());
		}

	}

	// get list of dropdown option2
	public static List<String> getAllOptions(By by) {
		List<String> options = new ArrayList<String>();
		for (WebElement option : new Select(driver.findElement(by)).getOptions()) {
			String txt = option.getText();
			if (option.getAttribute("value") != "")
				options.add(option.getText());
		}
		return options;
	}

	// get list of elements by xpath
	public static List<WebElement> getListOfWebElementsByXpath(String locator) {
		List<WebElement> list = new ArrayList<WebElement>();
		list = driver.findElements(By.xpath(locator));

		return list;

	}

	public static List<WebElement> getListOfWebElementsByID(String locator) {
		List<WebElement> list = new ArrayList<WebElement>();
		list = driver.findElements(By.id(locator));

		System.out.println(list.toString());

		return list;
	}

	public static List<WebElement> printListOfWebElementsByID(String locator) {

		WebElement element = driver.findElement(By.id(locator));
		Select sel = new Select(element);
		List<WebElement> options = sel.getOptions();
		int size = options.size();
		System.out.println("***Data from WebApp***");

		for (int i = 0; i < size; i++) {

			String optionName = options.get(i).getText();
			System.out.println(optionName);

		}

		return options;
	}

	public static List<String> getListOfString(List<WebElement> list) {

		List<String> items = new ArrayList<String>();
		for (WebElement element : list) {

			items.add(element.getText()); // using the Element Text
		}
		return items;

	}

	// *********************SCREEN SHOT**************************

	// used to capture screen shot create file name
	public static String getRandomString(int length) {
		StringBuilder sb = new StringBuilder();
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		for (int i = 0; i < length; i++) {
			int index = (int) Math.random() * characters.length();
			sb.append(characters.charAt(index));

		}
		return sb.toString();
	}

	public static void takeScreenshotAtEndOfTest() throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String currentDir = System.getProperty("user.dir");

		FileUtils.copyFile(scrFile, new File(currentDir + "/screenshots/" + System.currentTimeMillis() + ".png"));

	}

	// ********************HANDLING ALERTS***************************

	// handling Alert
	public static boolean isAlertPresent() {

		try {
			driver.switchTo().alert();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void alertAccept() throws InterruptedException {

		WebDriver driver = null;
		Alert alert = driver.switchTo().alert();

		alert.accept();
	}

	// same as alertAccept method
	public static void okAlert() {
		Alert alert = driver.switchTo().alert();
		alert.accept();
	}

	public void cancelAlert() {
		Alert alert = driver.switchTo().alert();
		alert.dismiss();
	}

	public static void getAlertText(Alert verifiedText) {

		Alert text = driver.switchTo().alert();
		System.out.println("Text of the alert is : " + text);

		if (verifiedText != text) {
			System.out.println("alert does not equal : " + verifiedText);

		}

	}

	// iFrame Handle
	public static void switchToIframe(String nameOrID) {

		// make sure you get the id or name of the iframe and pass it as element
		// so create a variable and store the webelement object and pass it to the
		// method parameter
		driver.switchTo().frame(nameOrID);

	}

	// counting iframe handles
	public static void countIframeHandles(String tagNameLocator) {

		int iFrameElements = driver.findElements(By.tagName(tagNameLocator)).size();

		System.out.println("total count of iframes on this page is : " + iFrameElements);

	}

	public static void goBackToHomeWindow() {

		driver.switchTo().defaultContent();
	}

	// Working with Window Handles
	public static void getWindowHandle() {
		// returns parent window handle
		String primeWindow = driver.getWindowHandle();

	}

	// switching from parent window to child window
	public static void switchParentToChildWindow() {

		Set<String> allWindows = driver.getWindowHandles();

		Iterator<String> allWindow = allWindows.iterator();

		String parentWindow = allWindow.next();

		String childWindow = allWindow.next();

		driver.switchTo().window(childWindow);

	}

	public static void getAllWindowHandles() {

		Set<String> allWindows = driver.getWindowHandles();

		System.out.println(allWindows);

	}

	public static void navigateBack() {

		driver.navigate().back();
	}

	public static void navigateForward() {
		driver.navigate().forward();
	}

	// *********************ALL WAIT TYPES******************************

	// wait for element to be clickable by any type
	public static void waitUntilClickable_UsingBy(By locator) {

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable((locator)));

	}

	// wait for element to be clickable by xpath
	public static void waitUntilClickAble(By locator) {

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(locator));

	}

	// wait for page to load completely
	public static void implicitWait(int impWait, int pageLoadT) {

		driver.manage().timeouts().implicitlyWait(impWait, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(35, TimeUnit.SECONDS);

	}

	// use this as an example to all other wait types
	public static void waitUntilVisible(By locator) {

		WebDriverWait wait = new WebDriverWait(driver, 10);

		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

	}

	// Explicit wait for an element to be present and then utilize it
	public static WebElement waitForElement(int timeout, By locator) {

		WebElement element = null;

		try {
			// create an element object before action

			System.out.println("waiting for maximum :: " + timeout + "seconds for the element to be available");
			WebDriverWait wait = new WebDriverWait(driver, 3);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			System.out.println("element appeared on the webpage");

		} catch (Exception e) {

			System.out.println("element not appeared on the webpage");

		}
		return element;

	}

	// wait for element to be selecatable by any locator using By
	public static void waitUntilSelectable(By locator) {

		WebDriverWait wait = new WebDriverWait(driver, 10);
		boolean element = wait.until(ExpectedConditions.elementToBeSelected(locator));

		// create an element object before action
	}

	// mouse hover by xpath
	public static void mouseHover(String xpathLocator) {

		Actions actions = new Actions(driver);
		WebElement dealsElement = driver.findElement(By.xpath(xpathLocator));
		actions.moveToElement(dealsElement).build().perform();

	}

	// drag and drop using By method
	public static void dragAndDropByAnyLocatorType(By fromLocator, By toLocator) throws InterruptedException {

		WebElement fromElement1 = driver.findElement((fromLocator));
		WebElement toElement1 = driver.findElement((toLocator));

		Actions action = new Actions(driver);

		// Click and hold, move to element, release, build and perform
		action.clickAndHold(fromElement1).perform();
		sleepFor(2);
		action.moveToElement(toElement1).perform();
		sleepFor(2);
		action.release(toElement1).perform();

	}

	// Drag and drop method option1
	public static void dragAndDrop(String fromLocatorXpath, String toLocatorXpath) throws InterruptedException {

		WebElement fromElement1 = driver.findElement(By.xpath(fromLocatorXpath));
		WebElement toElement1 = driver.findElement(By.xpath(toLocatorXpath));

		Actions action = new Actions(driver);

		// Click and hold, move to element, release, build and perform
		action.clickAndHold(fromElement1).perform();
		sleepFor(2);
		action.moveToElement(toElement1).perform();
		sleepFor(2);
		action.release(toElement1).perform();

	}

	// drag and drop method option2
	public static void dragAndDropMethod(String fromLocatorXpath, String toLocatorXpath) {

		WebElement fromElement1 = driver.findElement(By.xpath(fromLocatorXpath));
		WebElement toElement1 = driver.findElement(By.xpath(toLocatorXpath));

		Actions actions = new Actions(driver);

		actions.dragAndDrop(fromElement1, toElement1);

	}

	// getting coordinates of window
	public static void getWindowCoordinates() {

		int xCoordinate = driver.manage().window().getPosition().getX();
		int yCoordinate = driver.manage().window().getPosition().getY();

		System.out.println("x Coordinate is " + xCoordinate);
		System.out.println("y Coordinate is " + yCoordinate);

		/*
		 * Point point = driver.manage().window().getPosition();
		 * 
		 * point.getX(); point.getY();
		 * 
		 */

	}

	public static void moveToNewWindows(WebDriver driver, String windowTitle) {
		boolean windowExists = false;
		Set<String> windows = driver.getWindowHandles();
		for (String window : windows) {
			driver.switchTo().window(window);
			if (driver.getTitle().contains(windowTitle)) {
				windowExists = true;

				break;
			}
		}
		if (!windowExists) {
			Assert.fail(windowTitle + " Title window not exists");

		}
	}

}
