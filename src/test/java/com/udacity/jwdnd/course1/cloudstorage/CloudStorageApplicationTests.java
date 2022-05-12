package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.Model.Credentials;
import com.udacity.jwdnd.course1.cloudstorage.PageObjects.CredentialsPage;
import com.udacity.jwdnd.course1.cloudstorage.PageObjects.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.PageObjects.NotesPage;
import com.udacity.jwdnd.course1.cloudstorage.PageObjects.RegistrationPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CloudStorageApplicationTests {

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private RegistrationPage registrationPage;
	private LoginPage loginPage;
	private NotesPage notesPage;
	private CredentialsPage credentialsPage;

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		registrationPage=new RegistrationPage(driver);
		loginPage=new LoginPage(driver);
		notesPage=new NotesPage(driver);
		credentialsPage=new CredentialsPage(driver);
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));

		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful.
		// You may have to modify the element "success-msg" and the sign-up
		// success message below depening on the rest of your code.
		*/
		//Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
	}



	@Test
	@Order(1)
	public void getLoginPage() {
		driver.get("http://localhost:" + this.port + "/login");
		Assertions.assertEquals("Login", driver.getTitle());
	}
	@Test
	@Order(2)
	public void signupAndLogin()
	{
		driver.get("http://localhost:" + this.port + "/signup");
		registrationPage.enterFirstname("Kayode");
		registrationPage.enterLastname("Awe");
		registrationPage.enterUsername("username");
		registrationPage.enterPassword("password");
		registrationPage.clickSubmitButton();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		driver.get("http://localhost:" + this.port + "/login");
		loginPage.enterUsername("username");
		loginPage.enterPassword("password");
		loginPage.login();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Assertions.assertEquals("Home", driver.getTitle());
	}
	@Test
	@Order(3)
	public void unAuthorizedUser()
	{
		driver.get("http://localhost:" + this.port + "/login");
		loginPage.enterUsername("username");
		loginPage.enterPassword("mypassword");
		loginPage.login();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Assertions.assertNotEquals("Home", driver.getTitle());
	}

	@Test
	@Order(4)
	public void AddNote()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Note");
		int intialRows=notesPage.checkRowsCount();
		addNoteFunction();
        int finalRows=notesPage.checkRowsCount();
        Assertions.assertEquals(intialRows+1,finalRows);
	}
    @Test
	@Order(5)
	public void deleteNote()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Note");
		addNoteFunction();
		driver.get("http://localhost:" + this.port + "/Note");
		WebElement lastRow =notesPage.getNotesRows().get(1);
		List<WebElement> columns=lastRow.findElements(By.tagName("td"));
		WebElement deleteButton=columns.get(0).findElement(By.tagName("a"));
		System.out.println("delete "+deleteButton.getText());
		int intialRows=notesPage.checkRowsCount();
		deleteButton.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		int finalRows=notesPage.checkRowsCount();

		Assertions.assertEquals(intialRows,finalRows+1);
	}
    @Test
	@Order(6)
	public void editNote()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Note");
		addNoteFunction();
		driver.get("http://localhost:" + this.port + "/Note");
		WebElement lastRow =notesPage.getNotesRows().get(1);
		List<WebElement> columns=lastRow.findElements(By.tagName("td"));
		WebElement editButton=columns.get(0).findElement(By.tagName("button"));
		editButton.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		notesPage.clearNoteTitle();
		notesPage.enterNoteTitle("new title");
		notesPage.clickSubmitNote();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		WebElement editedRow =notesPage.getNotesRows().get(1);
		WebElement titleColumn=editedRow.findElement(By.tagName("th"));
		String noteTitle=titleColumn.getText();
		System.out.println("noteTitle "+noteTitle);
		Assertions.assertEquals("new title",noteTitle);
	}

	@Test
	@Order(7)
	public void AddCredential()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Credentials");
		int intialRows=credentialsPage.checkRowsCount();
		addCredentialsFunction();
		int finalRows=credentialsPage.checkRowsCount();
		Assertions.assertEquals(intialRows+1,finalRows);
	}
	@Test
	@Order(8)
	public void EditCredential()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Credentials");
		addCredentialsFunction();
		driver.get("http://localhost:" + this.port + "/Credentials");
		WebElement lastRow =credentialsPage.getCredentialsRows().get(0);
		List<WebElement> columns=lastRow.findElements(By.tagName("td"));
		WebElement editButton=columns.get(0).findElement(By.tagName("button"));
		editButton.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		credentialsPage.clearUrl();
		credentialsPage.enterUrl("new url");
		credentialsPage.submitCredentials();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		WebElement editedRow =credentialsPage.getCredentialsRows().get(0);
		WebElement UrlColumn=editedRow.findElement(By.tagName("th"));
		String urlColumnText=UrlColumn.getText();
		System.out.println("url "+urlColumnText+" "+credentialsPage.checkRowsCount());
		Assertions.assertEquals("new url",urlColumnText);
	}
	@Test
	@Order(9)
	public void deleteCredentials()
	{
		login();
		driver.get("http://localhost:" + this.port + "/Credentials");
		addCredentialsFunction();
		driver.get("http://localhost:" + this.port + "/Credentials");
		WebElement lastRow =credentialsPage.getCredentialsRows().get(0);
		List<WebElement> columns=lastRow.findElements(By.tagName("td"));
		WebElement deleteButton=columns.get(0).findElement(By.tagName("a"));
		System.out.println("delete "+deleteButton.getText());
		int intialRows=credentialsPage.checkRowsCount();
		deleteButton.click();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		int finalRows=credentialsPage.checkRowsCount();

		Assertions.assertEquals(intialRows,finalRows+1);
	}
	public void login()
	{
		driver.get("http://localhost:" + this.port + "/login");
		loginPage.enterUsername("username");
		loginPage.enterPassword("password");
		loginPage.login();
	}
	public void addNoteFunction()
	{
		notesPage.clickAddNoteButton();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		notesPage.enterNoteTitle("Sample");
		notesPage.enterNoteDescription("This is a sample description");
		notesPage.clickSubmitNote();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	public void addCredentialsFunction()
	{
        credentialsPage.openCredentialsModal();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		credentialsPage.enterUrl("http://localhost:8080");
		credentialsPage.enterUsername("username");
		credentialsPage.enterPassword("password");
		credentialsPage.submitCredentials();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
	 * rest of your code.
	 * This test is provided by Udacity to perform some basic sanity testing of
	 * your code to ensure that it meets certain rubric criteria.
	 *
	 * If this test is failing, please ensure that you are handling redirecting users
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric:
	 * https://review.udacity.com/#!/rubrics/2724/view
	 */
	@Test
	@Order(10)
	public void testRedirection() {
		// Create a test account
		driver.get("http://localhost:" + this.port + "/signup");
		registrationPage.enterFirstname("Kayode");
		registrationPage.enterLastname("Awe");
		registrationPage.enterUsername("username1");
		registrationPage.enterPassword("password1");
		registrationPage.clickSubmitButton();

		String oldURL = "http://localhost:" + this.port + "/signup";

		// Wait for 10 secs for the login page to load
		WebDriverWait webDriverWait = new WebDriverWait(driver, 10);
		webDriverWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldURL)));

		// Check if we have been redirected to the log in page.
		Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}
}
