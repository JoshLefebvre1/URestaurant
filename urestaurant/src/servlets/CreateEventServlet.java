/*
 * File name: CreateEventServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Liam McGovern
 * Date: 26/02/2015 (DD/MM/YYYY)
 * Purpose: Creates new events in the DB.
 * Event Documents and Integration Developer: Christopher Elliott
 */
package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import connections.DatabaseConnectionFactory;
import utilities.Event;
import utilities.Validation;

@WebServlet(urlPatterns = "/createvent")
@MultipartConfig(maxFileSize = 16177215) // upload file's size up to 16MB
public class CreateEventServlet extends HttpServlet{
	private static final long serialVersionUID = 6134625179825673721L;
	
	public CreateEventServlet () { super(); } 
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
				Event event = new Event();
				event.getJsonValidation().append("{ \"event\" : ");
	        	boolean isValid = Validation.validateEventWithJSON(connection, request, event);
	        	event.getJsonValidation().append(", \"isvalid\" : ").append(isValid).append(" }");
	        	response.getWriter().write(event.getJsonValidation().toString());
	        	if (isValid) {
	        		// insert event document first and use its PKey
	        		int eventDocId = insertEventDocument(connection, request.getPart("graphic"));
	    	        int eventId = insertEvent(connection, event, eventDocId);	    	        
	    	        if (eventId > 0) {
	    	        	// insert user_event entry
	    	        	int row = insertUserEvent(connection, userId, eventId);
	    	        	if (row == 0) {
		                    throw new SQLException("Error occured trying to save user event entry.");
		                }
	    	        	// commit sql transaction
	    	        	connection.commit();
	    	        }
	        	}
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
	 * Inserts a new event into the database.
	 * @param connection
	 * @param event
	 * @param eventDocId
	 * @return The generated key for the event. The key is used to insert into the user_events table right after.
	 * @throws SQLException
	 */
	private int insertEvent(Connection connection, Event event, int eventDocId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement insertEvtStmt = null;
		try {
			final String insertSQL = "INSERT INTO events (name, description, location, start_date, end_date, is_public, document_id) VALUES (?,?,?,?,?,?,?)";
	        insertEvtStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
	        insertEvtStmt.setString(1, event.getName());
	        insertEvtStmt.setString(2, event.getDescription());
	        insertEvtStmt.setString(3, event.getLocation());
	        insertEvtStmt.setTimestamp(4, event.getStartDateTime());
	        insertEvtStmt.setTimestamp(5, event.getEndDateTime());
	        insertEvtStmt.setBoolean(6, event.isPublic());
	        if (eventDocId > 0) insertEvtStmt.setInt(7, eventDocId);
	        else insertEvtStmt.setNull(7, java.sql.Types.INTEGER);
	        insertEvtStmt.execute();
	        resultSet = insertEvtStmt.getGeneratedKeys();
	        if (resultSet.next()) return resultSet.getInt(1);
	        else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (insertEvtStmt != null) insertEvtStmt.close();
		}
	}
	/**
	 * Inserts the user event entry for the user who created the new event. (The Organizer is established in this way.)
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
	    	insertUsrEvtStmt.setInt(3, 1); // id=1 for organizer
	    	insertUsrEvtStmt.setInt(4, 1); // id=1 says this user is going to the event (automatically because user is the organizer)
	        return insertUsrEvtStmt.executeUpdate();
		} finally {
			if (insertUsrEvtStmt != null) insertUsrEvtStmt.close();
		}
	}
	/**
	 * Inserts an event document entry. (Graphics)
	 * @param connection
	 * @param filePart
	 * @return The ResultSet that contains the newly inserted key.
	 * @throws SQLException
	 * @throws IOException
	 */
	private int insertEventDocument(Connection connection, Part filePart) throws SQLException, IOException {
		ResultSet resultSet = null;
		InputStream inputStream = null;
		PreparedStatement insertEvtDocStmt = null;
		String contentType = filePart.getContentType();
		if (filePart.getSize() == 0) return -1;
		if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg") && !contentType.equals("image/gif") && !contentType.equals("image/bmp"))
			return -1;
		try {
			final String insertEvtDocSQL = "INSERT INTO documents (document,content_type) VALUES (?,?)";
			// prints out some information for debugging
//	        System.out.println(filePart.getName());
//	        System.out.println(filePart.getSize());
//	        System.out.println(filePart.getContentType());
	    	inputStream = filePart.getInputStream();
	        insertEvtDocStmt = connection.prepareStatement(insertEvtDocSQL, Statement.RETURN_GENERATED_KEYS);
	        // fetches input stream of the upload file for the blob column
	    	insertEvtDocStmt.setBlob(1, inputStream);
	    	insertEvtDocStmt.setString(2, filePart.getContentType());
	    	insertEvtDocStmt.execute();
	    	resultSet = insertEvtDocStmt.getGeneratedKeys();
	    	if (resultSet.next()) return resultSet.getInt(1);
	    	else return -1;
		} finally {
			if (resultSet != null) resultSet.close();
			if (inputStream != null) inputStream.close();
			if (insertEvtDocStmt != null) insertEvtDocStmt.close();
		}
	}
}
