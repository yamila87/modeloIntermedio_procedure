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
	

	
	public void openFile(String fileStr) throws FileNotFoundException{
		br = new BufferedReader(new FileReader(fileStr));
	}
	
	public ArrayList<String[]> read100Lines () throws IOException{
		int i = 0;
		int max = 100;
		valuesList = new  ArrayList<String[]>();	
		while ((currentLine = br.readLine()) != null && i<max) {
			String[] colValus = currentLine.split(splitBy,-1);
			valuesList.add(colValus);
			i++;
		}
				
		logger.trace(currentLine);
		return valuesList;
	}
	
	
	public void closeFile (){
		if(br!=null){
			try {
				br.close();
			} catch (IOException e) {
				logger.error("Error al leer csv ",e);
			}
		}
	}
	
	private  ArrayList<String[]> getFileContent ( String fileStr){
		logger.debug("Archivo a leer: " + fileStr);
		
		br = null;
		currentLine = null;
		contentLines = new ArrayList<String>();
		valuesList = new  ArrayList<String[]>();	
		try {
			br = new BufferedReader(new FileReader(fileStr));
			
			while ((currentLine = br.readLine()) != null) {
				//contentLines.add(currentLine);
				
				String[] colValus = currentLine.split(splitBy,-1);			
/*				for(int j=0; j<colValus.length;j++){ //TODO arreglarlo desde el query

					if(Pattern.matches(reg,  colValus[j])){
						String aux =  colValus[j].replaceAll(toReplace,"");				
						colValus[j] = aux;
					}
				}*/
				valuesList.add(colValus);			
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
			
		return valuesList;
	}
	
	
/*	private ArrayList<String[]> getArray (ArrayList<String> arrayLines){		 
		valuesList = new  ArrayList<String[]>();	
		
		for (int i = 0 ; i<arrayLines.size();i++){
			String[] colValus = arrayLines.get(i).split(splitBy,-1);			
			for(int j=0; j<colValus.length;j++){ //TODO arreglarlo desde el query

				if(Pattern.matches(reg,  colValus[j])){
					String aux =  colValus[j].replaceAll(toReplace,"");				
					colValus[j] = aux;
				}
			}
			valuesList.add(colValus);		
		}		
		return valuesList;
	}*/
	
	public ArrayList<String[]> getParsedContent ( String fileStr){
		return getFileContent (fileStr);
	}
}
