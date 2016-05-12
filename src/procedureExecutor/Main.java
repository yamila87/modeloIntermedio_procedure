package procedureExecutor;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;

import dbUtils.DBconnector;
import dbUtils.ProcedureCaller;
import utils.CNTManager;
import utils.CSVreader;
import utils.GZunzipper;
import manifestUtils.JManifest;
import manifestUtils.JSManifestItems;
import manifestUtils.ManifestParser;


public class Main {
	final static Logger logger = Logger.getLogger(Main.class);

	private static ProcedureCaller caller;
	private static DBconnector connector;
	private static GZunzipper unzziper;
	private static CSVreader reader;
	private static ManifestParser parser;
	private static boolean error=false;

	
	public static void main(String[] args) {
		 PropertyConfigurator.configure("logconfig.properties");
		 logger.info("Iniciando proceso..");
		 
		if(Configuration.getInstance()!=null){

			caller = new ProcedureCaller();
			connector = new DBconnector();
			unzziper = new GZunzipper();
			reader = new CSVreader();
			parser = new ManifestParser();
		
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

			JManifest manifest = parser.getJSONManifest(path);

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
		{
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
		
		LinkedList<String> list = new LinkedList<String>();
		
		
		for (Entry <String, JSManifestItems> entry : map.entrySet()){
			list.add(entry.getKey());
		}
		
		int size = list.size();
		
		String procName="";
		String gzName="";
		String key="";
		try{
			logger.debug("Obteniendo conexion...");
			
			conn= connector.getConnection();
			conn.setAutoCommit(false);
			
			for(int i=size-1 ; i>=0 ; i--){
		//	for(Entry <String, JSManifestItems> entry : map.entrySet()){
				key = list.get(i);
			
				
				procName = Configuration.getInstance().getPackageName()+key.split("\\.")[0].toLowerCase();
				gzName = map.get(key).getFname();
				
				logger.trace("Procesando: " + gzName);
				logger.debug("ProcedureName:"+procName);
				
				String gzPath = Configuration.getInstance().getGzPath()+File.separator + gzName;
				String outPath = Configuration.getInstance().getTmpPath()+File.separator+gzName.replace(".gz", "");
				
				if(unzziper.gunzipGZ(gzPath, outPath )){
					ArrayList<String[]> array = reader.getParsedContent(outPath);
					logger.trace("Registros encontrados: " + array.size());
					
					if(array.size()>=1){
				
						int qtyParams = array.get(0).length;
						logger.trace("Columnas encontradas: " + qtyParams);
						
						caller.setProcedureName(procName);
						caller.setProcedureStringCaller(qtyParams);
						
						logger.trace("Ejecutando procedure");
						caller.executeProcedure(conn, array);	
						
					}else{
						logger.warn("Archivo vacio: " + gzName);
					}

					result=true;			
				}else{
					logger.error("Error al descomprimir archivo: " + gzName);
				}	
				
				new File(outPath).delete();
			}
			
			conn.commit();
			logger.trace("Commit, manifest");
				
		}catch(SQLException e){
			logger.error("Error al procesar manifest", e);
			logger.warn("Realizando rollback...");
			result = false;
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.fatal("Error al realizar el rollback".toUpperCase(),e1);
				//TODO y si llega aca ???
			}
			
		}catch(Exception e){
			result = false;
			logger.error("Error en: " + procName+" ,Archivo: "+gzName,e);
		}
		finally{
			caller.closeCallableStatement();
			logger.trace("cerrando conexion...");
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
			if (lowercaseName.endsWith("manifest.json")) {
				return true;
			} else {
				return false;
			}
		}
	};
	
	
}
