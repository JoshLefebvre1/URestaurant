package not_used;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import connections.DatabaseConnectionFactory;

public class GetPublicFullEventsServlet {
	
	public GetPublicFullEventsServlet(){
		super();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		response.setContentType("application/json");
		
		try (Connection connection = DatabaseConnectionFactory.getConnectionFactory().getConnection())
		{
		
			ResultSet resultSet;
			
			final String selectSQL = "select * from events where is_public=?"; 
			PreparedStatement selectStmt = connection.prepareStatement(selectSQL, Statement.RETURN_GENERATED_KEYS);
			selectStmt.setString(1, "1");
			resultSet = selectStmt.getGeneratedKeys();
			StringBuilder json = new StringBuilder("{ \"events\" : [");
			while(resultSet.next()){
				String name = resultSet.getString("name");
				String description = resultSet.getString("description");
				String location = resultSet.getString("location");
				String startDate = resultSet.getString("start_date");
				String endDate = resultSet.getString("end_date");
				String capacity = resultSet.getString("capacity"); //Will it work?
				
				json.append("{ \"name\" : ").append(name).append(", \"description\" : ").append(description)
				.append(", \"location\" : ").append(location).append(", \"startDate\" : ").append(startDate).append(", \"endDate\" : ")
				.append(endDate).append(", \"capacity\" : ").append(capacity).append("},");
				
			}
			json.append("] }");
			response.getWriter().write(json.toString());
			
			resultSet.close();
	        selectStmt.close();
			
		}
		catch (SQLException e) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		e.printStackTrace();
		}
	}	

}
