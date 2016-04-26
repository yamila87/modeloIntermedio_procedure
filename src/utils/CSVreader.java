package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class CSVreader {//csvutils
	final static Logger logger = Logger.getLogger(CSVreader.class);

	private static ArrayList<String> getFileContent ( String fileStr){
		logger.debug("Archivo a leer: " + fileStr);
		
		BufferedReader br = null;
		String currentLine = null;
		ArrayList<String> contentLines = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fileStr));
			
			while ((currentLine = br.readLine()) != null) {
				contentLines.add(currentLine);
				logger.trace(currentLine);
			}
			
		} catch (FileNotFoundException e) {
			logger.error("Error al leer: " + fileStr ,e);
		} catch (IOException e) {
			logger.error("Error al leer: " + fileStr ,e);
		}finally{
			
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					logger.error("Error al leer: " + fileStr ,e);
				}
			}
		}
			
		return contentLines;
	}
	
	
	private static  ArrayList<String[]> getArray (ArrayList<String> arrayLines){		 
		ArrayList<String[]> valuesList = new  ArrayList<String[]>();
		String reg ="[0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+:[0-9]+\\.0$";
		
		for (int i = 1 ; i<arrayLines.size();i++){
			String[] colValus = arrayLines.get(i).split("\t");			
			for(int j=0; j<colValus.length;j++){
				String value = colValus[j];

				if(Pattern.matches(reg, value)){
					String aux =  colValus[j].replaceAll("\\.[0-9]+","");				
					colValus[j] = aux;
				}
			}
			valuesList.add(colValus);		
		}		
		return valuesList;
	}
	
	public static ArrayList<String[]> getParsedContent ( String fileStr){
		return getArray(getFileContent (fileStr));
	}
}
