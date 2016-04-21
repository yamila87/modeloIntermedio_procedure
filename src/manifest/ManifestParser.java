package manifest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ManifestParser {

	public static JManifest getJSONManifest(String fileName) {

		BufferedReader br = null;		
		StringBuilder str = new StringBuilder ();
		JManifest m = new JManifest();
		JSManifestItems items = null;
		
		String currentLine = "";
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			while ((currentLine = br.readLine()) != null) {
				str.append(currentLine);		
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
			//probar que pasa si viene sin valores ;P
				
				m.addFiles(key, items);	
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return m;
	}

}
