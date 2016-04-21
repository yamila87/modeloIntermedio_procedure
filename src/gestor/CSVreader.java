package gestor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CSVreader {//csvutils
	

	private ArrayList<String> getFileContent ( String fileStr){
		
		BufferedReader br = null;

		String currentLine = null;
		ArrayList<String> contentLines = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fileStr));
			
			while ((currentLine = br.readLine()) != null) {
				contentLines.add(currentLine);
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
			
		return contentLines;
	}
	
	
	private  ArrayList<String[]> getArray (ArrayList<String> arrayLines){
		 
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
	
	public  ArrayList<String[]> getParsedContent ( String fileStr){
		return getArray(getFileContent (fileStr));
	}
}
