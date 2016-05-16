package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class PopulateEditUserServlet
 */
@WebServlet("/PopulateEditUserServlet")
public class PopulateEditUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PopulateEditUserServlet() 
    {
    	super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("application/json");
		
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
		{
			//Get ID of user being edited
			HttpSession session = request.getSession(false);
			int userID = (Integer)session.getAttribute("userid");
			
			
			//Select Contact Info
			final String selectSQL = "SELECT * FROM users"
										+" INNER JOIN contacts ON users.contact_id = contacts.id"
										+" INNER JOIN emails ON emails.contact_id = contacts.id"
										+" LEFT JOIN phone_numbers ON phone_numbers.contact_id = contacts.id"
										+" WHERE users.id = ?";
			PreparedStatement selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setInt(1, userID);
			ResultSet resultSet = selectStmt.executeQuery();
			String firstName=null;
			String lastName=null;
			List<String> email= new ArrayList<String>();
			List<String> phone= new ArrayList<String>();
			while(resultSet.next())
			{
				firstName = resultSet.getString("firstname");
				lastName=resultSet.getString("lastname");
				email.add(resultSet.getString("email"));
				phone.add(resultSet.getString("phone_number"));
			}
			
			//Set request attributes based on the currently stored user attributes the user wishes to edit
			request.setAttribute("firstname", firstName);
			request.setAttribute("lastname", lastName);
			request.setAttribute("email", email.get(0));
			if(phone.get(0)==null||phone.get(0).isEmpty())
			{
				request.setAttribute("phonenumber", "");
			}
			else
			{
				request.setAttribute("phonenumber", phone.get(0));
			}

			resultSet.close();
	        selectStmt.close();
	        RequestDispatcher rd=request.getRequestDispatcher("/edituser.jsp");//Redirects to editevent.jsp page
			rd.forward(request,response);
	        
		}
		catch (SQLException e) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		e.printStackTrace();
		}
	}


}
