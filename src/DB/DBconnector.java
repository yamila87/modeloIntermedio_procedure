package DB;

import gestor.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnector {

	
	 public  Connection getConnection(){ 		 
		 Connection conn = null;
		 try{ 
			 conn = DriverManager.getConnection(Configuration.getInstance().getURL(),
					 							Configuration.getInstance().getUser(),
					 							Configuration.getInstance().getPassword());
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
