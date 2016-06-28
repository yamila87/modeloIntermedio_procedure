package com.procedureExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class  Configuration {

	final static Logger logger = Logger.getLogger(Configuration.class);
	
	private static Configuration instance=null; ;
		
	private String user="";
	private String password="";
	private String packageName="";
	private String customPackageName="";
	private String [] customProcedureName;
	
	private transient ArrayList <String> customProcedures;

	private  String URL;
	
	public void setURL(String uRL) {
		URL = uRL;
	}

	private String gzPath="";
	private String tmpPath="";
	private String cntPath="";
	private int syncDeltaMax;
	private int maxLines;
	

	private transient File tmpPathFile;
	private transient File gzPathFile;
	private transient File cntPathFile;
	private transient File groupFile;
	
	private String groupFilePath; 
	
	public static Configuration getInstance (){
		if(instance==null){
			String fileStr = "config.json";
			Gson gson = new Gson ();		
			BufferedReader br = null;
			try {
				logger.debug("Leyendo configuracion...");
				br = new BufferedReader(new FileReader(fileStr));
				instance = gson.fromJson(br, Configuration.class);
				instance.setValues();
			
				logger.debug("Config: " + gson.toJson(instance));
			} catch (Exception e) {
				logger.error("Error al cargar configuracion ",e);
			}
		}
		return instance;
	}

	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getURL() {
		return URL;
	}

	public String getGzPath() {
		return gzPath;
	}

	public String getTmpPath() {
		return tmpPath;
	}

	public String getCntPath() {
		return cntPath;
	}

	public int getSyncDeltaMax() {
		return syncDeltaMax;
	}

	public File getTmpPathFile() {
		return tmpPathFile;
	}

	public File getGzPathFile() {
		return gzPathFile;
	}

	public File getCntPathFile() {
		return cntPathFile;
	}

	public File getGroupFile(){
		return groupFile;
	}

	public int getMaxLines() {
		return maxLines;
	}

	
	public String getCustomPackageName() {
		return customPackageName;
	}

	public ArrayList<String> getCustomProcedures() {
		return customProcedures;
	}

	public void setCustomPackageName(String customPackageName) {
		this.customPackageName = customPackageName;
	}

	public void setCustomProcedureName(String[] customProcedureName) {
		this.customProcedureName = customProcedureName;
	}

	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
	}

	
	private void setValues (){
		customProcedures = new ArrayList<String>(Arrays.asList(customProcedureName));
		
		gzPathFile = new File (gzPath);
		tmpPathFile = new File (tmpPath);
		cntPathFile = new File(cntPath);
		groupFile = new File (groupFilePath);
				
		tmpPathFile.mkdirs();
		cntPathFile.mkdirs();
		
		packageName = packageName+".";
		customPackageName = customPackageName + ".";
		
		logger.debug("Directorios creados");
	}
}
