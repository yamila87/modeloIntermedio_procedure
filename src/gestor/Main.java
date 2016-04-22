package gestor;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import utils.CNTManager;
import utils.CSVreader;
import utils.GZunzipper;
import manifest.JManifest;
import manifest.JSManifestItems;
import manifest.ManifestParser;
import DB.DBconnector;
import DB.ProcedureCaller;


public class Main {

	//private static Configuration conf;
	private static GZunzipper unzipper;
	private static CSVreader reader;
	private static ProcedureCaller caller;
	private static DBconnector connector;
	private static ManifestParser manifestParser;
	private static CNTManager cntManager;
	
	private static FilenameFilter jsonFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.endsWith(".json")) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	
	public static void main(String[] args) {
		loadConfiguration();
		
		reader = new CSVreader();
		caller = new ProcedureCaller();
		unzipper = new GZunzipper();
		manifestParser = new ManifestParser();
		connector = new DBconnector();
		cntManager = new CNTManager();
		
		//leer el cnt antes de los manifest 
		
		String [] manifests = getManifests();
		
		for(String manifestStr : manifests){
			String path = Configuration.getInstance().getGzPath()+File.separator+manifestStr;			
			JManifest manifest = manifestParser.getJSONManifest(path);
			
			System.out.println(manifest.toString());
			metodoQueHaceTodo(manifest); //TODO ponerle un nombre mas feliz :p			
		}
		
	}

	private static void loadConfiguration (){
		Configuration.getInstance().setValues();
	}
	
   
	private static String[] getManifests (){
		String [] manifestFiles = Configuration.getInstance().getGzPathFile().list(jsonFilter);
		return manifestFiles;
	}
	
	private static void metodoQueHaceTodo (JManifest manifest){
		Map<String, JSManifestItems> map = manifest.getFiles();
		
		Connection conn =null;
		try{				
			conn= connector.getConnection();
			conn.setAutoCommit(false);
			
			for(Entry <String, JSManifestItems> entry : map.entrySet()){
				String procName = Configuration.getInstance().getPackageName()+entry.getKey().split("\\.")[0].toLowerCase();
				String gzName = entry.getValue().getFname();
				
				String gzPath = Configuration.getInstance().getGzPath()+File.separator + gzName;
				String outPath = Configuration.getInstance().getTmpPath()+File.separator+gzName.replace(".gz", "");
				
				if(unzipper.gunzipGZ(gzPath, outPath )){
					ArrayList<String[]> array = reader.getParsedContent(outPath);
					if(array.size()>=1){
				
						int qtyParams = array.get(0).length;					
						caller.setProcedureName(procName);
						caller.setProcedureStringCaller(qtyParams);
						caller.executeProcedure(conn, array);	
					}	
				}				
			}
			
			conn.commit();
			
		}catch(SQLException e){
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}finally{
			caller.closeCallableStatement();
			
			if(conn!=null){
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
		
	}	
}
