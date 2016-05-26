package dbUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.apache.log4j.Logger;

public class ProcedureCaller {
	final static Logger logger = Logger.getLogger(ProcedureCaller.class);
	
	private CallableStatement callableStatement = null;
	private String procedureName ="";
	private StringBuilder procedureStringCall ;
	private String call;
	private ArrayDescriptor arrDesc;
	private ARRAY array_to_pass ;
	
	public void setProcedureName (String name){
		procedureName = name;
	}
	
	public String getProcedureName (){
		return procedureName;
	}
	
	public void setProcedureStringCaller (Connection conn,int paramsQty) throws SQLException{

		//"{call custom_cv.geohub(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
		procedureStringCall = new StringBuilder();		
		procedureStringCall.append("{call ").append(procedureName).append("(");
		
		for(int i = 0 ; i<paramsQty ; i++){
			
			procedureStringCall.append("?,");
		}
		
		procedureStringCall.deleteCharAt(procedureStringCall.lastIndexOf(","));
		procedureStringCall.append(")}");
		logger.debug("Declaracion: " + procedureStringCall.toString());
		call = procedureStringCall.toString();
		callableStatement = conn.prepareCall(call);

	}
	
	
	public void executeProcedure (Connection conn , ArrayList<String[]> arrayColsByReg, int colGroupBy) throws SQLException  {
	
		//callableStatement.setQueryTimeout(60); //segundos
		
		arrDesc = ArrayDescriptor.createDescriptor("STRINGARRAY", conn);
		String [] strArr = null;

		for(int i=1;i<arrayColsByReg.size();i++){
	//	for(int i=1;i<2;i++){
			String[] reg = arrayColsByReg.get(i);	
			logger.trace("Agrega registro n:." +i);

			for(int j=0 ; j<reg.length;j++){
			if(reg[j].equals("null")){
					reg[j]="";
				}

				if(j==colGroupBy){
					
					logger.trace("PARAM TO ARRAY:" + j +" Val: " + reg[j]);
					
					strArr = reg[j].split(",",-1);
					array_to_pass = new ARRAY(arrDesc,conn,strArr);
					callableStatement.setArray(j+1, array_to_pass);
					
				}else{										
					logger.trace("PARAM:" + j +" Val: " + reg[j]);
									
					callableStatement.setString(j+1,reg[j]);
				}
			}

			logger.trace("Agregando Batch...");
			callableStatement.addBatch();
		}

		logger.debug("Ejecutando proceso " + procedureName);
		callableStatement.executeBatch();
		logger.debug("ejecucion finalizada " + procedureName);
	}
	

	public void closeCallableStatement(){
		if(callableStatement!=null){
			try {
				callableStatement.close();
			} catch (SQLException e) {
				logger.error("Error al cerrar declaracion ",e);
			}
		}
	}
	
}
