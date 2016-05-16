/*
 * File name: PopulateEditEventForms.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Josh Lefebvre
 * Date: 24/03/2016 (DD/MM/YYYY)
 * Purpose: Populates forms for editing an event.
 */

package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class PopulateEditEventForms
 */
@WebServlet("/PopulateEditEventForms")
public class PopulateEditEventForms extends HttpServlet
{
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PopulateEditEventForms() { super(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("application/json");
		
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
		{
			ResultSet resultSet;
			String param1 = request.getParameter("term");
			
			//Select the event the user wishes to edit
			int EventID = Integer.parseInt(param1);
			final String selectSQL = "select * from events where id=?";
			PreparedStatement selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setInt(1, EventID);
			resultSet = selectStmt.executeQuery();
			
			//Set request attributes based on the currently stored event attributes the user wishes to edit
			while(resultSet.next())
			{
				int id =resultSet.getInt("id");
				String name = resultSet.getString("name");
				String description=resultSet.getString("description");
				String location = resultSet.getString("location");
				Timestamp startDateTime = resultSet.getTimestamp("start_date");
				Timestamp endDateTime = resultSet.getTimestamp("end_date");
				int capacity = resultSet.getInt("capacity");
				boolean isPublic = resultSet.getBoolean("is_public");
				
				
			    String startDate = new SimpleDateFormat("yyyy/MM/dd").format(startDateTime);
			    String startTime = new SimpleDateFormat("HH:mm a").format(startDateTime);
			    
			    String endDate = new SimpleDateFormat("yyyy/MM/dd").format(endDateTime);
			    String endTime = new SimpleDateFormat("HH:mm a").format(endDateTime);
			    
			    HttpSession session = request.getSession(false);
			    session.setAttribute("id", id );
				request.setAttribute("name", name);
				request.setAttribute("description", description);
				request.setAttribute("location",location);
				request.setAttribute("startDate",startDate);
				request.setAttribute("startTime", startTime);
				request.setAttribute("endDate", endDate);
				request.setAttribute("endTime", endTime);
				request.setAttribute("capacity", capacity);
				request.setAttribute("isPublic", isPublic);
				
				//Determine userID of event creator
				int userID = determineUserID(connection, id);
				request.setAttribute("userID", userID);
			}

			resultSet.close();
	        selectStmt.close();
	        RequestDispatcher rd=request.getRequestDispatcher("/editevent.jsp");//Redirects to editevent.jsp page
			rd.forward(request,response);
	        
		}
		catch (SQLException e) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		e.printStackTrace();
		}
	}
	
	/**
	 * Determines the userID of the person who created the event (used to ensure users are unable to
	 * edit other users events
	 * @param connection
	 * @param id
	 * @return The user id
	 */
	private int determineUserID(Connection connection, int id) throws SQLException 
	{
		PreparedStatement Stmt = null;
		try 
		{
			int result=0;
			final String StmtSQL = "select * from user_events where event_id=? AND user_event_type_id=1";
	    	Stmt = connection.prepareStatement(StmtSQL,Statement.RETURN_GENERATED_KEYS);
	    	Stmt.setInt(1, id);
	    	ResultSet resultSet = Stmt.executeQuery();
	    	while(resultSet.next())
			{
	    		result=resultSet.getInt("user_id");
			}
	    	return result;
		}
		finally 
		{
			if (Stmt != null) 
				Stmt.close();
		}
	}



}
