/*
 * File name: SignUpServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Sign Up servlet to insert new users into the database.
 */
package servlets;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utilities.Validation;
import connections.DatabaseConnectionFactory;
/**
 * Sign Up servlet to insert new users into the database.
 * @author Christopher Elliott
 * @version 0.1
 */
@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public SignUpServlet() { super(); }

	/** Receives incoming POST requests to insert a new user into the database. */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("application/json");
		Connection connection = null;
	    try 
	    {
	    	connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
	    	connection.setAutoCommit(false);
	    	StringBuilder json = new StringBuilder("{ \"user\" : ");
        	boolean isValid = Validation.validateUserWithJSON(connection, request, json);
        	json.append(", \"isvalid\" : ").append(isValid).append(" }");
        	response.getWriter().write(json.toString());
        	if (isValid) 
        	{
        		int userId = insertUser(connection, request.getParameter("username"), encryptPassword(request.getParameter("password")));     
    	        if (userId <= 0) throw new SQLException("Error occured inserting a new user.");
	        	int contactId = insertContact(connection, request.getParameter("firstname"), request.getParameter("lastname"), userId);
	        	if (contactId <= 0) throw new SQLException("Error occured inserting a new contact.");
        		int emailId = insertContactEmail(connection, contactId, request.getParameter("email"));
        		if (emailId <= 0) throw new SQLException("Error occured inserting a new contact email.");
    			int updateRows = updateUser(connection, contactId, userId);
    			if (updateRows != 1) throw new SQLException("Error occured updating the user entries contact id.");
    			// Initialize user session
    			HttpSession session = request.getSession(true);
	        	session.setAttribute("isAuthenticated", true);
	        	session.setAttribute("username", request.getParameter("username"));
	        	session.setAttribute("userid", userId);
	        	session.setAttribute("contactid", contactId);
	        	
	        	
	        	// init user_event from invite if user_event entry does not exist
	        	if (request.getParameter("aeb").equals("True") && !doesUserEventExist(connection, userId, (int)session.getAttribute("token"))) {
	        		insertUserEvent(connection, userId, (int)session.getAttribute("token"));
	        	}
	        	// persist transaction to DB
	        	connection.commit();
        	}
        	response.setStatus(HttpServletResponse.SC_OK);
	    } 
	    catch (SQLException e) 
	    {
	    	try {  
	    		if (connection != null) connection.rollback();
	    	} catch (SQLException e1) { /* TODO log error */ }
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // TODO log error
		} 
	    catch (NoSuchAlgorithmException e) 
	    {
			try {  
	    		if (connection != null) connection.rollback();
	    	} catch (SQLException e1) { /* TODO log error */ }
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // TODO log error
		} 
	    catch (InvalidKeySpecException e) 
	    {
			try {  
	    		if (connection != null) connection.rollback();
	    	} catch (SQLException e1) { /* TODO log error */ }
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // TODO log error
		} 
	    finally 
	    {
			if (connection != null)
				try { connection.close(); } catch (SQLException e1) { /* TODO log error */ }
		}
	}
	/**
	 * Checks for existing user_event entry.
	 * @param connection
	 * @param userId
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	private boolean doesUserEventExist(Connection connection, int userId, int eventId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement selectUsrEvtStmt = null;
		try {
			final String insertUsrEvtSQL = "SELECT id FROM user_events WHERE user_id = ? AND event_id = ?";
			selectUsrEvtStmt = connection.prepareStatement(insertUsrEvtSQL);
			selectUsrEvtStmt.setInt(1, userId);
			selectUsrEvtStmt.setInt(2, eventId);
			resultSet = selectUsrEvtStmt.executeQuery();
			return resultSet.next();
		} finally {
			if (selectUsrEvtStmt != null) selectUsrEvtStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Inserts the user event entry from the invite.
	 * @param connection
	 * @param userId
	 * @param eventId
	 * @return
	 * @throws SQLException
	 */
	private int insertUserEvent(Connection connection, int userId, int eventId) throws SQLException {
		PreparedStatement insertUsrEvtStmt = null;
		try {
			final String insertUsrEvtSQL = "INSERT INTO user_events (user_id, event_id, user_event_type_id, user_event_status_id) VALUES (?,?,?,?)";
	    	insertUsrEvtStmt = connection.prepareStatement(insertUsrEvtSQL);
	    	insertUsrEvtStmt.setInt(1, userId);
	    	insertUsrEvtStmt.setInt(2, eventId);
	    	insertUsrEvtStmt.setInt(3, 2); // id=2 for attendee
	    	insertUsrEvtStmt.setInt(4, 4); // id=4 says this user is invited
	        return insertUsrEvtStmt.executeUpdate();
		} finally {
			if (insertUsrEvtStmt != null) insertUsrEvtStmt.close();
		}
	}
	/**
	 * Updates a user entry with a contact id
	 * @param connection
	 * @param contactId
	 * @return number of rows the update statement affected, should only be 1
	 * @throws SQLException
	 */
	private int updateUser(Connection connection, int contactId, int userId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement updateUsrStmt = null;
		try {
			final String updateUsrEvtSQL = "UPDATE users SET contact_id = ? WHERE id = ?";
			updateUsrStmt = connection.prepareStatement(updateUsrEvtSQL, Statement.RETURN_GENERATED_KEYS);
			updateUsrStmt.setInt(1, contactId);
			updateUsrStmt.setInt(2, userId);
			//updateUsrStmt.execute();
	    	return updateUsrStmt.executeUpdate();
		} finally {
			if (updateUsrStmt != null) updateUsrStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Insert a new user into the database
	 * @param connection
	 * @param username
	 * @param password the already encrypted password to be stored in the database
	 * @return The auto-generated primary key for the new entry.
	 * @throws SQLException
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 */
	private int insertUser(Connection connection, String username, String password) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertStmt = null;
		try {
			final String insertSQL = "INSERT INTO users (username, password) VALUES (?,?)";
	        insertStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
	        insertStmt.setString(1, username);
	        insertStmt.setString(2, password);
	        insertStmt.execute();
	        resultSet = insertStmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (insertStmt != null) insertStmt.close();
		}
	}
	/**
	 * Insert new email entry
	 * @param connection
	 * @param contactId
	 * @param email
	 * @return auto-generated id for the newly inserted email entry
	 * @throws SQLException
	 */
	private int insertContactEmail(Connection connection, int contactId, String email) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertEmailStmt = null;
		try {
			final String insertSQL = "INSERT INTO emails (contact_id,email,is_primary,contact_type_id) VALUES (?,?,?,?)";
			insertEmailStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			insertEmailStmt.setInt(1, contactId);
			insertEmailStmt.setString(2, email);
			insertEmailStmt.setBoolean(3, true);
			insertEmailStmt.setInt(4, 1); // id for primary contact
			insertEmailStmt.execute();
	        resultSet = insertEmailStmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (insertEmailStmt != null) insertEmailStmt.close();
		}
	}	
	/**
	 * Inserts a new contact entry
	 * @param connection
	 * @param firstName
	 * @param lastName
	 * @param userId
	 * @return the auto-generated id for the contact entry
	 * @throws SQLException
	 */
	private int insertContact(Connection connection, String firstName, String lastName, int userId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertCntctStmt = null;
		try {
			final String insertSQL = "INSERT INTO contacts (firstname,lastname,user_id) VALUES (?,?,?)";
			insertCntctStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			insertCntctStmt.setString(1, firstName);
			insertCntctStmt.setString(2, lastName);
			insertCntctStmt.setInt(3, userId);
			insertCntctStmt.execute();
	        resultSet = insertCntctStmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (insertCntctStmt != null) insertCntctStmt.close();
		}
	}	
	/**
	 * Encrypts the users password to store in the database
	 * @param password The password string to hash
	 * @return The hashed password
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * http://howtodoinjava.com/optimization/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 */
	private String encryptPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// generate salt
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte salt[] = new byte[8];
		random.nextBytes(salt);
		// encryption
		int iterations = 1000;
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        SecretKey key = skf.generateSecret(spec);
        byte[] hash = key.getEncoded( );
        return String.format("%d:%s:%s", iterations, toHex(salt), toHex(hash));
	}
	/**
	 * Encodes hash in hexadecimal form
	 * @param array
	 * @return The encoded string
	 * http://howtodoinjava.com/optimization/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 */
	private String toHex(byte[] array) {
		BigInteger bigInt = new BigInteger(1, array);
        String hex = bigInt.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) return String.format("%0" + paddingLength + "d", 0) + hex;
        else return hex;
	}
}
