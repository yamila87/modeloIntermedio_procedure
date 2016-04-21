package gestor;

import java.io.File;

public class  Configuration {

		
	private String driver;
	private String host;
	private String port;
	private String service;
	private String user;
	private String password;
	private String packageName;
	
	
	private String gzPath;
	private String tmpPath;
	
	private File tmpPathFile;
	private File gzPathFile;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGzPath() {
		return gzPath;
	}
	public void setGzPath(String gzPath) {
		this.gzPath = gzPath;
	}
	public String getTmpPath() {
		return tmpPath;
	}
	public void setTmpPath(String tmpPath) {
		this.tmpPath = tmpPath;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public File getGzPathFile() {
		return gzPathFile;
	}
	public void setGzPathFile(File gzPathFile) {
		this.gzPathFile = gzPathFile;
	}
	
	public void setValues (){
		driver ="oracle.jdbc.driver.OracleDriver";
		host="10.70.254.197";
		port="1521";
		service="dbnav11g";
		user="NAVCABLE_DESA";
		password="NAVCABLE_DESA";


		gzPath="C:\\Users\\ysalomon\\workspaces\\gestor\\certa_deltas";
		
		tmpPath="C:\\Users\\ysalomon\\workspaces\\gestor\\Gestor\\tmp";
	
		gzPathFile = new File (gzPath);
		tmpPathFile = new File (tmpPath);
		
		tmpPathFile.mkdir();
		packageName = "custom_cv"+".";
		
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public File getTmpPathFile() {
		return tmpPathFile;
	}
	public void setTmpPathFile(File tmpPathFile) {
		this.tmpPathFile = tmpPathFile;
	}
	
	
}
