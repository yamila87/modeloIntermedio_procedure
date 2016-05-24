package procedureExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class  Configuration {

	final static Logger logger = Logger.getLogger(Configuration.class);
	
	private static Configuration instance=null; ;
		
	private String driver="";
	private String motor="";
	private String host="";
	private String port="";
	private String service="";
	private String user="";
	private String password="";
	private String packageName="";
	private transient String URL;
	
	private String gzPath="";
	private String tmpPath="";
	private String cntPath="";
	private int syncDeltaMax;
	
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
				logger.debug("Parseando json...");
				instance = gson.fromJson(br, Configuration.class);
				instance.setValues();
			
				logger.trace("Config: " + gson.toJson(instance));
			} catch (Exception e) {
				logger.error("Error al cargar configuracion ",e);
			}
		}
		return instance;
	}

	
	public String getDriver() {
		return driver;
	}

	public String getMotor() {
		return motor;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getService() {
		return service;
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

	private void setValues (){
		gzPathFile = new File (gzPath);
		tmpPathFile = new File (tmpPath);
		cntPathFile = new File(cntPath);
		groupFile = new File (groupFilePath);
				
		tmpPathFile.mkdirs();
		cntPathFile.mkdirs();
		
		packageName = packageName+".";
		URL = "jdbc:"+motor+":thin:@"+host+":"+ port +":"+service;  
		
		logger.debug("Directorios creados");
	}


	
}
