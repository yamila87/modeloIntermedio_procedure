package com.dbUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.procedureExecutor.Configuration;

public class DBconnector {
	final static Logger logger = Logger.getLogger(DBconnector.class);
	private  Connection conn ;
	
	 public  Connection getConnection(){ 		 
		conn = null;
		 try{ 
			 conn = DriverManager.getConnection(Configuration.getInstance().getURL(),
					 							Configuration.getInstance().getUser(),
					 							Configuration.getInstance().getPassword());

			logger.debug("Conectado");
 
		 }
		 catch(SQLException e){ 
			logger.error("Error al conectarse a la base: " + Configuration.getInstance().getURL(),e);
		 } 	
		 return conn;
	 } 
}
