package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import utilities.Contact;
import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class SendInvitationsServlet
 */
@WebServlet("/sendinvites")
public class SendInvitationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendInvitationsServlet() { super(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

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
			// Get system properties
		    Properties properties = System.getProperties();
		    // Setup mail server
		    properties.put("mail.smtp.auth", "true");
		    properties.put("mail.smtp.starttls.enable", "true");
		    properties.put("mail.smtp.host", "mail.smtp2go.com");
		    properties.put("mail.smtp.port", "2525"); // 8025, 587 and 25 can also be used.
		    Session mailSession = Session.getInstance(properties,
	  		    new javax.mail.Authenticator() {
		  			protected PasswordAuthentication getPasswordAuthentication() {
		  				return new PasswordAuthentication("elli", "123password456");
		  			}
	  		    });
			String[] contactIds = request.getParameterValues("contacts[]");
			int eventId = Integer.parseInt(request.getParameter("event"));
			try {
				String senderEmail = null;
				connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
				connection.setAutoCommit(false);
				// Create a default MimeMessage object.
		        MimeMessage message = new MimeMessage(mailSession);
		        // Set From: header field of the header.
		        senderEmail = selectContactEmail(connection, userId);
		        if (senderEmail == null) throw new SQLException("Sender email is null.");
		        message.setFrom(new InternetAddress(senderEmail));
		        // Set Subject: header field
		        message.setSubject("You've been invited!"); // TODO add internationalization
		        Multipart mp = new MimeMultipart();
		        MimeBodyPart htmlmessage = new MimeBodyPart();
		        String link = "<form action=\"index.jsp\" method=\"post\">"
							+ "<input type=\"hidden\" name=\"token\" value=\"" + eventId + "\" />"
							+ "<button type=\"submit\">Go to event</button></form>";
				htmlmessage.setText(link, "UTF-8", "html");
				mp.addBodyPart(htmlmessage);
				message.setContent(mp); 
				for (String id : contactIds) {
					Contact contact = getContact(connection, Integer.parseInt(id));
					// create user event entry if user exists
					if (contact.getUserLinkId() > 0) {
			        	int userEventId = selectUserEvent(connection, contact.getUserLinkId(), eventId);
			        	if (userEventId == 0) insertNewUserEvent(connection, contact.getUserLinkId(), eventId, 4); // status id 4 == invited
			        }
			        // Set To: header field of the header.
			        message.addRecipient(Message.RecipientType.TO, new InternetAddress(contact.getPrimaryEmail()));
				}
				// Send message
		        Transport.send(message);
		        connection.commit();
		        response.getWriter().write("{ \"message-sent\" : true }");
	        	response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				try {  
		    		if (connection != null) connection.rollback();
		    	} catch (SQLException ex) { ex.printStackTrace();/* TODO log error */ }
				e.printStackTrace(); // TODO log error
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (AddressException e) {
				try {  
		    		if (connection != null) connection.rollback();
		    	} catch (SQLException ex) { ex.printStackTrace();/* TODO log error */ }
				e.printStackTrace(); // TODO log error
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (MessagingException e) {
				try {  
		    		if (connection != null) connection.rollback();
		    	} catch (SQLException ex) { ex.printStackTrace();/* TODO log error */ }
				e.printStackTrace(); // TODO log error
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} finally {
				if (connection != null)
					try { connection.close(); } catch (SQLException e) { e.printStackTrace();/* TODO log error */ }
			}
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
	private String selectContactEmail(Connection connection, int userId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement selectEmailStmt = null;
		try {
			final String selectUsrEvtSQL = "SELECT email FROM emails "
										 + "INNER JOIN contacts ON emails.contact_id = contacts.id "
										 + "INNER JOIN users ON users.contact_id = contacts.id "
										 + "WHERE user_id = ? AND contact_type_id = 1";
			selectEmailStmt = connection.prepareStatement(selectUsrEvtSQL, Statement.RETURN_GENERATED_KEYS);
			selectEmailStmt.setInt(1, userId);
			resultSet = selectEmailStmt.executeQuery();
	        if (resultSet.next()) return resultSet.getString("email");
        	else return null;
		} finally {
			if (selectEmailStmt != null) selectEmailStmt.close();
			if (resultSet != null) resultSet.close();
		}
	}
	

	private Contact getContact(Connection connection, int contactId) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement selectEvtStmt = null;
		try {
			final String selectEvtSQL =   "SELECT * FROM contacts "
										+ "INNER JOIN emails ON contact_id = contacts.id "
										+ "WHERE contact_id = ? AND contact_type_id = 1";
        	selectEvtStmt = connection.prepareStatement(selectEvtSQL, Statement.RETURN_GENERATED_KEYS);
        	selectEvtStmt.setInt(1, contactId);
        	resultSet = selectEvtStmt.executeQuery();
        	if (resultSet.next()) {
        		Contact contact = new Contact();
        		contact.setFirstName(resultSet.getString("firstname"));
        		contact.setLastName(resultSet.getString("lastname"));
        		contact.setPrimaryEmail(resultSet.getString("email"));
        		contact.setUserLinkId(resultSet.getInt("user_link_id"));
        		return contact;
        	} else return null;
		} finally {
			if (selectEvtStmt != null) selectEvtStmt.close();
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
}
