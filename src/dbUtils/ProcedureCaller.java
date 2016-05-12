package dbUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class ProcedureCaller {
	final static Logger logger = Logger.getLogger(ProcedureCaller.class);
	
	
	private CallableStatement callableStatement = null;
	private String procedureName ="";
	private StringBuilder procedureStringCall ;
	private String call;
	
	
	public void setProcedureName (String name){
		procedureName = name;
	}
	
	public String getProcedureName (){
		return procedureName;
	}
	
	public void setProcedureStringCaller (int paramsQty){

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
	}
	
	
	public void executeProcedure (Connection conn , ArrayList<String[]> arrayColsByReg) throws SQLException  {
		callableStatement = conn.prepareCall(call);
		callableStatement.setQueryTimeout(10);
		
		for(int i=1;i<arrayColsByReg.size();i++){

			String[] reg = arrayColsByReg.get(i);	
			logger.trace("Agrega registro n:." +i);

			for(int j=0 ; j<reg.length;j++){
				logger.trace("PARAM:" + j +" Val: " + reg[j]);
				callableStatement.setString(j+1,reg[j]);
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
