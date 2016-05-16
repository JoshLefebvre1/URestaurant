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

@WebServlet("/getmapevents")
public class GetMapEventsServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	public GetMapEventsServlet()
	{
		super();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setContentType("application/json");
		ResultSet resultSet = null;
		PreparedStatement selectStmt = null;
		ResourceBundle bundle = ResourceBundle.getBundle("labels", Locale.CANADA);
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("userid") != null && session.getAttribute("userid") != "") {
			int userId = (int)session.getAttribute("userid");
			String locale = (String)session.getAttribute("locale");
			if ("fr_CA".equals(locale)) bundle = ResourceBundle.getBundle("labels", Locale.CANADA_FRENCH);
			
			try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
			{
				//final String selectSQL = "SELECT DISTINCT * FROM events WHERE is_public=?";
				final String selectSQL = "SELECT DISTINCT events.id, name, description, location, start_date, is_public, user_event_type_id, user_event_status_id FROM events "
				+ "JOIN user_events ON event_id = events.id " 
				+ "WHERE (is_public=? AND user_event_type_id <> ?) OR (user_id = ? AND (user_event_status_id = 1 OR user_event_status_id = 2))";

				selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
				selectStmt.setString(1, "1");
				selectStmt.setInt(2, userId);
				selectStmt.setInt(3, userId);
				resultSet = selectStmt.executeQuery();
				StringBuilder json = new StringBuilder("{ \"events\" : [");
				while(resultSet.next()){
					String eventId = resultSet.getString("id");
					String name = resultSet.getString("name");
					String location = resultSet.getString("location");
					String startDate = resultSet.getString("start_date");
					String userEventStatus = resultSet.getString("user_event_status_id");
					
					json.append("{ \"id\" : \"").append(eventId)
					.append("\", \"name\" : \"").append(name)
					.append("\", \"address\" : \"").append(location)
					.append("\", \"startDate\" : \"").append(startDate)
					.append("\", \"userEventStatus\" : \"").append(userEventStatus)
					.append("\", \"latitude\": \"")
					.append("\", \"longitude\":\"\"")
					//.append("\", \"location\" : { \"latitude\": \"\", \"longitude\":\"\" }")
					.append(resultSet.isLast() ? "}" : "},");
				}
				json.append("] }");
				response.getWriter().write(json.toString());
				response.setStatus(HttpServletResponse.SC_OK);
			}
			catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				e.printStackTrace(); // TODO log exception
			} finally {
				if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* TODO log exception */ }
				if (selectStmt != null) try { selectStmt.close(); } catch (SQLException e) { /* TODO log exception */ }
			}
		}
	}
}