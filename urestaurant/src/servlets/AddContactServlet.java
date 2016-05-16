package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utilities.Validation;
import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class AddContactServlet
 */
@WebServlet("/addcontact")
public class AddContactServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddContactServlet() { super(); }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int userId = -1;
		response.setContentType("application/json");
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userid") != null) userId = (int)session.getAttribute("userid");
		if (userId == -1) {
			response.getWriter().write("{ \"redirect\" : true }");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			Connection connection = null;
			try {
				connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
				connection.setAutoCommit(false);
				StringBuilder json = new StringBuilder("{ \"contact\" : ");
				boolean isValid = Validation.validateContactInfoWithJSON(connection, request, json);
	        	json.append(", \"isvalid\" : ").append(isValid).append(" }");
	        	response.getWriter().write(json.toString());
	        	if (isValid) {
	        		int key;
	        		String id = request.getParameter("id");
	        		if (id != null && !id.isEmpty() && Integer.parseInt(id) > 0) {
	        			List<Integer> ids = new ArrayList<>();
	        			key = updateContact(connection, id, request);
	        	        if (key <= 0) throw new SQLException("Unable to update contact entry.");
	        	        for (int i = 0; request.getParameter("email" + i) != null && !request.getParameter("email" + i).isEmpty(); ++i) {
		    	        	int emailId = insertEmail(connection, Integer.parseInt(id), request.getParameter("email" + i), (i == 0 ? 1 : 5));
		    	        	if (emailId <= 0) throw new SQLException("Error occured while trying to insert new email.");
		    	        	if (!(request.getParameter("email" + i).isEmpty())) ids.add(emailId);
		    	        }
	        	        deleteEmails(connection, ids, Integer.parseInt(id));
	        	        ids.clear();
		    	        for (int i = 0; request.getParameter("phone" + i) != null && !request.getParameter("phone" + i).isEmpty(); ++i) {
		    	        	int phoneId = insertPhoneNumber(connection, Integer.parseInt(id), request.getParameter("phone" + i), (i == 0 ? 1 : 5));
		    	        	if (phoneId <= 0) throw new SQLException("Error occured while trying to insert new phone number.");
		    	        	if (!(request.getParameter("phone" + i).isEmpty())) ids.add(phoneId);
		    	        }
		    	        deletePhoneNumbers(connection, ids, Integer.parseInt(id));
	        		} else {
	        			// insert contact entry first and use its Primary Key for the following tables
		        		int contactId = insertContact(connection, request.getParameter("firstname"), 
		        				request.getParameter("lastname"), userId, Integer.parseInt(request.getParameter("usr-lnk")));    	        
		    	        if (contactId <= 0) throw new SQLException("Error occured while trying to insert new contact.");
		    	        for (int i = 0; request.getParameter("email" + i) != null && !request.getParameter("email" + i).isEmpty(); ++i) {
		    	        	int emailId = insertEmail(connection, contactId, request.getParameter("email" + i), (i == 0 ? 1 : 5));
		    	        	if (emailId <= 0) throw new SQLException("Error occured while trying to insert new email.");
		    	        }
		    	        for (int i = 0; request.getParameter("phone" + i) != null && !request.getParameter("phone" + i).isEmpty(); ++i) {
		    	        	int emailId = insertPhoneNumber(connection, contactId, request.getParameter("phone" + i), (i == 0 ? 1 : 5));
		    	        	if (emailId <= 0) throw new SQLException("Error occured while trying to insert new phone number.");
		    	        }
	        		}
	        	}
	        	connection.commit();
	        	response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				try {  
		    		if (connection != null) connection.rollback();
		    	} catch (SQLException ex) { ex.printStackTrace();/* TODO log error */ }
				// TODO log error
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} finally {
				if (connection != null)
					try { connection.close(); } catch (SQLException e) { e.printStackTrace();/* TODO log error */ }
			}
		}
	}
	/**
	 * Marks unused emails as deleted
	 * @param connection
	 * @param ids
	 * @return 1 if an email was marked
	 * @throws SQLException
	 */
	private int deleteEmails(Connection connection, List<Integer> ids, int contactId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement updateCtcStmt = null;
		try {
			String updateEmSQL = "UPDATE emails SET contact_type_id = 6 WHERE contact_id = ?";
			for (int i = 0; i < ids.size(); i++)
				updateEmSQL += " AND id <> ?";		
	    	updateCtcStmt = connection.prepareStatement(updateEmSQL, Statement.RETURN_GENERATED_KEYS);
	    	updateCtcStmt.setInt(1, contactId);
	    	for (int i = 0; i < ids.size(); i++)
	    		updateCtcStmt.setInt(i+2, ids.get(i)); // plus two for correct param index
	    	return updateCtcStmt.executeUpdate();
		} finally {
			if (updateCtcStmt != null) updateCtcStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Marks unused phone numbers as deleted
	 * @param connection
	 * @param ids
	 * @return 1 if an email was marked
	 * @throws SQLException
	 */
	private int deletePhoneNumbers(Connection connection, List<Integer> ids, int contactId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement updateCtcStmt = null;
		try {
			String updateEmSQL = "UPDATE phone_numbers SET contact_type_id = 6 WHERE contact_id = ?";
			for (int i = 0; i < ids.size(); i++)
				updateEmSQL += " AND id <> ?";			
	    	updateCtcStmt = connection.prepareStatement(updateEmSQL, Statement.RETURN_GENERATED_KEYS);
	    	updateCtcStmt.setInt(1, contactId);
	    	for (int i = 0; i < ids.size(); i++)
	    		updateCtcStmt.setInt(i+2, ids.get(i)); // plus two for correct param index
	    	return updateCtcStmt.executeUpdate();
		} finally {
			if (updateCtcStmt != null) updateCtcStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Updates contact name fields
	 * @param connection
	 * @param id
	 * @param request
	 * @return 1 for success, 0 on failure
	 * @throws SQLException
	 */
	private int updateContact(Connection connection, String id, HttpServletRequest request) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement updateCtcStmt = null;
		try {
			int userLink = Integer.parseInt(request.getParameter("usr-lnk"));
			final String updateCtcSQL = "UPDATE contacts SET firstname = ?,lastname = ?,user_link_id = ? WHERE id = ?";
	    	updateCtcStmt = connection.prepareStatement(updateCtcSQL, Statement.RETURN_GENERATED_KEYS);
	    	updateCtcStmt.setString(1, request.getParameter("firstname"));
	    	updateCtcStmt.setString(2, request.getParameter("lastname"));
	    	if (userLink > 0) updateCtcStmt.setInt(3, userLink);
	        else updateCtcStmt.setNull(3, java.sql.Types.INTEGER);
	    	updateCtcStmt.setInt(4, Integer.parseInt(id));
	    	return updateCtcStmt.executeUpdate();
		} finally {
			if (updateCtcStmt != null) updateCtcStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Insert new contact into the database
	 * @param connection
	 * @param firstName
	 * @param lastName
	 * @return the auto-generated index of the new insertion
	 * @throws SQLException
	 */
	private int insertContact(Connection connection, String firstName, String lastName, int userId, int userLinkId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertStmt = null;
		try {
			final String insertSQL = "INSERT INTO contacts (firstname,lastname,user_id,user_link_id) VALUES (?,?,?,?)";
	        insertStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
	        insertStmt.setString(1, firstName);
	        insertStmt.setString(2, lastName);
	        insertStmt.setInt(3, userId);
	        if (userLinkId > 0) insertStmt.setInt(4, userLinkId);
	        else insertStmt.setNull(4, java.sql.Types.INTEGER);
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
	 * Insert a new email entry into the database
	 * @param connection
	 * @param contactId
	 * @param email
	 * @return id of the email entry of interest
	 * @throws SQLException
	 */
	private int insertEmail(Connection connection, int contactId, String email, int contactType) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		try {
			// check if id exists
			final String selectSQL = "SELECT id,contact_type_id FROM emails WHERE email = ? AND contact_id = ?";
			stmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, email);
			stmt.setInt(2, contactId);
			resultSet = stmt.executeQuery();
        	if (resultSet.next()) {
        		int id = resultSet.getInt("id");
        		int contactTypeId = resultSet.getInt("contact_type_id");
        		if (contactTypeId == contactType) return id;
        		// manage previous resources
            	if (resultSet != null) resultSet.close();
    			if (stmt != null) stmt.close();
        		final String updateSQL = "UPDATE emails SET contact_type_id = ? WHERE id = ?";
        		stmt = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
        		stmt.setInt(1, contactType);
        		stmt.setInt(2, id);
        		stmt.executeUpdate();
    	    	return id;
        	}
        	// manage previous resources
        	if (resultSet != null) resultSet.close();
			if (stmt != null) stmt.close();
			final String insertSQL = "INSERT INTO emails (contact_id,email,is_primary,contact_type_id) VALUES (?,?,?,?)";
			stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, contactId);
			stmt.setString(2, email);
			stmt.setBoolean(3, false);
			stmt.setInt(4, contactType);
			stmt.execute();
	        resultSet = stmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (stmt != null) stmt.close();
		}
	}
	/**
	 * Insert a new phone number entry into the database
	 * @param connection
	 * @param contactId
	 * @param phoneNumber
	 * @return the auto-generated index for the new insertion
	 * @throws SQLException
	 */
	private int insertPhoneNumber(Connection connection, int contactId, String phoneNumber, int contactType) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement stmt = null;
		try {
			// check if id exists
			final String selectSQL = "SELECT id,contact_type_id FROM phone_numbers WHERE phone_number = ?";
			stmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, phoneNumber);
			resultSet = stmt.executeQuery();
        	if (resultSet.next()) {
        		int id = resultSet.getInt("id");
        		int contactTypeId = resultSet.getInt("contact_type_id");
        		if (contactTypeId == contactType) return id;
        		// manage previous resources
            	if (resultSet != null) resultSet.close();
    			if (stmt != null) stmt.close();
        		final String updateSQL = "UPDATE phone_numbers SET contact_type_id = ? WHERE id = ?";
        		stmt = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
        		stmt.setInt(1, id);
        		stmt.setInt(2, contactType);
    	    	return id;
        	}
        	// manage previous resources
        	if (resultSet != null) resultSet.close();
			if (stmt != null) stmt.close();
			final String insertSQL = "INSERT INTO phone_numbers (contact_id, phone_number, contact_type_id) VALUES (?,?,?)";
			stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, contactId);
			stmt.setString(2, phoneNumber);
			stmt.setInt(3, contactType);
			stmt.execute();
	        resultSet = stmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (stmt != null) stmt.close();
		}
	}
}
