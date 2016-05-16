/*
 * File name: GetEventsServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Olexander Agafonov
 * Integration Developer: Jeremy Chen
 * Date: 26/02/2015 (DD/MM/YYYY)
 * Purpose: Gets all public events to display to the user.
 */
package not_used;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import connections.DatabaseConnectionFactory;


//@WebServlet("/getevents")
public class GetPublicPartialEventsServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	public GetPublicPartialEventsServlet() { super(); }
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		response.setContentType("text/html");
		ResultSet resultSet = null;
		PreparedStatement selectStmt = null;
		PrintWriter out = response.getWriter();
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
		{
			final String selectSQL = "SELECT events.id AS id,name,location,start_date,event_documents.id AS DocId "
								   + "FROM events LEFT JOIN event_documents ON event_document_id = event_documents.id WHERE is_public = 1";
			selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			resultSet = selectStmt.executeQuery();
			while(resultSet.next()){
				int docId = resultSet.getInt("DocId");
				out.write(String.format("<div class=\"col-xs-12 col-sm-6 col-md-2 col-lg-2 thumbnail public-evt\" data-evt=\"%d\">", resultSet.getInt("id")));
				out.write(String.format("<a href=\"getimage?img=%d\" title=\"%s\">", docId, resultSet.getString("name")));
				out.write(String.format("<img class=\"evt-img\" src=\"getimage?img=%d\" />", docId));
				out.write("</a>");
				out.write(String.format("<div class\"caption\"><h5>%s</h5>", resultSet.getString("name")));
				out.write(String.format("<h5>%s</h5>", resultSet.getTimestamp("start_date").toString()));
				out.write("<div class=\"btn-toolbar\"><div class=\"btn-group btn-group-sm\">");
				out.write("<a href=\"#\" class=\"btn btn-primary\" role=\"button\"><i class=\"fa fa-eye\"></i> See this event</a>");
				out.write("<a href=\"#\" class=\"btn btn-success\" role=\"button\"><i class=\"fa fa-share\"></i> Share </a>");
				out.write("<button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">RSVP <span class=\"caret\"></span></button>");
				out.write("<ul class=\"dropdown-menu\"><li><a href=\"#\" data-sts=\"1\">Going</a></li><li><a href=\"#\" data-sts=\"2\">Interested</a></li><li><a href=\"#\" data-sts=\"3\">Not Going</a></li></ul>");
				out.write("</div></div></div></div></div>");
			}
			response.setStatus(HttpServletResponse.SC_OK);
		}
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			// TODO log exception
		} finally {
			if (out != null) out.close();
			if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* TODO log exception */ }
			if (selectStmt != null) try { selectStmt.close(); } catch (SQLException e) { /* TODO log exception */ }
		}
	}
}
