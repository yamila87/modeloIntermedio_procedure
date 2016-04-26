package DB;

import gestor.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

public class DBconnector {
	final static Logger logger = Logger.getLogger(DBconnector.class);
	
	 public  Connection getConnection(){ 		 
		 Connection conn = null;
		 try{ 
			 conn = DriverManager.getConnection(Configuration.getInstance().getURL(),
					 							Configuration.getInstance().getUser(),
					 							Configuration.getInstance().getPassword());

			logger.debug("Conectado");
 
		 }
		 catch(Exception e){ 
			logger.error("Error al conectarse a la base: " + Configuration.getInstance().getURL(),e);
		 } 	
		 return conn;
	 } 
}
