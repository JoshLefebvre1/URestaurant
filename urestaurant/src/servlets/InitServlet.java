/*
 * File name: InitServlet.java
 * Compiler: Eclipse Java EE IDE Luna Release 4.4
 * Author: Christopher Elliott
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Purpose: Initializes the database connection factory. Maybe more initialization functionality to come later.
 */
package servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import connections.DatabaseConnectionFactory;

@WebServlet(urlPatterns = "/init", loadOnStartup = 1)
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public InitServlet() 
	{ 
		super(); 
	}

	public void init(ServletConfig config) throws ServletException 
	{
	    try
	    {
	        DatabaseConnectionFactory.getConnectionFactory().init();
	    } 
	    catch (IOException e) 
	    {
	        config.getServletContext().log(e.getLocalizedMessage(),e);
	    }
	}
}
