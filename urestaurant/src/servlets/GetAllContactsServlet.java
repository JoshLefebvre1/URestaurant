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

import utilities.Contact;
import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class GetAllContactsServlet
 */
@WebServlet("/allcontacts")
public class GetAllContactsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllContactsServlet() { super(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int userId = -1;
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
		if (session != null && session.getAttribute("userid") != null) userId = (int)session.getAttribute("userid");
		if (userId == -1) {
			response.getWriter().write("{ \"redirect\" : true }");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
			{
				final String selectSQL = "SELECT contacts.id, firstname, lastname, email, photo_id, emails.contact_type_id AS e_id FROM contacts "
									   + "LEFT JOIN emails ON contact_id = contacts.id "
									   + "WHERE user_id = ? ORDER BY firstname,contacts.id";
				selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
				selectStmt.setInt(1, userId);
				resultSet = selectStmt.executeQuery();
				boolean printContact = false;
				Contact contact = new Contact();
				for(int i = 0, id = 0; resultSet.next(); ){
					id = resultSet.getInt("id");
					if (id != i) {
						// send contact info
						if (printContact) {
							out.write("<div class=\"list-group-item\" data-id=\"" + contact.getId() + "\"><div class=\"userItem\">");
							out.write("<img class=\"img-circle\" width=\"18\" src=\"");
							out.write("/urestaurant/getimage?img=" + contact.getPhotoId() + "\"/>"); // TODO get better avatar
							out.write(String.format("<h4 class=\"list-group-item-heading\">%s %s</h4>", contact.getFirstName(), contact.getLastName()));
							out.write(String.format("</div><p class=\"list-group-item-text\">%s</p></div>", contact.getEmails().size() > 0 ? contact.getEmails().get(0) : ""));
						} else {
							printContact = true;
						}
						i = id;
						contact.setId(id);
						contact.setFirstName(resultSet.getString("firstname"));
						contact.setLastName(resultSet.getString("lastname"));
						if (resultSet.getInt("photo_id") > 0) contact.setPhotoId(resultSet.getInt("photo_id"));
						else contact.setPhotoId(0);
						contact.getEmails().clear();
					}
					if (resultSet.getInt("e_id") == 1)
						contact.getEmails().add(resultSet.getString("email"));
				}
				// print last contact
				out.write("<div class=\"list-group-item\" data-id=\"" + contact.getId() + "\"><div class=\"userItem\">");
				out.write("<img class=\"img-circle\" width=\"18\" src=\"");
				out.write("/urestaurant/getimage?img=" + contact.getPhotoId() + "\"/>"); // TODO get better avatar
				out.write(String.format("<h4 class=\"list-group-item-heading\">%s %s</h4>", contact.getFirstName(), contact.getLastName()));
				out.write(String.format("</div><p class=\"list-group-item-text\">%s</p></div>", contact.getEmails().size() > 0 ? contact.getEmails().get(0) : ""));
//				while(resultSet.next()){
//					int docId = resultSet.getInt("photo_id");
//					out.write("<div class=\"list-group-item\" data-id=\"" + resultSet.getInt("id") + "\"><div class=\"userItem\">");
//					out.write("<img class=\"img-circle\" width=\"18\" src=\"");
//					out.write(docId > 0 ? "/urestaurant/getimage?img=" + docId : "images/avatar01.jpg\"/>"); // TODO get better avatar
//					out.write(String.format("<h4 class=\"list-group-item-heading\">%s %s</h4>", resultSet.getString("firstname"), resultSet.getString("lastname")));
//					out.write(String.format("</div><p class=\"list-group-item-text\">%s</p></div>", resultSet.getString("email")));
//					/*
//					<a href="#" class="list-group-item">
//					<div class="userItem">
//						<!-- <i class="fa fa-user"></i> -->
//						<img class="img-circle" width="18" src="images/avatar01.jpg"/>
//						<h4 class="list-group-item-heading">Chris Elliott</h4>
//					</div>
//					<p class="list-group-item-text">email@email.com</p>
//					<button class="btn btn-info pull-right" type="submit">Invite</button>
//				</a>*/
//				}
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

}
