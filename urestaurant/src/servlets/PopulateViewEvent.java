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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.ant.SessionsTask;

import connections.DatabaseConnectionFactory;
import utilities.Validation;

@WebServlet("/PopulateViewEvent")
public class PopulateViewEvent extends HttpServlet{
	
	public PopulateViewEvent()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("application/json");
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		//HttpSession session = request.getSession(false);
		
		
	/*	if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}
	 */
		
		ResultSet resultSet = null;
		ResultSet resultSet2 = null;
		///ResultSet resultSet2 = null;
		PreparedStatement selectStmt = null;
		PreparedStatement selectStmt2 = null;
		
		//PreparedStatement selectStmt2 = null;
	    try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) 
	    {    	 
	    //	out = response.getWriter();
	    	
	    	String tempId = request.getParameter("id");
	    	int eventId = Integer.parseInt(tempId);
	    	
	    	final String selectSQL = "select * from events where id=?";
	    	
	    	//final String selectSQL2 = "select * from user_events where event_id=? AND user_id=?";
	    
	    	
			selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setInt(1, eventId);
			resultSet = selectStmt.executeQuery();
			
			
			/*
			selectStmt2 = connection.prepareStatement(selectSQL2, Statement.RETURN_GENERATED_KEYS);
			selectStmt2.setInt(1, eventId);
			selectStmt2.setInt(2, (Integer)session.getAttribute("userid"));
			resultSet2 = selectStmt.executeQuery();
	        */
			
			
			//StringBuilder json = new StringBuilder("{ \"event\" : [");
			while(resultSet.next()){
				HttpSession session = request.getSession(false);
				int userID = Integer.parseInt(session.getAttribute("userid").toString());
				
				final String selectSQL2 = "select * from user_events where user_id=? and event_id=?";
				selectStmt2 = connection.prepareStatement(selectSQL2, Statement.RETURN_GENERATED_KEYS);
				selectStmt2.setInt(1, userID);
				selectStmt2.setInt(2, eventId);
				resultSet2 = selectStmt2.executeQuery();
				boolean isPublic = resultSet.getBoolean("is_public");
				if(isPublic || resultSet2.next() && resultSet2.getInt("user_event_status_id") != 3)
				{
					
					String name = resultSet.getString("name");
					String description = resultSet.getString("description");
					String location = resultSet.getString("location");
					//String startDate = resultSet.getString("start_date");
					//String endDate = resultSet.getString("end_date");
					Timestamp startDateTime = resultSet.getTimestamp("start_date");
					Timestamp endDateTime = resultSet.getTimestamp("end_date");
					int capacity = resultSet.getInt("capacity");
					//boolean isPublic = resultSet.getBoolean("is_public");
					
					String startDate = new SimpleDateFormat("yyyy/MM/dd").format(startDateTime);
					String startTime = new SimpleDateFormat("HH:mm a").format(startDateTime);
					    
				    String endDate = new SimpleDateFormat("yyyy/MM/dd").format(endDateTime);
				    String endTime = new SimpleDateFormat("HH:mm a").format(endDateTime);
					int docId = resultSet.getInt("document_id");
				    
				    
					//HttpSession session = request.getSession(false);
				    session.setAttribute("id", eventId );
					request.setAttribute("name", name);
					request.setAttribute("description", description);
					request.setAttribute("location",location);
					request.setAttribute("startDate",startDate);
					request.setAttribute("startTime", startTime);
					request.setAttribute("endDate", endDate);
					request.setAttribute("endTime", endTime);
					request.setAttribute("capacity", capacity);
					request.setAttribute("isPublic", isPublic);
					request.setAttribute("docId", docId);
					RequestDispatcher rd=request.getRequestDispatcher("/viewevent.jsp");
					rd.forward(request,response);

						
					
					
				}
				else
				{
					RequestDispatcher rd = request.getRequestDispatcher("/dashboard.jsp");
					rd.forward(request, response);

				}
				
				
			}
			//json.append("] }");
			
			//response.getWriter().write(json.toString());
			
			resultSet.close();
	        selectStmt.close();
			
			
			
	    } 
	    catch (SQLException e) 
	    {
	    	// TODO log error
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} 
	}
	
}
