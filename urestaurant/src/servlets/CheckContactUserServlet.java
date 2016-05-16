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
 * Servlet implementation class CheckContactUserServlet
 */
@WebServlet("/userlink")
public class CheckContactUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckContactUserServlet() { super(); }

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
		response.setContentType("json/application");
		ResultSet resultSet = null;
		PreparedStatement selectStmt = null;
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) {
			final String selectSQL = "SELECT * FROM users "
									+"INNER JOIN contacts ON users.contact_id = contacts.id "
									+"INNER JOIN emails ON emails.contact_id = contacts.id "
									+"WHERE email = ? AND contact_type_id <> 6";
			selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setString(1, request.getParameter("email"));
			resultSet = selectStmt.executeQuery();
			StringBuilder json = new StringBuilder("{ \"users\" : [ ");
			for(int i = 0; resultSet.next(); i++){
				if (i > 0) json.append(" , ");
				json.append(" { \"id\" : " + resultSet.getInt("users.id") + " , ");
				json.append(" \"username\" : \"" + resultSet.getString("username") + "\" , ");
				json.append(" \"firstname\" : \"" + resultSet.getString("firstname") + "\" , ");
				json.append(" \"lastname\" : \"" + resultSet.getString("lastname") + "\" , ");
				json.append(" \"photo_id\" : " + resultSet.getInt("photo_id") + " }");
			}
			json.append(" ] }");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(json.toString());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // TODO log exception
		} finally {
			if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* TODO log exception */ }
			if (selectStmt != null) try { selectStmt.close(); } catch (SQLException e) { /* TODO log exception */ }
		}
	}

}
