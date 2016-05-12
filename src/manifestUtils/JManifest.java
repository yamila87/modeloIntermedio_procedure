package manifestUtils;

import java.util.LinkedHashMap;


import com.google.gson.Gson;

public class JManifest {

	//private JSManifestObj obj;
	private int logIdMax;
	private int logIdMin;
	 
	   
		private LinkedHashMap<String , JSManifestItems> files;
		
		public LinkedHashMap<String , JSManifestItems> getFiles() {
			return files;
		}
		public void addFiles(String key , JSManifestItems obj) {
			if(files==null){
				files = new LinkedHashMap<String , JSManifestItems>();
			}
			
			this.files.put(key.trim(), obj);
		}
	
	
	
	public int getLogIdMax() {
		return logIdMax;
	}
	public void setLogIdMax(String logIdMax) {
		this.logIdMax = Integer.valueOf(logIdMax.trim());
	}
	public int getLogIdMin() {
		return logIdMin;
	}
	public void setLogIdMin(String logIdMin) {
		this.logIdMin = Integer.valueOf(logIdMin.trim());
	}
	
	public String toString(){
		Gson gson = new Gson();
		String jsonStr = gson.toJson(this); 
		return jsonStr;

	}
	
	
}
