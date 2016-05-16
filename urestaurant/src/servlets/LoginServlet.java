/*
 * File name: LoginServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Joshua Lefebvre
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Login servlet to validate users in the database.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utilities.Validation;
import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() { super(); }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("application/json");
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}
		PrintWriter out = null;
		ResultSet resultSet = null;
		PreparedStatement selectStmt = null;
	    try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) 
	    {    	 
	    	out = response.getWriter();
	    	connection.setAutoCommit(true);
        	final String selectSQL = "SELECT * FROM users WHERE username = ?";  
	        selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
	        selectStmt.setString(1, request.getParameter("username"));
	        resultSet = selectStmt.executeQuery();
	        
	        
	        StringBuilder json = new StringBuilder("{ \"isvalid\" : ");
	        if (resultSet.next())//If the entered username exists in the database
	        {    
		        String returnedPassword =resultSet.getString("password");//Encrypted password from db
		        String enteredPassword =request.getParameter("password");//User entered password
		        if(Validation.validatePassword(enteredPassword, returnedPassword))//Checks to see if passwords match
		        {
		        	json.append("true,");
		        	int userId = resultSet.getInt("id");
		        	int contactId= resultSet.getInt("contact_id");
		        	session.setAttribute("isAuthenticated", true);
		        	session.setAttribute("username", resultSet.getString("username"));
		        	session.setAttribute("userid", userId);
		        	session.setAttribute("contactid", contactId);
		        	// init user_event from invite if user_event entry does not exist
		        	if (request.getParameter("aeb").equals("True") && !doesUserEventExist(connection, userId, (int)session.getAttribute("token"))) {
		        		insertUserEvent(connection, userId, (int)session.getAttribute("token"));
		        	}
		        }
		        else//Wrong Password
		        {
		        	json.append("false,");
		        }
	        }	        
	        else//Username does not exist in the database
	        {
	        	json.append("false,");
	        }
	        json.append(" \"message\" : \"").append(bundle.getString("loginMessage")).append("\" }");
	        out.write(json.toString());
	       
	        response.setStatus(HttpServletResponse.SC_OK);
	    } 
	    catch (SQLException e) 
	    {
	    	// TODO log error
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(e.getMessage());
			e.printStackTrace();
		} 
	    catch (NoSuchAlgorithmException e) 
	    {
	    	// TODO log error
	    	response.getWriter().write(e.getMessage());
			e.printStackTrace();
		} 
	    catch (InvalidKeySpecException e) 
	    {
	    	// TODO log error
	    	response.getWriter().write(e.getMessage());
			e.printStackTrace();
		} 
	    finally {
	    	if (out != null) out.close();
			if (resultSet != null)
				try { resultSet.close(); } catch (SQLException e) { /* TODO log error */ }
			if (selectStmt != null)
				try { selectStmt.close(); } catch (SQLException e) { /* TODO log error */ }
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
}
