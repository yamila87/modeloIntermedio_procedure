package utils;

import gestor.Configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CNTManager {

	private File cntFile;
	private String cntName = "SyncProc.cnt";
	private String cntFilePath=Configuration.getInstance().getCntPath()+File.separator+cntName;
	
	private int cnt; 
	
	
	
	public int getCnt (){
			cntFile = new File(cntFilePath);
			if(cntFile.exists()){
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(cntFile));
					cnt=Integer.valueOf(br.readLine().trim());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				cnt=Configuration.getInstance().getSyncDeltaMax();
			}	
			
		return cnt;		
	}
	

	public void updateCnt(){
		cnt =cnt + Configuration.getInstance().getSyncDeltaMax();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(cntFilePath));
			bw.write(cnt);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}				
	}
	
}
