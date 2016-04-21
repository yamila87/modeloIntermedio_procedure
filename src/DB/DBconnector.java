package DB;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnector {

	 private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	 private static final String DB_URL = "jdbc:oracle:thin:@10.70.254.197:1521:dbnav11g";
	 private static final String DB_USERNAME = "NAVCABLE_DESA";
	 private static final String DB_PASSWORD = "NAVCABLE_DESA";

	
	 public  Connection getConnection(){ 
		 
		 Connection conn = null;
		 try{ //Register the JDBC driver Class.forName(DB_DRIVER);
			 //Open the connection 
			 conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			 if(conn != null)
			 { 
				 System.out.println("Successfully connected.");
			 }
			 else{ 
				 System.out.println("Failed to connect.");
			 } 
		 }
		 catch(Exception e){ 
			 e.printStackTrace();
		 } 
	
		 return conn;
	 } 
	
	
	
	
}
