package com.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class GZunzipper {
	final static Logger logger = Logger.getLogger(GZunzipper.class);
	
	
	 private boolean result;
	 private byte[]buffer ;
	 private GZIPInputStream gzis;
	 private FileOutputStream out;
	
	public boolean gunzipGZ(String gzPath,String tmpPath){
		 result = false;
	     buffer = new byte[1024];
	     gzis = null;
	     out=null;
	     
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
	    }catch(Exception ex){
	    	logger.error("Error al descomprimir: "+ gzPath ,ex);
	    }
	     finally{
	    	
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
	
	
	public BufferedReader  getGZIPStream (String gzPath){

		 FileInputStream fin = null;
   	  	GZIPInputStream gzis = null;
   	  	InputStreamReader xover = null;
   	  	BufferedReader br = null;
		
		
	     try{
	 
	    	logger.debug("A Leer: "+ gzPath); 	
	    	
	    	 fin = new FileInputStream(gzPath);
	    	 gzis = new GZIPInputStream(fin);
	    	 xover = new InputStreamReader(gzis);
	    	 br = new BufferedReader(xover);
	    	
	    }catch(IOException ex){
	       logger.error("Error al leer: "+ gzPath ,ex);
	    }catch(Exception ex){
	    	logger.error("Error al leer: "+ gzPath ,ex);
	    }
	   
	     return br;
	} 
	
	
	public void closeGzipStream(BufferedReader br) throws IOException{
		if(br!=null){
			br.close();
		}
	}
}
