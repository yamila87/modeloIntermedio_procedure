package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class GZunzipper {
	final static Logger logger = Logger.getLogger(GZunzipper.class);
	
	public static boolean gunzipGZ(String gzPath,String tmpPath){
		 boolean result = false;
	     byte[] buffer = new byte[1024];
	     GZIPInputStream gzis = null;
	     FileOutputStream out=null;
	     
	     try{
	 
	    	logger.debug("A descomprimir: "+ gzPath +" en "+tmpPath); 	
	    	gzis = new GZIPInputStream(new FileInputStream(gzPath));	 
	    	out = new FileOutputStream(tmpPath);
	 
	        int len;
	        while ((len = gzis.read(buffer)) > 0) {
	        	out.write(buffer, 0, len);
	        }
	 
	        logger.debug("Archivo descomprimido: "+tmpPath);
	    	result=true;
	    }catch(IOException ex){
	       logger.error("Error al descomprimir: "+ gzPath ,ex);
	    }finally{
	    	
	    	if(gzis!=null){
	    		 try {
					gzis.close();
				} catch (IOException e) {
					 logger.error("Error al descomprimir: "+ gzPath ,e);
				}
	    	}
	    	
	    	if(out!=null){
	    		try {
					out.close();
				} catch (IOException e) {
					 logger.error("Error al descomprimir: "+ gzPath ,e);
				}
	    	}
	    }
	       
	     return result;
	} 
	
}
