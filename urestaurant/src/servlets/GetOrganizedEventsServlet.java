/*
 * File name: GetOrganizedEventsServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 6/03/2015 (DD/MM/YYYY)
 * Purpose: Gets all user hosted events to display.
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

/**
 * Servlet implementation class GetOrganizedEventsServlet
 */
@WebServlet("/gethostedevents")
public class GetOrganizedEventsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetOrganizedEventsServlet() { super(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		// check for userid in session to continue with transaction
		if (session != null && session.getAttribute("userid") != null && session.getAttribute("userid") != "") {
			ResourceBundle bundle;
			int userId = (int)session.getAttribute("userid");

			ResultSet resultSet = null;
			PreparedStatement getOrganizedEvtsStmt = null;
			PrintWriter out = response.getWriter();
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
			else bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
			try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) {
				final String sqlStmt = "SELECT DISTINCT events.id AS id,name,location,start_date,documents.id AS DocId FROM events "
									 + "INNER JOIN user_events ON event_id = events.id "
									 + "LEFT JOIN documents ON document_id = documents.id "
									 + "WHERE user_id = ? AND user_event_type_id = 1;";

				getOrganizedEvtsStmt = connection.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS);
				getOrganizedEvtsStmt.setInt(1, userId);
				resultSet = getOrganizedEvtsStmt.executeQuery();
				while (resultSet.next()) {
					int docId = resultSet.getInt("DocId");
					out.write(String.format(
							"<div class=\"col-xs-12 col-sm-6 col-md-3 col-lg-3 thumbnail hosted-evt\" data-evt=\"%d\">",
							resultSet.getInt("id")));
					out.write(String.format("<a href=\"/urestaurant/getimage?img=%d\" title=\"%s\">", docId,
							resultSet.getString("name")));
					out.write(String.format("<img class=\"evt-img\" src=\"/urestaurant/getimage?img=%d\" />", docId));
					out.write("</a>");
					out.write(String.format("<div class\"caption\"><h5>%s</h5>", resultSet.getString("name")));
					out.write(String.format("<h5>%s</h5>", resultSet.getTimestamp("start_date").toString()));
					out.write("<div class=\"btn-toolbar\"><div class=\"btn-group btn-group-xs\">");
					out.write(String.format("<a href=\"/urestaurant/PopulateViewEvent?id="+resultSet.getInt("id")+"\" class=\"btn btn-primary\" role=\"button\"><i class=\"fa fa-eye\"></i> %s </a>", 
							bundle.getString("seeEvent")));
					out.write(String.format("<a href=\"#invite-modal\" class=\"btn btn-success invite\" data-toggle=\"modal\" role=\"button\"><i class=\"fa fa-share\"></i> %s </a></div>", 
							bundle.getString("invite")));
					out.write(String.format("<a href=\"/urestaurant/PopulateEditEventForms?term=" + resultSet.getInt("id") + "\" class=\"btn btn-primary btn-xs\" role=\"button\" style=\"float: right;\"><i class=\"fa fa-edit\"></i> %s </a>", bundle.getString("edit")));
//					out.write("<button type=\"button\" class=\"btn btn-primary dropdown-toggle\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">RSVP <span class=\"caret\"></span></button>");
//					out.write("<ul class=\"dropdown-menu\"><li><a href=\"#\" data-sts=\"1\">Going</a></li><li><a href=\"#\" data-sts=\"2\">Interested</a></li><li><a href=\"#\" data-sts=\"3\">Not Going</a></li></ul>");

					out.write("</div></div></div></div>");
				}
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (SQLException e) {
				// TODO log error
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} finally {
				if (out != null)
					out.close();
				if (resultSet != null)
					try {
						resultSet.close();
					} catch (SQLException e) {
						/* TODO log error */ }
				if (getOrganizedEvtsStmt != null)
					try {
						getOrganizedEvtsStmt.close();
					} catch (SQLException e) {
						/* TODO log error */ }
			}
		} else { // redirect if userid does not exist in session
			response.getWriter().write("unauthorized");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
