/*
 * File name: GetEventsServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Olexander Agafonov
 * Integration Developer: Jeremy Chen
 * Date: 26/02/2015 (DD/MM/YYYY)
 * Purpose: Gets all public events to display to the user.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/getevents")
public class GetEventsServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	public GetEventsServlet() { super(); }
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.setContentType("text/html");
		ResultSet resultSet = null;
		PreparedStatement selectStmt = null;
		PrintWriter out = response.getWriter();
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null) {
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
		}
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
		{
			int userId = (int)session.getAttribute("userid");
			//final String selectSQL = "SELECT events.id AS id,name,location,start_date,documents.id AS DocId "
			//					   + "FROM events LEFT JOIN documents ON document_id = documents.id WHERE is_public = 1";
			final String selectSQL = "SELECT DISTINCT events.id AS id,name,location,start_date,documents.id AS DocId FROM events "
									+"LEFT JOIN documents ON document_id = documents.id "
									+"INNER JOIN user_events ON event_id = events.id "
									+"WHERE (is_public = 1 AND user_id = ? AND user_event_type_id <> 1) OR (user_id <> ? AND user_event_type_id = 1 AND is_public = 1);";
			selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setInt(1, userId);
			selectStmt.setInt(2, userId);
			resultSet = selectStmt.executeQuery();
			while(resultSet.next()){
				int docId = resultSet.getInt("DocId");
				out.write(String.format("<div class=\"col-xs-12 col-sm-6 col-md-3 col-lg-3 thumbnail public-evt\" data-evt=\"%d\">", resultSet.getInt("id")));
				out.write(String.format("<a href=\"/urestaurant/getimage?img=%d\" title=\"%s\">", docId, resultSet.getString("name")));
				out.write(String.format("<img class=\"evt-img\" src=\"/urestaurant/getimage?img=%d\" />", docId));
				out.write("</a>");
				out.write(String.format("<div class\"caption\"><h5>%s</h5>", resultSet.getString("name")));
				out.write(String.format("<h5>%s</h5>", resultSet.getTimestamp("start_date").toString()));
				out.write("<div class=\"btn-toolbar\"><div class=\"btn-group btn-group-xs\">");
				out.write(String.format("<a href=\"PopulateViewEvent?id="+resultSet.getInt("id")+"\" class=\"btn btn-primary\" role=\"button\"><i class=\"fa fa-eye\"></i> %s </a>", 
						bundle.getString("seeEvent")));
//				out.write(String.format("<a href=\"#\" class=\"btn btn-success\" role=\"button\"><i class=\"fa fa-share\"></i> %s </a></div>", 
//						bundle.getString("invite")));
				out.write("<button type=\"button\" class=\"btn btn-info btn-xs dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\" style=\"float: right;\"> RSVP <span class=\"caret\"></span></button>");
				out.write(String.format("<ul class=\"dropdown-menu\"><li><a class=\"status\" href=\"#\" data-sts=\"1\">%s</a></li><li><a class=\"status\" href=\"#\" data-sts=\"2\">%s</a></li><li><a class=\"status\" href=\"#\" data-sts=\"3\">%s</a></li></ul>", 
						bundle.getString("going"), bundle.getString("interested"), bundle.getString("notGoing")));
				out.write("</div></div></div></div>");
			}
			response.setStatus(HttpServletResponse.SC_OK);
		}
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // TODO log exception
		} finally {
			if (out != null) out.close();
			if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* TODO log exception */ }
			if (selectStmt != null) try { selectStmt.close(); } catch (SQLException e) { /* TODO log exception */ }
		}
	}
}
