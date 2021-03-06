package com.manifestUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;


public class ManifestParser {

	final static Logger logger = Logger.getLogger(ManifestParser.class);
	
	private BufferedReader br = null;		
	private StringBuilder str = new StringBuilder ();
	private JManifest m = new JManifest();
	private JSManifestItems items = null;
	private String currentLine;
	
	public JManifest getJSONManifest(String fileName) {

		br = null;		
		str = new StringBuilder ();
		m = new JManifest();
		items = null;
		currentLine = "";
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			while ((currentLine = br.readLine()) != null) {
				str.append(currentLine);
				logger.trace(currentLine);
			}
	
			String strFiles = str.substring(str.indexOf("{")+1, str.lastIndexOf("}"));
			
			String minLogId = strFiles.substring(strFiles.lastIndexOf(","), strFiles.length());		
			m.setLogIdMin(minLogId.split(":")[1]);

			String fileLogId = strFiles.substring(0, strFiles.lastIndexOf(","));
			String maxLogID = fileLogId.substring(fileLogId.lastIndexOf(","),fileLogId.length());
			m.setLogIdMax(maxLogID.split(":")[1]);
						
			String soloFile = fileLogId.substring(0 , fileLogId.lastIndexOf(","));			
			String soloObj = soloFile.substring(soloFile.indexOf("{")+1 , soloFile.lastIndexOf("}"));				
			String [] objs = soloObj.split("},");
			
			
			for(int i=0 ; i<objs.length;i++){
				String key = objs[i].substring(0, objs[i].indexOf(":")).replace("\"","");				
				String keyValues =objs[i].substring(objs[i].indexOf(":")+1,objs[i].length()).replace("\"","");							
				String [] values = keyValues.replace("\"","").replace("{","").split(",");
				
				items = new JSManifestItems();
				
				items.setCnt(values[0].split(":")[1]);
				items.setFname(values[1].split(":")[1]);
				items.setOutPath(values[2].split(":")[1]);
				items.setDigest(values[3].split(":")[1]);
				items.setQuery(values[4].split(":")[1]);
			//TODO probar que pasa si viene sin valores ;P
				
				m.addFiles(key, items);	
			}
			logger.debug("Cantidad de objetos file: " + objs.length);
			
		} catch (FileNotFoundException e) {
			logger.error("Error al parsear "+ fileName,e);
		} catch (IOException e) {
			logger.error("Error al parsear"+ fileName,e);
		}finally{
			
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Error al parsear"+ fileName,e);
				}
			}
		}

		return m;
	}

}
