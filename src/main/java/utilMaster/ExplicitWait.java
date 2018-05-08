package utilMaster;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ExplicitWait {
	
	private static WebDriver driver;
	private static WebDriverWait wait;
	
	public void waitUntilClickable(By locator) {
		
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.elementToBeClickable(locator));

	}
	
	// wait for page to load completely
	public static void implicitWait(int impWait, int pageLoadT) {

		driver.manage().timeouts().implicitlyWait(impWait, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(pageLoadT, TimeUnit.SECONDS);

	}


	// Explicit wait for an element to be present and then utilize it
	public static WebElement waitForElementVisablity(int timeout, By locator) {

		WebElement element = null;

		try {
			// create an element object before action

			System.out.println("waiting for maximum :: " + timeout + "seconds for the element to be available");
			wait = new WebDriverWait(driver, 7);
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			System.out.println("element appeared on the webpage");

		} catch (Exception e) {

			System.out.println("element not appeared on the webpage");

		}
		return element;

	}

	// wait for element to be selecatable by any locator using By
	public static void waitUntilSelectable(By locator) {

		wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeSelected(locator));
		
	}
	
	public static void waitForAlert() {
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.alertIsPresent());
		
	}

	public static void waitToSwitchFrameByIDorName(String frameLocator) {
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
	}
	
	
	public static void waitToSwitchFrameByIndex(String frameLocator) {
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
		
	}
	
	public static void waitForElementToBeInvisable(WebElement element) {
		wait = new WebDriverWait(driver, 7);	
		wait.until(ExpectedConditions.invisibilityOf(element));
	}
	
	public static void waitForElementAllElementsToBePresentInDOM(By locator) {
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
	}
	
	public static void waitForTextToBe(By locator, String text) {
		wait = new WebDriverWait(driver, 7);
		wait.until(ExpectedConditions.textToBePresentInElementValue(locator, text));
	}
	
	public WebElement waitForElementToBeRefreshedAndClickable(WebDriver driver, By by) {
	    return new WebDriverWait(driver, 30)
	            .until(ExpectedConditions.refreshed(
	                    ExpectedConditions.elementToBeClickable(by)));
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
