package servlets;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connections.DatabaseConnectionFactory;

/**
 * Servlet implementation class GetEventGraphicServlet
 */
@WebServlet("/getimage")
public class GetEventGraphicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetEventGraphicServlet() { super(); }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResultSet resultSet = null;
		PreparedStatement getOrganizedEvtsStmt = null;
		ServletOutputStream out = response.getOutputStream();
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection()) {
			final String sqlStmt = "SELECT * FROM documents WHERE id = ?";
			getOrganizedEvtsStmt = connection.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS);
			getOrganizedEvtsStmt.setInt(1, Integer.parseInt(request.getParameter("img")));
			resultSet = getOrganizedEvtsStmt.executeQuery();
			if(resultSet.next()) {
				response.setContentType(resultSet.getString("content_type"));
				Blob img = resultSet.getBlob("document");
				InputStream in = img.getBinaryStream();
				int length = (int)img.length();
				int bufferSize = 1024;
			    byte[] buffer = new byte[bufferSize];	
			    while ((length = in.read(buffer)) != -1) {
			        out.write(buffer, 0, length);
			    }
			}
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (SQLException e) {
			// TODO log error
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			if (out != null) out.close();
			if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { /* TODO log error */ }
			if (getOrganizedEvtsStmt != null) try { getOrganizedEvtsStmt.close(); } catch (SQLException e) { /* TODO log error */ }
		}
	}

}
