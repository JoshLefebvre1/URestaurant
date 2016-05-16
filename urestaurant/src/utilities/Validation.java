/*
 * File name: Validation.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Form validation class.
 */
package utilities;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Validation {
	
	private Validation() { }
	
	// Password validation
	/**
	 * Encrypts the client's password attempt and compares to the one stored in the database.
	 * @param passwordAttempt The client's password attempt
	 * @param storedPassword The hashed password stored in the database
	 * @return true if password matches and false if password does not match
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * http://howtodoinjava.com/optimization/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 */
	public static boolean validatePassword(String passwordAttempt, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
         
        PBEKeySpec spec = new PBEKeySpec(passwordAttempt.toCharArray(), salt, iterations, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
         
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++) diff |= hash[i] ^ testHash[i];

        return diff == 0;
    }
	/**
	 * Transforms an encoded string into a byte array
	 * @param hex Encoded string
	 * @return The byte array derived from encoded string
	 * http://howtodoinjava.com/optimization/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 */
    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }
    // End password validation
    
	// Sign up form validation
	/**
	 * Validates a new user to store in the database.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @return a boolean value stating the validity of the sign up form.
	 * @throws SQLException
	 */
	public static boolean validateUser(Connection connection, HttpServletRequest request) throws SQLException {
		boolean isValid = true;
		boolean tempIsValid = true;
		// username validation
		tempIsValid = request.getParameter("username").matches("[a-zA-Z0-9_!@#$%&-]+"); // TODO regex for username
		if (!tempIsValid) isValid = false;	
		tempIsValid = verifyUsername(connection, request);
		if (isValid) isValid = tempIsValid;
		// email validation
		tempIsValid = request.getParameter("email").matches("[^\\s|^@]+@[^\\.]+\\.[a-zA-Z]{2,4}"); // TODO regex for email
		if (isValid) isValid = tempIsValid;
		// firstname validation
		tempIsValid = request.getParameter("firstname").matches("[\\w]+"); // TODO regex for firstname
		if (isValid) isValid = tempIsValid;
		// lastname validation
		tempIsValid = request.getParameter("lastname").matches("[\\w]+"); // TODO regex for lastname
		if (isValid) isValid = tempIsValid;
    	// password validation
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");   	
    	tempIsValid = password.matches("[^\\s]{8,}?"); // TODO regex for password
    	if (isValid) isValid = tempIsValid;
    	tempIsValid = password.equals(confirm);
    	if (isValid) isValid = tempIsValid;
    	return isValid;
	}
	
	/**
	 * Validates a new user to store in the database. Formats the result as a JSON object to describe every field.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @param json string builder to send specific details back to the client.
	 * @return a boolean value stating the validity of the sign up form.
	 * @throws SQLException
	 */
	public static boolean validateUserWithJSON(Connection connection, HttpServletRequest request, StringBuilder json) throws SQLException {
		String message = "\"ok\"";
		boolean isValid = true;
		boolean tempIsValid = true;
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}

		json.append("{ ");
		
		// username validation
		json.append("\"username\" : { \"isvalid\" : ");
		tempIsValid = request.getParameter("username").matches("[a-zA-Z0-9_!@#$%&-]+"); // TODO regex for username
		if (!tempIsValid) {
			isValid = tempIsValid;
			message = "\"" + bundle.getString("usernameFormat") + "\"";
		}
		json.append(tempIsValid)
			.append(" , \"isunique\" : ");		
		tempIsValid = verifyUsername(connection, request);
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("usernameExists") + "\"";
		}
		json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }, ");
		
		// email validation
		json.append("\"email\" : { \"isvalid\" : ");
		tempIsValid = request.getParameter("email").matches("[^\\s|^@]+@[^\\.]+\\.[a-zA-Z]{2,4}"); // TODO regex for email
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("enterValidEmail") + "\"";
		}
		/*json.append(tempIsValid)
			.append(" , \"isunique\" : ");
		tempIsValid = verifyEmail(connection, request);
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("emailAlreadyExists") + "\"";
		}*/
		json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }, ");
		
		// firstname validation
		tempIsValid = request.getParameter("firstname").matches("[\\w]+"); // TODO regex for firstname
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("whatsYourName") + "\"";
		}
		json.append("\"firstname\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" } , ");
		
		// lastname validation
		tempIsValid = request.getParameter("lastname").matches("[\\w]+"); // TODO regex for lastname
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("whatsYourName") + "\"";
		}
		json.append("\"lastname\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" } , ");
		
    	// password validation
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");   	
    	json.append("\"password\" : { \"isvalid\" : ");
    	tempIsValid = password.matches("[\\p{Graph}]{6,}?"); // TODO regex for password
    	if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("passwordFormat") + "\"";
		}
    	json.append(tempIsValid)
    		.append(" , \"isconfirmed\" : ");
    	tempIsValid = password.equals(confirm);
    	if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("passwordDoesntMatch") + "\"";
		}
    	json.append(tempIsValid).append(", \"message\" : ").append(message)
    		.append(" } , \"confirm\" : { \"isvalid\" : ");
    	tempIsValid = !confirm.isEmpty();
    	if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("reenterPassword") + "\"";
		}
    	json.append(!confirm.isEmpty()).append(", \"message\" : ").append(message).append(" } }");
    	return isValid;
	}
	
	/**
	 * Checks if the username already exists.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @return True if the username is unique and False if the username already exists in the database
	 * @throws SQLException
	 */
	private static boolean verifyUsername(Connection connection, HttpServletRequest request) throws SQLException {
		boolean isUnique;
    	final String lookupSQL = "SELECT id FROM users WHERE username = ?";    	
        PreparedStatement lookupStmt = connection.prepareStatement(lookupSQL);
        lookupStmt.setString(1, request.getParameter("username"));
        ResultSet resultSet = lookupStmt.executeQuery(); 
        if (resultSet.isBeforeFirst()) isUnique = false;
        else isUnique = true;
		lookupStmt.close();
        resultSet.close();
        return isUnique;
	}
	
	/**
	 * Checks if the requested email already exists in the database.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @return True if the email is unique and False if the email already exists in the database
	 * @throws SQLException
	 */
	private static boolean verifyEmail(Connection connection, HttpServletRequest request) throws SQLException {
		boolean isUnique;
    	final String lookupSQL = "SELECT id FROM emails WHERE email = ? AND contact_type_id = 1";    	
        PreparedStatement lookupStmt = connection.prepareStatement(lookupSQL);
        lookupStmt.setString(1, request.getParameter("email"));
        ResultSet resultSet = lookupStmt.executeQuery();        
        if (resultSet.isBeforeFirst()) isUnique = false;
        else isUnique = true;
        lookupStmt.close();
        resultSet.close();
		return isUnique;
	}
	// end sign up form validation
	
	// Event form validation
	/**
	 * Validates a new event to store in the database. Formats the result as a JSON object to describe every field.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @param json string builder to send specific details back to the client.
	 * @return a boolean value stating the validity of the sign up form.
	 * @throws SQLException
	 */
	public static boolean validateEventWithJSON(Connection connection, HttpServletRequest request, Event event) throws SQLException {
		String paramString;
		String message = "\"ok\"";
		boolean isValid = true;
		boolean tempIsValid = true;
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}

		event.getJsonValidation().append("{ ");
		
		// event name validation
		event.getJsonValidation().append("\"name\" : { \"isvalid\" : ");
		paramString = request.getParameter("name");
		tempIsValid = paramString.matches(".{1,45}"); // matches anything not whitespace, under 45 chars.
		if (!tempIsValid) {
			isValid = tempIsValid;
			message = "\"" + bundle.getString("eventnameFormat") + "\"";
		} else event.setName(paramString);
		event.getJsonValidation().append(tempIsValid).append(", \"message\" : ").append(message).append(" }, ");
		
		// location validation
		paramString = request.getParameter("location");
		tempIsValid = paramString.matches(".{1,84}"); // matches any characters less than 85 in total. 
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("locationFormat") + "\"";
		} else event.setLocation(paramString);
		event.getJsonValidation().append("\"location\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" } , ");
		
		try {
			boolean flag = true;
			String dateString, timeString;
			// start date validation
			dateString = request.getParameter("startdate");
			tempIsValid = dateString.matches("([0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])))"); // TODO regex for start date
			if (!tempIsValid) {
				flag = false;
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("dateFormat") + "\"";
			}
			event.getJsonValidation().append("\"startdate\" : { \"isvalid\" : ").append(tempIsValid)
				.append(", \"message\" : ").append(message).append(" } , ");
			
			// start time validation
			timeString = request.getParameter("starttime");
			tempIsValid = timeString.matches("(([1-9]|(1[0-2])):[0-5][0-9] (AM|PM))"); // TODO regex for start time
			if (!tempIsValid) {
				flag = false;
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("timeFormat") + "\"";
			}
			event.getJsonValidation().append("\"starttime\" : { \"isvalid\" : ").append(tempIsValid)
				.append(", \"message\" : ").append(message).append(" } , ");
			
			if (flag) {
				SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd h:mm a");
				Date date = dateTime.parse(String.format("%s %s", dateString, timeString));
				event.setStartDateTime(new Timestamp(date.getTime()));
			}
		} catch (ParseException e) {
			// TODO log error
		}
		
		try {
			boolean flag = true;
			String dateString, timeString;
			// end date validation
			dateString = request.getParameter("enddate");
			tempIsValid = dateString.matches("([0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1])))"); // TODO regex for end date
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("dateFormat") + "\"";
			}
			event.getJsonValidation().append("\"enddate\" : { \"isvalid\" : ").append(tempIsValid)
				.append(", \"message\" : ").append(message).append(" } , ");
			
			// end time validation
			timeString = request.getParameter("endtime");
			tempIsValid = timeString.matches("(([1-9]|(1[0-2])):[0-5][0-9] (AM|PM))"); // TODO regex for end time
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("timeFormat") + "\"";
			}
			event.getJsonValidation().append("\"endtime\" : { \"isvalid\" : ").append(tempIsValid)
				.append(", \"message\" : ").append(message).append(" } , ");
			
			if (flag) {
				SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd h:mm a");
				Date date = dateTime.parse(String.format("%s %s", dateString, timeString));
				event.setEndDateTime(new Timestamp(date.getTime()));
			}
		} catch (ParseException e) {
			// TODO log error
		}
		
		// description validation
		paramString = request.getParameter("description");
		tempIsValid = paramString.matches(".{0,250}"); //matches any chars under 250 count 
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("descriptionFormat") + "\"";
		} else event.setDescription(paramString);
		event.getJsonValidation().append("\"description\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" } , ");

		// ispublic validation
		paramString = request.getParameter("ispublic");
		tempIsValid = paramString.matches("true|false"); 
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("ispublicFormat") + "\"";
		} else event.setPublic(paramString.equals("true"));
		event.getJsonValidation().append("\"ispublic\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" } }");
		
    	return isValid;
	}
	// end event form validation
	
	// Contact info validation
	/**
	 * Validates the contact form
	 * @param connection
	 * @param request
	 * @param json
	 * @return true is the form is valid otherwise false
	 */
	public static boolean validateContactInfoWithJSON(Connection connection, HttpServletRequest request, StringBuilder json) {
		String message = "\"ok\"";
		boolean isValid = true;
		boolean tempIsValid = true;
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}

		json.append("{ ");
		// firstname validation
		String firstName = request.getParameter("firstname");
		tempIsValid = firstName != null && firstName.matches("[\\w]+"); // TODO regex for firstname
		if (!tempIsValid) {
			if (isValid) isValid = tempIsValid;
			message = "\"" + bundle.getString("whatsYourName") + "\"";
		}
		json.append("\"firstname\" : { \"isvalid\" : ").append(tempIsValid)
			.append(", \"message\" : ").append(message).append(" }");
		
		// lastname validation
		String lastName = request.getParameter("lastname");
		if (lastName != null && !lastName.isEmpty()) {
			tempIsValid = lastName.matches("[\\w]+"); // TODO regex for lastname
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("whatsYourName") + "\"";
			}
			json.append(" , \"lastname\" : { \"isvalid\" : ").append(tempIsValid)
				.append(", \"message\" : ").append(message).append(" } ");
		}
		
		// email validation
		for (int i = 0; i == 0 || request.getParameter("email" + i) != null && !request.getParameter("email" + i).isEmpty(); ++i) {
			json.append(" , \"email" + i + "\" : { \"isvalid\" : ");
			tempIsValid = request.getParameter("email" + i).matches("[^\\s|^@]+@[^\\.]+\\.[a-zA-Z]{2,4}"); // TODO regex for email
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("enterValidEmail") + "\"";
			}
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
		}
		
		// phone number validation
		for (int i = 0; request.getParameter("phone" + i) != null && !request.getParameter("phone" + i).isEmpty(); ++i) {
			json.append(" , \"phone" + i + "\" : { \"isvalid\" : ");
			tempIsValid = request.getParameter("phone" + i).matches("(\\([0-9]{3}\\)) [0-9]{3}-[0-9]{4}"); // TODO regex for phone
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("enterValidPhoneNumber") + "\"";
			}
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
        }
		json.append(" } ");
		
		return isValid;
	}
	
	public static boolean validateUserInfoWithJSON(Connection connection, HttpServletRequest request, StringBuilder json) 
	{
		String message = "\"ok\"";
		boolean isValid = true;
		boolean tempIsValid = true;
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) 
		{
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}

		json.append("{ ");
		// firstname validation
		String firstName = request.getParameter("firstname");
		tempIsValid = firstName != null && firstName.matches("[\\w]+"); // TODO regex for firstname
		if (!tempIsValid)
		{
			if (isValid) 
				isValid = tempIsValid;
			message = "\"" + bundle.getString("whatsYourName") + "\"";
		}
		json.append("\"firstname\" : { \"isvalid\" : ").append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
		
		// lastname validation
		String lastName = request.getParameter("lastname");
		tempIsValid = lastName.matches("[\\w]+"); // TODO regex for lastname
		if (!tempIsValid) 
		{
			if (isValid) 
				isValid = tempIsValid;
			message = "\"" + bundle.getString("whatsYourName") + "\"";
		}
		json.append(" , \"lastname\" : { \"isvalid\" : ").append(tempIsValid).append(", \"message\" : ").append(message).append(" } ");
		
		
		// email validation
		String email = request.getParameter("email");
		if(email != null && !email.isEmpty()) 
		{
			json.append(" , \"email\" : { \"isvalid\" : ");
			tempIsValid = email.matches("[^\\s|^@]+@[^\\.]+\\.[a-zA-Z]{2,4}"); // TODO regex for email
			if (!tempIsValid) 
			{
				if (isValid) 
					isValid = tempIsValid;
				message = "\"" + bundle.getString("enterValidEmail") + "\"";
			}
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
		}
		
		// email validation(no email provided)
		if(email == null || email.isEmpty()) 
		{
			json.append(" , \"email\" : { \"isvalid\" : ");
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
		}

		// phone number validation
		String phone = request.getParameter("phone");
		if(phone != null && !phone.isEmpty()) 
		{
			json.append(" , \"phone\" : { \"isvalid\" : ");
			tempIsValid = phone.matches("(\\([0-9]{3}\\)) [0-9]{3}-[0-9]{4}"); // TODO regex for phone
			if (!tempIsValid) {
				if (isValid) isValid = tempIsValid;
				message = "\"" + bundle.getString("enterValidPhoneNumber") + "\"";
			}
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
        }
		
		// phone number validation (no phone provided)
		if(phone==null || phone.isEmpty()) 
		{
			json.append(" , \"phone\" : { \"isvalid\" : ");
			json.append(tempIsValid).append(", \"message\" : ").append(message).append(" }");
        }
		
		json.append(" } ");
		
		return isValid;
	}
	// End contact info validation
}
