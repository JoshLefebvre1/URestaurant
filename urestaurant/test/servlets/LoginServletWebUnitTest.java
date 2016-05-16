package servlets;

import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

// https://jwebunit.github.io/jwebunit/
public class LoginServletWebUnitTest {

	private static final String WEBSITE_URL = "http://localhost:8080/urestaurant/";
	private WebTester wt;
	
	//replace these with an existing user in your database
	private final String USERNAME = "f";
	private final String PASSWORD = "abc123!";
	
	@Before
	public void setup() {
		wt = new WebTester();
		wt.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		wt.getTestContext().setBaseUrl(WEBSITE_URL);
	}
	
	@Test
	public void sanity() {
		wt.beginAt("/index.jsp");
		wt.assertTitleEquals("Urestaurant");
		wt.assertFormPresent("login-frm");
	}
	
	@Test
    public void testLogin() throws InterruptedException {
		 wt.beginAt("/index.jsp");
		 wt.assertFormPresent("login-frm");
		 wt.setTextField("username", USERNAME);
		 wt.setTextField("password", PASSWORD);
		 wt.assertTextFieldEquals("username", USERNAME);
		 wt.assertTextFieldEquals("password", PASSWORD);
		 wt.submit();
		 //wt.clickButton("login-btn");
		 Thread.sleep(100); // delay to ensure that main page is completely loaded
		 wt.assertLinkPresent("browse-events");
		 //wt.assertButtonNotPresent("login-btn");
		 //wt.assertElementPresent("tabs");
    }
	
//	@Test
//    public void testSignUp() throws InterruptedException {
//		String uuid = UUID.randomUUID().toString().substring(0, 8);
//		
//		 wt.beginAt("/index.jsp");
//		 wt.assertFormPresent("sign-in-frm");
//		 wt.assertLinkPresent("sgn-btn");
//		// wt.clickLinkWithText("Sign Up");
//		 Thread.sleep(100); 
//		 wt.setTextField("username", "FirstName");
//		 wt.setTextField("sgn-lastname", "LastName");
//		 wt.setTextField("sgn-username", "Tester" + uuid);
//		 wt.setTextField("sgn-password", PASSWORD);
//		 wt.setTextField("sgn-confirm", PASSWORD);
//		 wt.clickButton("sgn-signup");
//		// wt.setTextField("password", PASSWORD);
//		 //wt.assertTextFieldEquals("username", USERNAME);
//		 //wt.assertTextFieldEquals("password", PASSWORD);
//		 //wt.submit();
//		 Thread.sleep(100); // delay to ensure that main page is completely loaded
//		 wt.assertLinkPresent("browse-events");
//		 //wt.assertButtonNotPresent("login-btn");
//		 //wt.assertElementPresent("tabs");
//    }
	
	
}
