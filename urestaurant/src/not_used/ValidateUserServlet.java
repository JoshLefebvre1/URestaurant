/*
 * NOT USED IN APPLICATION
 * File name: ValidateUserServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Form validation to sign up a new user.
 */
package not_used;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utilities.Validation;
import connections.DatabaseConnectionFactory;

public class ValidateUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public ValidateUserServlet() { super(); } 
	/**
	 * Servlets POST method.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
	    try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) {
	    	StringBuilder json = new StringBuilder("{ \"user\" : ");
        	boolean isValid = Validation.validateUserWithJSON(connection, request, json);
        	json.append(", \"isvalid\" : ").append(isValid).append(" }");
        	response.getWriter().write(json.toString());
        	response.setStatus(HttpServletResponse.SC_OK);
	    } catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		}
	}
	/**
	 * Validates a new user to store in the database. Formats the result as a JSON object to describe every field.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @return The JSON object returned as a string.
	 * @throws SQLException
	 */
	private String validateUser(Connection connection, HttpServletRequest request) throws SQLException {
		StringBuilder json = new StringBuilder();
		json.append("{ ");
		json.append("\"username\" : { \"isempty\" : ")
			.append(request.getParameter("username").isEmpty())
			.append(" , \"isunique\" : ")
			.append(verifyUsername(connection, request)).append(" }, ");
		json.append("\"email\" : { \"isempty\" : ")
			.append(request.getParameter("email").isEmpty())
			.append(" , \"isunique\" : ")
			.append(verifyEmail(connection, request)).append(" }, ");
		json.append("\"firstname\" : { \"isempty\" : ").append(request.getParameter("firstname").isEmpty()).append(" } , ");
		json.append("\"lastname\" : { \"isempty\" : ").append(request.getParameter("lastname").isEmpty()).append(" } , ");		
    	
		String password = request.getParameter("password");
		String confirm = request.getParameter("confirm");   	
    	json.append("\"password\" : { \"isempty\" : ")
    		.append(password.isEmpty())
    		.append(" , \"isconfirmed\" : ")
    		.append(password.equals(confirm))
    		.append(" } ,");
    	json.append("\"confirm\" : { \"isempty\" : ").append(confirm.isEmpty()).append(" } }");
    	return json.toString();
	}
	/**
	 * Checks if the username already exists.
	 * @param connection The connection the the database to query for comparisons against existing fields.
	 * @param request The servlet request to obtain request parameters.
	 * @return True if the username is unique and False if the username already exists in the database
	 * @throws SQLException
	 */
	private boolean verifyUsername(Connection connection, HttpServletRequest request) throws SQLException {
		boolean isUnique;
    	final String lookupSQL = "select id from users where username = ?";    	
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
	private boolean verifyEmail(Connection connection, HttpServletRequest request) throws SQLException {
		boolean isUnique;
    	final String lookupSQL = "select id from users where email = ?";    	
        PreparedStatement lookupStmt = connection.prepareStatement(lookupSQL);
        lookupStmt.setString(1, request.getParameter("email"));
        ResultSet resultSet = lookupStmt.executeQuery();        
        if (resultSet.isBeforeFirst()) isUnique = false;
        else isUnique = true;
        lookupStmt.close();
        resultSet.close();
		return isUnique;
	}
}
