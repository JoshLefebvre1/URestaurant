/*
 * File name: EditEventServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Josh Lefebvre
 * Date: 24/03/2016 (DD/MM/YYYY)
 * Purpose: Edits events in the DB.
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

/**
 * Servlet implementation class EditEventServlet
 */
@WebServlet("/EditEventServlet")
@MultipartConfig(maxFileSize = 16177215) // upload file's size up to 16MB
public class EditEventServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditEventServlet() 
    {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		int userId = -1;
		response.setContentType("application/json"); 
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userid") != null) 
			userId = (int)session.getAttribute("userid");
		if (userId == -1) 
		{
			response.getWriter().write("{ \"redirect\" : true }");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} 
		else 
		{
			PreparedStatement updateEvtStmt = null;
			ResultSet resultSet = null;
			Connection connection=null;
			try {
				connection = DatabaseConnectionFactory.getConnectionFactory().getConnection();
				connection.setAutoCommit(false);
				
				Event event = new Event();
				event.getJsonValidation().append("{ \"event\" : ");
	        	boolean isValid = Validation.validateEventWithJSON(connection, request, event);
	        	event.getJsonValidation().append(", \"isvalid\" : ").append(isValid).append(" }");
	        	response.getWriter().write(event.getJsonValidation().toString());

	        	if (isValid) 
	        	{   
	        		//Get Current Event ID
	        		String stringEventID= request.getSession(false).getAttribute("id").toString();
	    	        int eventID= Integer.parseInt(stringEventID);
	    	        
	    	        //Get Document_ID
	    	        int docID=-1;
	    	        final String selectDocSQL = "select * from events where id=?";
	    			PreparedStatement selectDocStmt = connection.prepareStatement(selectDocSQL, Statement.RETURN_GENERATED_KEYS);
	    			selectDocStmt.setInt(1, eventID);
	    			resultSet = selectDocStmt.executeQuery();
	    			if(resultSet.next())
	    			{
	    				docID =resultSet.getInt("document_id");
	    				if(docID!=0)//If replacing an old image
	    				{
	    					editEventDocument(connection, request.getPart("graphic"), docID);
	    				}
	    				else//If no previous photo exits
	    				{
	    					docID=insertEventDocument(connection, request.getPart("graphic"));
	    				}
	    			
		    			//Edit Event
		            	final String updateSQL = "update events SET name=?, description=?, location=?, start_date=?, end_date=?, is_public=?, document_id=?  where id=?";
		            	updateEvtStmt = connection.prepareStatement(updateSQL, Statement.RETURN_GENERATED_KEYS);
		    	        
		    	        updateEvtStmt.setString(1, event.getName());
		    	        updateEvtStmt.setString(2, event.getDescription());
		    	        updateEvtStmt.setString(3, event.getLocation());
		    	        updateEvtStmt.setTimestamp(4, event.getStartDateTime());
		    	        updateEvtStmt.setTimestamp(5, event.getEndDateTime());
		    	        updateEvtStmt.setBoolean(6, event.isPublic());
		    	        updateEvtStmt.setInt(7, docID);
		    	        updateEvtStmt.setInt(8,eventID);
		    	        updateEvtStmt.execute();
		    			
		    	        session.removeAttribute("id");
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
				if (updateEvtStmt != null)
					try {updateEvtStmt.close();} catch (SQLException e1) {e1.printStackTrace();}
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
	private int editEventDocument(Connection connection, Part filePart, int document_id) throws SQLException, IOException 
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
	private int insertEventDocument(Connection connection, Part filePart) throws SQLException, IOException 
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
}






