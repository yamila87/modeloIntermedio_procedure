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
	private  BufferedReader br;
	private  String currentLine;
	private  ArrayList<String> contentLines;
	private  ArrayList<String[]> valuesList;

	private final String reg ="[0-9]+-[0-9]+-[0-9]+ [0-9]+:[0-9]+:[0-9]+\\.0$";
	private final String splitBy="\t";
	private final String toReplace ="\\.[0-9]+";
	

	private  ArrayList<String> getFileContent ( String fileStr){
		logger.debug("Archivo a leer: " + fileStr);
		
		br = null;
		currentLine = null;
		contentLines = new ArrayList<String>();
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
	
	
	private ArrayList<String[]> getArray (ArrayList<String> arrayLines){		 
		valuesList = new  ArrayList<String[]>();	
		
		for (int i = 1 ; i<arrayLines.size();i++){
			String[] colValus = arrayLines.get(i).split(splitBy);			
			for(int j=0; j<colValus.length;j++){ //TODO arreglarlo desde el query

				if(Pattern.matches(reg,  colValus[j])){
					String aux =  colValus[j].replaceAll(toReplace,"");				
					colValus[j] = aux;
				}
			}
			valuesList.add(colValus);		
		}		
		return valuesList;
	}
	
	public ArrayList<String[]> getParsedContent ( String fileStr){
		return getArray(getFileContent (fileStr));
	}
}
