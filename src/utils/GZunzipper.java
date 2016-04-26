package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class GZunzipper {

	
	public static boolean gunzipGZ(String gzPath,String tmpPath){
		 boolean result = false;
	     byte[] buffer = new byte[1024];
	     GZIPInputStream gzis = null;
	     FileOutputStream out=null;
	     
	     try{
	 
	    	gzis = new GZIPInputStream(new FileInputStream(gzPath));	 
	    	out = new FileOutputStream(tmpPath);
	 
	        int len;
	        while ((len = gzis.read(buffer)) > 0) {
	        	out.write(buffer, 0, len);
	        }
	 
	    	System.out.println("Done");
	    	result=true;
	    }catch(IOException ex){
	       ex.printStackTrace();   
	    }finally{
	    	
	    	if(gzis!=null){
	    		 try {
					gzis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
	    	if(out!=null){
	    		try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	       
	     return result;
	} 
	
}
