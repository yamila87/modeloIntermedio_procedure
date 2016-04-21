package DB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProcedureCaller {
	
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

		call = procedureStringCall.toString();
	}
	
	
	public boolean executeProcedure (Connection conn , ArrayList<String[]> arrayColsByReg){
		boolean result = false;
		
		try {
			callableStatement = conn.prepareCall(call);
			
			for(int i=0;i<arrayColsByReg.size();i++){
		
				String[] reg = arrayColsByReg.get(i);	
				System.out.println("AGREGA REG n:." +i);
				
				for(int j=0 ; j<reg.length;j++){
					System.out.println("PARAM:" + j +" Val: " + reg[j]);
					callableStatement.setString(j+1,reg[j]);
					
				}
				
				callableStatement.addBatch();
			}
			
			System.out.println("EJECUTANDO PROC.");
			callableStatement.executeBatch();
			
			result=true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
			if(callableStatement!=null){
				try {
					System.out.println("CIERRA PROC.");
					callableStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
		return result;		
	}
	
	
}
