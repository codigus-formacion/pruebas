package es.codeurjc.test.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageControllerTest {

	@LocalServerPort
    int port;

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
		
		driver.get("http://localhost:"+this.port+"/");

		String newTitle = "MessageTitle";
		String newBody = "MessageBody";

		Thread.sleep(1000);		
		
		driver.findElement(By.id("title-input")).sendKeys(newTitle);
		driver.findElement(By.id("body-input")).sendKeys(newBody);

		Thread.sleep(1000);
		
		driver.findElement(By.id("submit")).click();

		Thread.sleep(1000);
		
		String title = driver.findElement(By.id("title")).getText();
		String body = driver.findElement(By.id("body")).getText();

		assertThat(title).isEqualTo(newTitle);
		assertThat(body).isEqualTo(newBody);

	}

}
