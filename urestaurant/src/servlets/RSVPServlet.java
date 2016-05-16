/*
 * File name: RSVPServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 26/02/2015 (DD/MM/YYYY)
 * Purpose: Updates event status for attendees.
 */
package servlets;

import java.io.IOException;
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

import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class RSVPServlet
 */
@WebServlet("/rsvp")
public class RSVPServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RSVPServlet() { super(); }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession(false);
		// check for userid in session to continue with transaction
		if (session != null && session.getAttribute("userid") != null && session.getAttribute("userid") != "") {
			int userId = (int)session.getAttribute("userid");
			int eventId = Integer.parseInt(request.getParameter("event"));
			Connection connection = null;
			try {
				int key = -1;
				connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
				connection.setAutoCommit(false);
				// check if user_event entry already exists, if so proceed to update user status 	        
				key = selectUserEvent(connection, userId, eventId);
    	        if (key > 0) {
    	        	key = updateUserEvent(connection, Integer.parseInt(request.getParameter("event_status")), userId, eventId);
        	        if (key <= 0) throw new SQLException("Unable to update user_event entry.");
        	        response.getWriter().write(formatJsonResponse(session, true));
    	        } else {
    	        	key = selectEvent(connection, eventId);
    	        	if (key <= 0) throw new SQLException("Unable to select event entry.");
    	        	key = insertNewUserEvent(connection, userId, eventId, Integer.parseInt(request.getParameter("event_status")));
	    	        if (key <= 0) throw new SQLException("Unable to insert into user_events table.");
	    	        response.getWriter().write(formatJsonResponse(session, true));
    	        }
    	        connection.commit();
    	        response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				// TODO log error
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				try { 
					if (connection != null) connection.rollback(); 
				} catch (SQLException ex) { 
					ex.printStackTrace();/* TODO log error */ 
				}
			} finally {
				if (connection != null)
					try { connection.close(); } catch (SQLException e) { e.printStackTrace();/* TODO log error */ }
			}
		} else { // redirect if userid does not exist in session
			response.getWriter().write("{ \"redirect\" : true }");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
	/**
	 * Selects an event using an event id.
	 * @param connection
	 * @param eventId
	 * @return The key of the event or -1 if it wasn't found.
	 * @throws SQLException
	 */
	private int selectEvent(Connection connection, int eventId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement selectEvtStmt = null;
		try {
			final String selectEvtSQL = "SELECT id FROM events WHERE is_public = 1 AND id = ?";
        	selectEvtStmt = connection.prepareStatement(selectEvtSQL, Statement.RETURN_GENERATED_KEYS);
        	selectEvtStmt.setInt(1, eventId);
        	resultSet = selectEvtStmt.executeQuery();
        	if (resultSet.next()) return resultSet.getInt("id");
        	else return -1;
		} finally {
			if (selectEvtStmt != null) selectEvtStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Selects a user_event entry for a specified user and event. Should be distinct.
	 * @param connection
	 * @param userId
	 * @param eventId
	 * @return the key of the user_event entry or -1 if not found.
	 * @throws SQLException
	 */
	private int selectUserEvent(Connection connection, int userId, int eventId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement selectUsrEvtStmt = null;
		try {
			final String selectUsrEvtSQL = "SELECT id FROM user_events WHERE user_id = ? AND event_id = ?";
			selectUsrEvtStmt = connection.prepareStatement(selectUsrEvtSQL, Statement.RETURN_GENERATED_KEYS);
			selectUsrEvtStmt.setInt(1, userId);
			selectUsrEvtStmt.setInt(2, eventId);
			resultSet = selectUsrEvtStmt.executeQuery();
	        if (resultSet.next()) return resultSet.getInt("id");
        	else return -1;
		} finally {
			if (selectUsrEvtStmt != null) selectUsrEvtStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Updates a user_event entry by changing the user_event_status
	 * @param connection
	 * @param statusId
	 * @param userId
	 * @param eventId
	 * @return the generated key from the executed query.
	 * @throws SQLException
	 */
	private int updateUserEvent(Connection connection, int statusId, int userId, int eventId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement updateUsrEvtStmt = null;
		try {
			final String updateUsrEvtSQL = "UPDATE user_events SET user_event_status_id = ? WHERE user_id = ? AND event_id = ?";
	    	updateUsrEvtStmt = connection.prepareStatement(updateUsrEvtSQL, Statement.RETURN_GENERATED_KEYS);
	    	updateUsrEvtStmt.setInt(1, statusId);
	    	updateUsrEvtStmt.setInt(2, userId);
	    	updateUsrEvtStmt.setInt(3, eventId);
	    	return updateUsrEvtStmt.executeUpdate();
		} finally {
			if (updateUsrEvtStmt != null) updateUsrEvtStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	/**
	 * Inserts a new entry into the user_events table with the user defined user_event_status
	 * @param connection
	 * @param userId
	 * @param eventId
	 * @param statusId
	 * @return the generated key from the executed query.
	 * @throws SQLException
	 */
	private int insertNewUserEvent(Connection connection, int userId, int eventId, int statusId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertUsrEvtStmt = null;
		try {
			final String insertUsrEvtSQL = "INSERT INTO user_events (user_id, event_id, user_event_type_id, user_event_status_id) VALUES (?,?,?,?)";
	    	insertUsrEvtStmt = connection.prepareStatement(insertUsrEvtSQL, Statement.RETURN_GENERATED_KEYS);
	    	insertUsrEvtStmt.setInt(1, userId);
	    	insertUsrEvtStmt.setInt(2, eventId);
	    	insertUsrEvtStmt.setInt(3, 2); // type 2 is "attendee"
	    	insertUsrEvtStmt.setInt(4, statusId);
	        insertUsrEvtStmt.execute();
	        resultSet = insertUsrEvtStmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
        	else return -1;
		} finally {
			if (insertUsrEvtStmt != null) insertUsrEvtStmt.close();
			if (resultSet != null) resultSet.close();
		}		
	}
	/**
	 * Formats the JSON response String
	 * @param session
	 * @param isAuthorized
	 * @return JSON string to send back in HttpServletResponse
	 */
	private String formatJsonResponse(HttpSession session, boolean isAuthorized) {
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		// check if the user locale is actually fr_CA and change if necessary
		if (session != null && session.getAttribute("locale") != null && session.getAttribute("locale") != "" && "fr_CA".equals(session.getAttribute("locale")))
			bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		return String.format("{ \"event_status\" : %b , \"message\" : \"%s\" }", 
				isAuthorized, isAuthorized ? bundle.getString("rsvpSuccessMessage") : bundle.getString("rsvpUnauthMessage"));
	}
}
