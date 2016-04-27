package utils;

import gestor.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class CNTManager {
	final static Logger logger = Logger.getLogger(CNTManager.class);
	
	private File cntFile;
	private String cntName = "SyncProc.cnt";
	private String cntFilePath=Configuration.getInstance().getCntPath()+File.separator+cntName;
	private static CNTManager instance;
	private int cnt; 
	
	public static CNTManager getInstance(){
		if(instance==null){
			instance = new CNTManager();
		}
		
		return instance;
	} 
	
	
	public void readCnt (){
			cntFile = new File(cntFilePath);
			if(cntFile.exists()){
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(cntFile));
					cnt=Integer.valueOf(br.readLine().trim());
					logger.debug("Valor encontrado:"+cnt);
				} catch (FileNotFoundException e) {
					logger.error("Error al leer cnt",e);
				} catch (IOException e) {
					logger.error("Error al leer cnt",e);
				}finally{
					try {
						br.close();
					} catch (IOException e) {
						logger.error("Error al leer cnt",e);
					}
				}
			}else{
				logger.warn("No se encontro el archivo SyncProc.cnt, se creara el mismo");
				cnt=Configuration.getInstance().getSyncDeltaMax();
			}				
	}
	

	public void updateCnt(){
		cnt =cnt + Configuration.getInstance().getSyncDeltaMax();
		
		BufferedWriter bw = null;
		try {
			logger.debug("Actualizando cnt a: "+ cnt);
			bw = new BufferedWriter(new FileWriter(cntFilePath));
			bw.write(String.valueOf(cnt));
		} catch (IOException e) {
			logger.error("Error al actualizar cnt",e);
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				logger.error("Error al actualizar cnt",e);
			}			
		}				
	}
	
	public int getCnt(){
		return cnt;
	}
}
