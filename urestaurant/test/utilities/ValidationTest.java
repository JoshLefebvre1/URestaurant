/*
 * File name: LoginServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Olexander Agafonov 
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Unit tests for password validation on login attempts.
 */
package utilities;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.*;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Validation.class)
public class ValidationTest {
	
	private HttpServletRequest request;
	private Connection connection;
	private Validation validation;
	
	@Before
	public void setUp(){
		request = PowerMock.createMock(HttpServletRequest.class);
		connection = PowerMock.createMock(Connection.class);
		PowerMock.mockStatic(Validation.class);
		validation = PowerMock.createPartialMock(Validation.class, "verifyUsername");
	}

//	@Test
//	public void testValidatePasswordIsSuccessful() throws NoSuchAlgorithmException, InvalidKeySpecException {
//		SecureRandom random = new SecureRandom();
//		for (int i = 0; i < 50; i++) {
//			String password = new BigInteger(130, random).toString(32);
//			assertTrue(validation.validatePassword(password, encryptPassword(password)));
//		}
//	}
	
	private String encryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// generate salt
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte salt[] = new byte[8];
		random.nextBytes(salt);
		// encryption
		int iterations = 1000;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        SecretKey key = skf.generateSecret(spec);
        byte[] hash = key.getEncoded( );
        return String.format("%d:%s:%s", iterations, toHex(salt), toHex(hash));
	}
	
	private String toHex(byte[] array) {
		BigInteger bigInt = new BigInteger(1, array);
        String hex = bigInt.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) return String.format("%0" + paddingLength + "d", 0) + hex;
        else return hex;
	}
	
	@Test
	public void testValidateUserIsSuccessful() throws Exception {
		EasyMock.expect(request.getParameter("username")).andReturn("Tester");
		PowerMock.expectPrivate(validation, "verifyUsername", connection, request).andReturn(true);
		EasyMock.expect(request.getParameter("email")).andReturn("Tester@Test.com");
		EasyMock.expect(request.getParameter("firstname")).andReturn("Tester");
		EasyMock.expect(request.getParameter("lastname")).andReturn("Tester");
		EasyMock.expect(request.getParameter("password")).andReturn("Tester1!");
		EasyMock.expect(request.getParameter("confirm")).andReturn("Tester1!");
		EasyMock.expect(validation.validateUser(connection, request)).andReturn(true);
		PowerMock.replayAll();
		
		assertTrue(Validation.validateUser(connection, request));
		
		PowerMock.verify(validation);
	}
	
	
	
}
