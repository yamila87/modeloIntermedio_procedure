package com.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.procedureExecutor.Configuration;

public class GroupJsonUtils {

	final static Logger logger = Logger.getLogger(GroupJsonUtils.class);
	
	private static Map<String,GroupCFGObj> gcfg;
	
	public void loadGroupJson (){
		StringBuilder str = new StringBuilder();
		BufferedReader br=null;
		gcfg=null;
		if(Configuration.getInstance().getGroupFile().exists()){
			try {
				br = new BufferedReader(new FileReader(Configuration.getInstance().getGroupFile()));
				String currentLine = "";
				while ((currentLine = br.readLine()) != null) {
					str.append(currentLine);
				}
	
				String json = str.toString().substring(str.toString().indexOf("=")+1, str.toString().length());
				
				logger.trace("groupBy: " + json);
				
			    Gson gson = new GsonBuilder().create();
	            Type typeOfHashMap = new TypeToken<Map<String, GroupCFGObj>>() { }.getType();
	             gcfg = gson.fromJson(json, typeOfHashMap); 
	    
	             logger.debug("group configuration: "  + gson.toJson(gcfg));
			} catch (FileNotFoundException e) {
				logger.error("Error al leer archivo " + Configuration.getInstance().getGroupFile() ,e);
			} catch (IOException e) {
				logger.error("Error al leer archivo " + Configuration.getInstance().getGroupFile() ,e);
			}finally{
				if(br!=null){
					try {
						br.close();
					} catch (IOException e) {
						logger.error("Error al leer archivo " + Configuration.getInstance().getGroupFile() ,e);
					}
				}
			}
		}
	}
	
	
	
	public  int getGroupBy (String key,String[] cols){
		int result = -1;
		if(gcfg!=null){
			if(gcfg.containsKey(key)){
				String col = gcfg.get(key).getCampoResultado();
				for(int i = cols.length-1 ; i>=0;i--){
					if(cols[i].equals(col)){
						result=i;
						break;
					}
				}		
			}
		}	
		return result;
	}
	
	public String getResultType (String key){
		if(gcfg!=null){
			if(gcfg.containsKey(key)){
				return gcfg.get(key).getCampoResultadoTipo();
			}else{
				return null;
			}
		}
		return null;
		
	}
	
	
}
