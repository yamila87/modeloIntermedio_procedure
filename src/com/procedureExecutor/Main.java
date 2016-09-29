package com.procedureExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;

import com.dbUtils.DBconnector;
import com.dbUtils.ProcedureCaller;
import com.manifestUtils.JManifest;
import com.manifestUtils.JSManifestItems;
import com.manifestUtils.ManifestParser;
import com.utils.CNTManager;
import com.utils.CSVreader;
import com.utils.GZunzipper;
import com.utils.GroupJsonUtils;


public class Main {
	final static Logger logger = Logger.getLogger(Main.class);
	final static Logger loggerTime = Logger.getLogger("time");
	
	private static ProcedureCaller caller;
	private static DBconnector connector;
	private static GZunzipper unzziper;
	private static CSVreader reader;
	private static ManifestParser parser;
	private static GroupJsonUtils groupUtil;
	
	public static String CfgPath="config.json";
	
	public static void main(String[] args) {
		 PropertyConfigurator.configure("logconfig.properties");
		 logger.info("Iniciando proceso..");

		 if(args.length>0){
			 CfgPath = args[0];
		 }
		 
		 
		if(Configuration.getInstance()!=null){

			groupUtil = new GroupJsonUtils();
			caller = new ProcedureCaller();
			connector = new DBconnector();
			unzziper = new GZunzipper();
			reader = new CSVreader();
			parser = new ManifestParser();
		
			CNTManager.getInstance().readCnt();			
			logger.info("logid: " + CNTManager.getInstance().getCnt());
			groupUtil.loadGroupJson();

			loadManifests();

			System.exit(0); 
		}
		else {
			logger.fatal("Error al cargar configuracion");
			System.exit(-1);
		}		
	}

	
	private static void loadManifest(){
		logger.info("Obteniendo manifests...");
		
		String manifesetNext = "h_o_logId"+String.valueOf( CNTManager.getInstance().getCnt())+"_manifest.json"; 
		File manifestFile = new File(Configuration.getInstance().getGzPath()+File.separator+manifesetNext);
				
		if(manifestFile.exists()){
			JManifest manifest=null;
			while(manifestFile.exists()){
				logger.info("Leyendo archivo: " +manifestFile.getName());
	
				manifest = parser.getJSONManifest(manifestFile.getPath());		
				if(startLoadProcesStream(manifest)){
					logger.info("finalizado sin errores para logid:"+CNTManager.getInstance().getCnt());
					
					CNTManager.getInstance().updateCnt();
					manifesetNext = "h_o_logId"+String.valueOf( CNTManager.getInstance().getCnt())+"_manifest.json"; 
					manifestFile = new File(Configuration.getInstance().getGzPath()+File.separator+manifesetNext);
				}else{
					logger.error("finalizado con errores para logid: "+CNTManager.getInstance().getCnt());
					break;
				}
			}

		}else{
			logger.info("No se encontro manifest para el logid: " +  CNTManager.getInstance().getCnt());
			logger.debug("manifest buscado: " + manifestFile.getAbsolutePath());
		}		
	}
	
	
	
	
	private static void loadManifests(){
		logger.info("Obteniendo manifests...");
		
		
		File[] list=Configuration.getInstance().getGzPathFile().listFiles(new FileFilter(){
			public boolean accept (File file){
				if(file.isFile() && file.getName().endsWith(".json")){
					return true;
				}else{
					return false;
				}
			}
		});
		
		Collections.sort(Arrays.asList(list),new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return new Long(o1.lastModified()).compareTo(new Long(o2.lastModified()));
			}
			});
		
		JManifest manifest=null;
		
		for(File manifestFile : list){
			logger.info("Leyendo archivo: " +manifestFile.getName());
			
			manifest = parser.getJSONManifest(manifestFile.getPath());		
			
			if(manifest.getLogIdMin()==CNTManager.getInstance().getCnt()){
				
				if(startLoadProcesStream(manifest)){
					logger.info("finalizado con exito para logid:"+manifest.getLogIdMax());
					
					CNTManager.getInstance().updateCnt(manifest.getLogIdMax());
				}else{
					logger.error("finalizado con errores para logid: "+manifest.getLogIdMax());
					break;
				}
			}
		}
	}

	
	private static boolean startLoadProcess (JManifest manifest){
		boolean result= true;	
		Map<String, JSManifestItems> map = manifest.getFiles();

		logger.info("Cantidad de archivos a procesar: " + map.size());
		Connection conn =null;		

		String procName="";
		String gzName="";
		String key="";
		String name="";
		String gzPath = "";
		String outPath ="";

		long beforeExe = 0;
		long afterExe = 0;		
		long beforeManifest = 0;
		long afterManifest = 0;	
		long beforeCsv = 0;
		long afterCsv=0;
		ArrayList<Long> promedioPorRegistros = new ArrayList<Long>();

		int qtyReg = 0;
		try{
			logger.info("Obteniendo conexion a: " + Configuration.getInstance().getURL());

			conn= connector.getConnection();
			if(conn!=null){
				conn.setAutoCommit(false);
				beforeManifest =  java.lang.System.currentTimeMillis();

				for(Entry <String, JSManifestItems> entry : map.entrySet()){
					key = entry.getKey();
					name=key.split("\\.")[0];

					logger.info("Procesando archivo para: "+name + " , cantidad de registros:"+entry.getValue().getCnt());
					boolean cols =false;

					if(Configuration.getInstance().getCustomProcedures().contains(name)){
						logger.debug("Usando custom package");
						procName = Configuration.getInstance().getCustomPackageName()+name;
					}else{					
						procName = Configuration.getInstance().getPackageName()+name;
					}

					gzName = map.get(key).getFname();
					gzPath = Configuration.getInstance().getGzPath()+File.separator + gzName;
					outPath = Configuration.getInstance().getTmpPath()+File.separator+gzName.replace(".gz", "");

					int qtyParams = 0;
					qtyReg = 0;

					if(unzziper.gunzipGZ(gzPath, outPath )){
						loggerTime.info("csv para: " + procName);
						beforeCsv =  java.lang.System.currentTimeMillis();

						reader.openFile(outPath);							
						int groupById = -1;
						String groupByType ="";

						ArrayList<String[]> array = reader.readLines();
						while(array.size()>0){
							if(array.size()>=1){						
								if(!cols){
									qtyParams = array.get(0).length; 
									logger.debug("Columnas encontradas: " + qtyParams);
									groupById= groupUtil.getGroupBy(name,array.get(0));
									groupByType = groupUtil.getResultType(name);	
									logger.debug("Agrupa por: " + groupById +" tipo:" + groupByType);									
									caller.setProcedureName(procName);
									caller.setProcedureStringCaller(conn,qtyParams);
									array.remove(0);

									cols=true;
								}

								logger.trace("Ejecutando procedure");									
								beforeExe =  java.lang.System.currentTimeMillis();								
								caller.executeProcedure(conn, array,groupById,groupByType);									
								afterExe =  java.lang.System.currentTimeMillis();									
								promedioPorRegistros.add(afterExe-beforeExe);	
								
								conn.commit();
								qtyReg+=caller.getRegQty();
								
								logger.info("insertados hasta el momento:" + qtyReg);
								
								
								array = reader.readLines();
								
							}else{
								logger.warn("Archivo vacio: " + outPath);
								break;
							}
						}		

						afterCsv = java.lang.System.currentTimeMillis();

						logger.info("Cantidad de registros procesados :"+ qtyReg);
						long resultAcum = 0 ;

						for(Long l : promedioPorRegistros){
							resultAcum+=l;
						}

						if(caller.getRegQty()>0){
							long resultPromedio = resultAcum / caller.getRegQty();	
							loggerTime.info("Promedio por cada "+Configuration.getInstance().getMaxLines()+": " +resultPromedio );
							loggerTime.info("Tiempo para csv "+name+":" + (afterCsv -beforeCsv));
						}	
						reader.closeFile();																
						new File(outPath).delete();							
					}else{
						logger.error("Error al descomprimir archivo: " + gzName);
						result=false;
					}						
				}

				afterManifest = java.lang.System.currentTimeMillis();
				loggerTime.info("Tiempo para manifest:" + (afterManifest-beforeManifest));				
			}
		}catch(IOException e){
			logger.error("Error al leer el archivo :" +outPath);
			result = false;
		}
		catch(SQLException e){
			result = false;
			logger.error("Error al ejecutar el proceso", e);
			logger.error("Ultimo procedure: " + procName +", cantidad de registros insertados: " + qtyReg);			
			logger.warn("Realizando rollback...");
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("error al realizar el rollback" ,e);
			}
			
		}catch(Exception e){
			result = false;
			logger.error("Error para: " + name ,e);
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
	
	
	
	private static boolean startLoadProcesStream (JManifest manifest){
		boolean result= true;	
		Map<String, JSManifestItems> map = manifest.getFiles();

		logger.info("Cantidad de archivos a procesar: " + map.size());
		Connection conn =null;		

		String procName="";
		String gzName="";
		String key="";
		String name="";
		String gzPath = "";
		String outPath ="";

		long beforeExe = 0;
		long afterExe = 0;		
		long beforeManifest = 0;
		long afterManifest = 0;	
		long beforeCsv = 0;
		long afterCsv=0;
		ArrayList<Long> promedioPorRegistros = new ArrayList<Long>();

		int qtyReg = 0;
		try{
			logger.info("Obteniendo conexion a: " + Configuration.getInstance().getURL());

			conn= connector.getConnection();
		
			if(conn!=null){
				conn.setAutoCommit(false);
				beforeManifest =  java.lang.System.currentTimeMillis();

				for(Entry <String, JSManifestItems> entry : map.entrySet()){
					key = entry.getKey();
					name=key.split("\\.")[0];

					logger.info("Procesando archivo para: "+name + " , cantidad de registros:"+entry.getValue().getCnt());
					boolean cols =false;

					if(Configuration.getInstance().getCustomProcedures().contains(name)){
						logger.debug("Usando custom package");
						procName = Configuration.getInstance().getCustomPackageName()+name;
					}else{					
						procName = Configuration.getInstance().getPackageName()+name;
					}

					gzName = map.get(key).getFname();
					gzPath = Configuration.getInstance().getGzPath()+File.separator + gzName;
				
					int qtyParams = 0;
					qtyReg = 0;

					
					
					if(new File(gzPath).exists()){
						loggerTime.info("csv para: " + procName);
						BufferedReader br = unzziper.getGZIPStream(gzPath);
						beforeCsv =  java.lang.System.currentTimeMillis();
						
						int groupById = -1;
						String groupByType ="";

						ArrayList<String[]> array = reader.readLines(br);
						while(array.size()>0){
							if(array.size()>=1){						
								if(!cols){
									qtyParams = array.get(0).length; 
									logger.debug("Columnas encontradas: " + qtyParams);
									groupById= groupUtil.getGroupBy(name,array.get(0));
									groupByType = groupUtil.getResultType(name);	
									logger.debug("Agrupa por: " + groupById +" tipo:" + groupByType);									
									caller.setProcedureName(procName);
									caller.setProcedureStringCaller(conn,qtyParams);
									array.remove(0);

									cols=true;
								}

								logger.trace("Ejecutando procedure");									
								beforeExe =  java.lang.System.currentTimeMillis();								
								caller.executeProcedure(conn, array,groupById,groupByType);									
								afterExe =  java.lang.System.currentTimeMillis();									
								promedioPorRegistros.add(afterExe-beforeExe);	
								
								conn.commit();
								qtyReg+=caller.getRegQty();
								
								logger.info("insertados hasta el momento:" + qtyReg);
								
								
								array = reader.readLines(br);
								
							}else{
								logger.warn("Archivo vacio: " + outPath);
								break;
							}
						}		

						afterCsv = java.lang.System.currentTimeMillis();

						logger.info("Cantidad de registros procesados :"+ qtyReg);
						long resultAcum = 0 ;

						for(Long l : promedioPorRegistros){
							resultAcum+=l;
						}

						if(caller.getRegQty()>0){
							long resultPromedio = resultAcum / caller.getRegQty();	
							loggerTime.info("Promedio por cada "+Configuration.getInstance().getMaxLines()+": " +resultPromedio );
							loggerTime.info("Tiempo para csv "+name+":" + (afterCsv -beforeCsv));
						}	
						
						unzziper.closeGzipStream(br);
						
						//new File(outPath).delete();							
					}else{
						logger.error("Error al descomprimir archivo: " + gzName);
						result=false;
					}						
				}

				afterManifest = java.lang.System.currentTimeMillis();
				loggerTime.info("Tiempo para manifest:" + (afterManifest-beforeManifest));				
			}
		}catch(IOException e){
			logger.error("Error al leer el archivo :" +outPath);
			result = false;
		}
		catch(SQLException e){
			result = false;
			logger.error("Error al ejecutar el proceso", e);
			logger.error("Ultimo procedure: " + procName +", cantidad de registros insertados: " + qtyReg);			
			logger.warn("Realizando rollback...");
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error("error al realizar el rollback" ,e);
			}
			
		}catch(Exception e){
			result = false;
			logger.error("Error para: " + name ,e);
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
	
	
	
	
}
