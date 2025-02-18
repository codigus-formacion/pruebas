package es.codeurjc.test.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WikipediaTest {

	private WebDriver driver;
	
	@BeforeEach
	public void setupTest() {
		driver = new ChromeDriver();
	}

	@AfterEach
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void test() throws InterruptedException {

		driver.get("https://wikipedia.org");
				
		WebElement searchInput = driver.findElement(By.name("search"));
		
		Thread.sleep(5000);	

		searchInput.sendKeys("Rick Astley");
		
		Thread.sleep(5000);	
		
		searchInput.submit();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		
		Thread.sleep(5000);	
					
		WebElement text = wait.until(
			presenceOfElementLocated(By.id("mw-content-text")));
					
		assertTrue(text.getText().contains("Richard Paul Astley"));
		
	}

}







