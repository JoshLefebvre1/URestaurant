/*
 * File name: EditEventServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Josh Lefebvre
 * Date: 31/03/2016 (DD/MM/YYYY)
 * Purpose: Edits user profile in the DB.
 */
package servlets;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

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

/**
 * Servlet implementation class EditEventServlet
 */
@WebServlet("/EditProfileServlet")
@MultipartConfig(maxFileSize = 16177215) // upload file's size up to 16MB
public class EditProfileServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditProfileServlet() 
    {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int userId = -1;
		int contactID=-1;
		response.setContentType("application/json"); 
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userid") != null) 
			userId = (int)session.getAttribute("userid");//Get user ID from session attribute
		if (session != null && session.getAttribute("contactid") != null) 
			contactID = (int)session.getAttribute("contactid");//Get user ID from session attribute
		if (userId == -1) 
		{
			response.getWriter().write("{ \"redirect\" : true }");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} 
		else 
		{
			//Prepared Statements
			PreparedStatement updateUserStmt = null;
			PreparedStatement updateUserStmt2 = null;
			PreparedStatement updateUserStmt3 = null;
			PreparedStatement selectPhotoStmt = null;
			PreparedStatement selectPhoneStmt = null;
			//ResultSets
			ResultSet resultSet = null;
			ResultSet resultSet2;
			//Connections
			Connection connection=null;
			try 
			{
				connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
				connection.setAutoCommit(false);
				
				//Check validation
				StringBuilder json = new StringBuilder("{ \"contact\" : ");
				boolean isValid = Validation.validateUserInfoWithJSON(connection, request, json);
	        	json.append(", \"isvalid\" : ").append(isValid).append(" }");
	        	response.getWriter().write(json.toString());
				
	        	if (isValid) 
	        	{       
	    	        //Get current contact info
	    	        int photoID=-1;
	    	        final String selectPhotoSQL = "select * from contacts where id=?";
	    			selectPhotoStmt = connection.prepareStatement(selectPhotoSQL, Statement.RETURN_GENERATED_KEYS);
	    			selectPhotoStmt.setInt(1, contactID);
	    			resultSet = selectPhotoStmt.executeQuery();
	    			if(resultSet.next())
	    			{
	    				//Edit Photo Info
	    				photoID =resultSet.getInt("photo_id");
	    				if(photoID!=0)//If user already has a user profile picture set
	    				{
	    					updatePhoto(connection, request.getPart("graphic"), photoID);
	    				}
	    				else//If user does not have a user profile picture set
	    				{
	    					photoID=insertPhoto(connection, request.getPart("graphic"));
	    				}
	    			
		    			//Edit Contact Info
		            	final String updateSQL = "update contacts SET firstname=?, lastname=?, photo_id=? where id=?";
		            	updateUserStmt = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
		    	        updateUserStmt.setString(1, request.getParameter("firstname"));
		    	        updateUserStmt.setString(2, request.getParameter("lastname"));
		    	        if(photoID==-1)
		    	        	updateUserStmt.setNull(3, java.sql.Types.INTEGER);
		    	        else
		    	        	updateUserStmt.setInt(3, photoID);
		    	        updateUserStmt.setInt(4, contactID);
		    	        updateUserStmt.execute();
		    	        
		    	        //Edit Email Info
		            	final String updateSQL2 = "update emails SET email=? where contact_id=? AND contact_type_id=1";
		            	updateUserStmt2 = connection.prepareStatement(updateSQL2, Statement.RETURN_GENERATED_KEYS);
		    	        updateUserStmt2.setString(1, request.getParameter("email"));
		    	        updateUserStmt2.setInt(2, contactID);
		    	        updateUserStmt2.execute();
		    	        
		    	        //Edit Phone Info
		    	        String phone = request.getParameter("phone");
		    			if(phone != null && !phone.isEmpty())//If the user wants to attach a phone number to their profile
		    			{
		    				//Check if contact already has a phone number saved
		    				final String selectPhoneSQL = "SELECT * FROM phone_numbers WHERE contact_id = ? AND contact_type_id=1";
		    				selectPhoneStmt = connection.prepareStatement(selectPhoneSQL, Statement.RETURN_GENERATED_KEYS);
		    				selectPhoneStmt.setInt(1, contactID);
		    				resultSet2 = selectPhoneStmt.executeQuery();
		    				int phoneID=-1;
		    				while(resultSet2.next())
		    				{
		    					phoneID= resultSet2.getInt("id");
		    				
		    				}
		    				if(phoneID!=-1)
		    				{
		    					int updatePhone=updatePhoneNumber(connection, resultSet.getInt("id"), phone, phoneID);
		    					if (updatePhone <= 0) 
		    						throw new SQLException("Error occured inserting a new contact.");
		    				}
		    				else
		    				{
		    					int insertPhone=insertPhoneNumber(connection, phone, resultSet.getInt("id"));
		    					if (insertPhone <= 0) 
		    						throw new SQLException("Error occured inserting a new contact.");
		    				}

		    			}

	    	        	connection.commit();
	    			}
	        	}
	        	response.setStatus(HttpServletResponse.SC_OK);
			} 
			catch (SQLException e) 
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} 
			finally 
			{
				//Close Statements
				if (updateUserStmt != null)
					try {updateUserStmt.close();} catch (SQLException e1) {e1.printStackTrace();}
				if (updateUserStmt2 != null)
					try {updateUserStmt2.close();} catch (SQLException e1) {e1.printStackTrace();}
				if (updateUserStmt3 != null)
					try {updateUserStmt3.close();} catch (SQLException e1) {e1.printStackTrace();}
				if (selectPhotoStmt != null)
					try {selectPhotoStmt.close();} catch (SQLException e1) {e1.printStackTrace();}
				if (selectPhoneStmt != null)
					try {selectPhoneStmt.close();} catch (SQLException e1) {e1.printStackTrace();}
				//Close ResultSets
				if (resultSet != null)
					try {resultSet.close();} catch (SQLException e1) {e1.printStackTrace();}
				if (resultSet != null)
					try {resultSet.close();} catch (SQLException e1) {e1.printStackTrace();}
				//Close Connections
				if (connection != null)
					try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
			}
		}
	}
	
	/**
	 * Edits an event document entry. (Graphics)
	 * @param connection
	 * @param filePart
	 * @param document_id
	 * @return The ResultSet that contains the newly inserted key.
	 * @throws SQLException
	 * @throws IOException
	 */
	private int updatePhoto(Connection connection, Part filePart, int document_id) throws SQLException, IOException 
	{
		ResultSet resultSet = null;
		InputStream inputStream = null;
		PreparedStatement updateEvtDocStmt = null;
		String contentType = filePart.getContentType();
		if (filePart.getSize() == 0) 
			return -1;
		if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg") && !contentType.equals("image/gif") && !contentType.equals("image/bmp"))
			return -1;
		try 
		{
			final String updateEvtDocSQL = "UPDATE documents SET document=?, content_type=? where id=?";
	    	inputStream = filePart.getInputStream();
	        updateEvtDocStmt = connection.prepareStatement(updateEvtDocSQL, Statement.RETURN_GENERATED_KEYS);
	        // fetches input stream of the upload file for the blob column
	    	updateEvtDocStmt.setBlob(1, inputStream);
	    	updateEvtDocStmt.setString(2, filePart.getContentType());
	    	updateEvtDocStmt.setInt(3, document_id);
	    	updateEvtDocStmt.execute();
	    	resultSet = updateEvtDocStmt.getGeneratedKeys();
	    	if (resultSet.next()) 
	    		return resultSet.getInt(1);
	    	else 
	    		return -1;
		} 
		finally 
		{
			if (resultSet != null) resultSet.close();
			if (inputStream != null) inputStream.close();
			if (updateEvtDocStmt != null) updateEvtDocStmt.close();
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
	private int insertPhoto(Connection connection, Part filePart) throws SQLException, IOException 
	{
		ResultSet resultSet = null;
		InputStream inputStream = null;
		PreparedStatement insertEvtDocStmt = null;
		String contentType = filePart.getContentType();
		if (filePart.getSize() == 0) 
			return -1;
		if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg") && !contentType.equals("image/gif") && !contentType.equals("image/bmp"))
			return -1;
		try 
		{
			final String insertEvtDocSQL = "INSERT INTO documents (document,content_type) VALUES (?,?)";
	    	inputStream = filePart.getInputStream();
	        insertEvtDocStmt = connection.prepareStatement(insertEvtDocSQL, Statement.RETURN_GENERATED_KEYS);
	        // fetches input stream of the upload file for the blob column
	    	insertEvtDocStmt.setBlob(1, inputStream);
	    	insertEvtDocStmt.setString(2, filePart.getContentType());
	    	insertEvtDocStmt.execute();
	    	resultSet = insertEvtDocStmt.getGeneratedKeys();
	    	if (resultSet.next()) return resultSet.getInt(1);
	    	else return -1;
		} 
		finally 
		{
			if (resultSet != null) resultSet.close();
			if (inputStream != null) inputStream.close();
			if (insertEvtDocStmt != null) insertEvtDocStmt.close();
		}
	}
	
	/**
	 * Updates users phone number
	 * @param connection
	 * @param userId
	 * @param phoneNumber
	 * @return 1 on success
	 * @throws SQLException
	 */
	private int updatePhoneNumber(Connection connection, int userId, String phoneNumber, int phoneID) throws SQLException 
	{
		ResultSet resultSet = null;
		PreparedStatement updatePhoneStmt = null;
		try 
		{
			final String updateSQL = "update phone_numbers SET phone_number=? where contact_id=?";
			updatePhoneStmt = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
			updatePhoneStmt.setString(1, phoneNumber);
			updatePhoneStmt.setInt(2, userId);
			updatePhoneStmt.execute();
	        resultSet = updatePhoneStmt.getGeneratedKeys();
	        return 1;
		} 
		finally 
		{
			if (resultSet != null) 
				resultSet.close();
			if (updatePhoneStmt != null) 
				updatePhoneStmt.close();
		}
	}
	
	/**
	 * Inserts a user phoneNumber
	 * @param connection
	 * @param phoneNumber
	 * @param userId
	 * @return The ResultSet that contains the newly inserted key.
	 * @throws SQLException
	 */
	private int insertPhoneNumber(Connection connection, String phoneNumber, int userId) throws SQLException 
	{
		ResultSet resultSet = null;
		PreparedStatement insertPhoneStmt = null;
		try 
		{
			final String insertSQL = "INSERT INTO phone_numbers (phone_number,contact_id) VALUES (?,?)";
			insertPhoneStmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
			insertPhoneStmt.setString(1, phoneNumber);
			insertPhoneStmt.setInt(2, userId);
			insertPhoneStmt.execute();
	        resultSet = insertPhoneStmt.getGeneratedKeys();
	        if (resultSet.next()) 
	        	return resultSet.getInt(1);
	        else 
	        	return -1;
		} 
		finally 
		{
			if (resultSet != null) 
				resultSet.close();
			if (insertPhoneStmt != null) 
				insertPhoneStmt.close();
		}
	}
	
	
	

}
