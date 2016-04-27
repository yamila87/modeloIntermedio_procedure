package gestor;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;

import utils.CNTManager;
import utils.CSVreader;
import utils.GZunzipper;
import manifest.JManifest;
import manifest.JSManifestItems;
import manifest.ManifestParser;
import DB.DBconnector;
import DB.ProcedureCaller;


public class Main {
	final static Logger logger = Logger.getLogger(Main.class);

	private static ProcedureCaller caller;
	private static DBconnector connector;
//	private static int cnt;
	private static boolean error=false;

	
	public static void main(String[] args) {
		 PropertyConfigurator.configure("logconfig.properties");
		 logger.info("Iniciando proceso..");
		 
		if(Configuration.getInstance()!=null){

			caller = new ProcedureCaller();
			connector = new DBconnector();
		
			CNTManager.getInstance().readCnt();
			
			logger.info("Ultimo logid guardado: " + CNTManager.getInstance().getCnt());
			getManifests();
			
			if(error){			
				logger.info("Finalizado con errores. Ultimo logid: " +  CNTManager.getInstance().getCnt());
			}
			else{
				logger.info("Finalizado con exito. Ultimo logid: " +  CNTManager.getInstance().getCnt());
			}
			System.exit(0);
		}
		else {
			logger.fatal("Error al cargar configuracion");
			System.exit(-1);
		}
		
	}

   
	private static void getManifests (){

		String manifestStr=existFile();
		while (manifestStr!=null && !error){
			String path = Configuration.getInstance().getGzPath()+File.separator+manifestStr;			

			logger.info("Leyendo archivo: " + manifestStr);

			JManifest manifest = ManifestParser.getJSONManifest(path);

			logger.trace(manifestStr+": "+manifest.toString());

			if(loadProcess(manifest)){
				 CNTManager.getInstance().updateCnt();
				manifestStr=existFile();
			}else{
				logger.error("Error al procesar, ultimo LOGID: "+ CNTManager.getInstance().getCnt()+" ,Archivo:"+ manifestStr);
				error=true;
				break;
			} 				
		}
	}
	
	private static String existFile (){
		String [] manifestFiles = Configuration.getInstance().getGzPathFile().list(jsonFilter);
		for(int i = 0 ; i<manifestFiles.length;i++)
		{ //"logid"+cnt+"_
			String regex="logid"+String.valueOf( CNTManager.getInstance().getCnt())+"_";
			if(manifestFiles[i].toLowerCase().contains(regex)){
				return manifestFiles[i];
			}
		}
		
		return null;
	}
	
	private static boolean loadProcess (JManifest manifest){
		boolean result= false;	
		Map<String, JSManifestItems> map = manifest.getFiles();
		
		Connection conn =null;
		
		String procName="";
		String gzName="";
		
		try{
			logger.debug("Obteniendo conexion...");
			
			conn= connector.getConnection();
			conn.setAutoCommit(false);
			
			for(Entry <String, JSManifestItems> entry : map.entrySet()){
				
				procName = Configuration.getInstance().getPackageName()+entry.getKey().split("\\.")[0].toLowerCase();
				gzName = entry.getValue().getFname();
				
				logger.trace("Procesando: " + gzName);
				logger.debug("ProcedureName:"+procName);
				
				String gzPath = Configuration.getInstance().getGzPath()+File.separator + gzName;
				String outPath = Configuration.getInstance().getTmpPath()+File.separator+gzName.replace(".gz", "");
				
				if(GZunzipper.gunzipGZ(gzPath, outPath )){
					ArrayList<String[]> array = CSVreader.getParsedContent(outPath);
					logger.trace("Registros encontrados: " + array.size());
					
					if(array.size()>=1){
				
						int qtyParams = array.get(0).length;
						logger.trace("Columnas encontradas: " + qtyParams);
						
						caller.setProcedureName(procName);
						caller.setProcedureStringCaller(qtyParams);
						
						logger.trace("Ejecutando procedure");
						caller.executeProcedure(conn, array);	
						
						conn.commit();
						logger.trace("Commit , procedure: " + procName);
					}else{
						logger.warn("Archivo vacio: " + gzName);
					}

					result=true;			
				}else{
					logger.error("Error al descomprimir archivo: " + gzName);
				}	
				
				new File(outPath).delete();
			}
				
		}catch(SQLException e){
			logger.error("Error al ejecutar: " + procName , e);
			logger.warn("Realizando rollback...");
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.fatal("Error al realizar el rollback".toUpperCase(),e1);
				//TODO y si llega aca ???
			}
			
		}catch(Exception e){
			logger.error("Error en: " + procName+" ,Archivo: "+gzName);
		}
		finally{
			caller.closeCallableStatement();
			
			if(conn!=null){
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}	
		return result;
	}
	
	

	
	private static FilenameFilter jsonFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			//if (lowercaseName.endsWith("logid"+cnt+"_manifest.json")) {
			if (lowercaseName.endsWith("manifest.json")) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	
}
