package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
  
  // create constant variables
  private static String HOST = "localhost";
  private static String PASSWORD = "projects";
  private static int PORT = 3306;
  private static String SCHEMA = "projects";
  private static String USER = "projects";
  
  // get the db connection
  public static java.sql.Connection getConnection(){
    String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false",
                 HOST, PORT, SCHEMA, USER, PASSWORD);
    
    // attempt connection. print success or failure message.
    try {
      Connection conn = DriverManager.getConnection(uri);
      System.out.println("Connection to schema " + SCHEMA + " was successful!");
      return conn;
    }
    catch(SQLException e) {
      System.out.println("Unable to connect to " + uri);
      throw new DbException("Unable to connect to \" + uri ");
    }
  }
}
