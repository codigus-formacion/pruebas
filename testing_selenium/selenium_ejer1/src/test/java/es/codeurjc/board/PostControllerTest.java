package es.codeurjc.board;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerTest {

	@LocalServerPort
	int port;

	private WebDriver driver;
	private WebDriverWait wait;

	@BeforeEach
	public void setup() {
		this.driver = new ChromeDriver();
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	@AfterEach
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	@DisplayName("When a post with a title is created, a message that it has been created is displayed and appears on the board")
	public void createPost() throws InterruptedException {

		// GIVEN
		driver.get("http://localhost:" + this.port + "/");

		// WHEN
		createPostAux("Michel", "Vendo moto roja", "Muy barata");

		// THEN
		this.wait.until(ExpectedConditions.textToBe(By.id("message"), "Post has been saved"));
		driver.findElement(By.linkText("Back to board")).click();
		this.wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Michel - Vendo moto roja")));
	}

	@Test
	@DisplayName("When a post is created without a title, a message that it has not been created is displayed and does not appear on the board.")
	public void createPostWithoutTitle() throws InterruptedException {
		// GIVEN
		driver.get("http://localhost:" + this.port + "/");

		// WHEN
		createPostAux("Mica", "", "Muy barata");

		// THEN
		this.wait.until(ExpectedConditions.textToBe(By.id("message"), "Post has not been saved: Title is empty"));
		driver.findElement(By.linkText("Back to board")).click();
		assertThrows(NoSuchElementException.class, () -> {
			driver.findElement(By.partialLinkText("Mica"));
		});
	}

	@Test
	@DisplayName("When an post is created and deleted, a message is displayed that the post has been deleted and does not appear on the board.")
	public void deletePost() throws InterruptedException {
		// GIVEN
		driver.get("http://localhost:" + this.port + "/");

		// WHEN
		createPostAux("Ivan", "Vendo moto azul", "Muy barata");
		driver.findElement(By.linkText("Back to board")).click();
		driver.findElement(By.partialLinkText("Vendo moto azul")).click();
		driver.findElement(By.id("delete-post")).click();

		// THEN
		this.wait.until(ExpectedConditions.textToBe(By.id("message"), "Post has been deleted"));
		driver.findElement(By.linkText("Back to board")).click();
		assertThrows(NoSuchElementException.class, () -> {
			driver.findElement(By.linkText("Ivan - Vendo moto azul"));
		});

	}

	private void createPostAux(String username, String title, String text) {
		driver.findElement(By.linkText("New Post")).click();

		driver.findElement(By.name("username")).sendKeys(username);
		driver.findElement(By.name("title")).sendKeys(title);
		driver.findElement(By.name("text")).sendKeys(text);

		driver.findElement(By.id("save-post")).click();
	}
}
