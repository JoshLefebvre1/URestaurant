/*
 * File name: DatabaseConnectionFactory.java
 * Implemented by: Christopher Elliott
 * Date: 01/02/2015 (DD/MM/YYYY)
 * Database connection factory class.
 * Extracted from: Java EE Development with Eclipse - Second Edition by Ram Kulkarni
 */
package connections;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DatabaseConnectionFactory {
	
	private DataSource dataSource = null;
	private static DatabaseConnectionFactory instance = null;
	
	private DatabaseConnectionFactory() 
	{ }
	
	/**
	 * Must be called before any other method in this class.
	 * Initializes the data source and saves it in an instance variable
	 *
	 * @throws IOException
	 */
	public synchronized void init() throws IOException 
	{
		// check if init was already called
		if (dataSource != null) 
			return;
		
		Properties dbProperties = new Properties();
		try (InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("database.properties")) 
		{
			dbProperties.load(inStream);
		}
	    
	    // create Tomcat specific pool properties
	    PoolProperties p = new PoolProperties();
	    p.setUrl("jdbc:mysql://" + dbProperties.getProperty("db_host") + ":" + dbProperties.getProperty("db_port") + "/"  + dbProperties.getProperty("db_name"));
	    p.setDriverClassName(dbProperties.getProperty("db_driver_class_name"));
	    p.setUsername(dbProperties.getProperty("db_user_name"));
	    p.setPassword(dbProperties.getProperty("db_password"));
	    p.setMaxActive(10);

	    dataSource = new DataSource();
	    dataSource.setPoolProperties(p);
	}
	
	// Provides access to singleton instance
	public static DatabaseConnectionFactory getConnectionFactory() 
	{
		if (instance == null) 
			instance = new DatabaseConnectionFactory();
		
		return instance;
	}
	
	// returns database connection object 
	public Connection getConnection () throws SQLException 
	{
	    if (dataSource == null) throw new SQLException("Error initializing datasource");
	    return dataSource.getConnection();
	}
}
