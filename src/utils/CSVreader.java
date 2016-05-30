package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import procedureExecutor.Configuration;

public class CSVreader {//csvutils
	final static Logger logger = Logger.getLogger(CSVreader.class);
	private  BufferedReader br;
	private  String currentLine;
	private  ArrayList<String[]> valuesList;
	private final String splitBy="\t";

	
	public void openFile(String fileStr) throws FileNotFoundException{
		br = new BufferedReader(new FileReader(fileStr));
	}
	
	public ArrayList<String[]> read100Lines () throws IOException{
		int i = 0;
		int max = Configuration.getInstance().getMaxLines();  
		valuesList = new  ArrayList<String[]>();
		
		logger.info("Leyendo siguientes : " + max + " lineas");
		
		while ((currentLine = br.readLine()) != null && (i<max || max==0)) {
			if(i==0){
				logger.debug("primer linea de cada "+ max+" : " + currentLine);
			}
				
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
	

}
