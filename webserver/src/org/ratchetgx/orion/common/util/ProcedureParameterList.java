package org.ratchetgx.orion.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储过程参数列表类
 * @author sun
 *
 */
public class ProcedureParameterList {

	static final long serialVersionUID = 1L; 

	private List<ProcedureParameter> parameterList = new ArrayList<ProcedureParameter>();
	
	public ProcedureParameterList(){
		
	}

	/**
	 * 添加存储过程参数
	 * @param obj1 DIRECTION
	 * @param obj2 DATATYPE
	 * @param obj3 DATANAME
	 * @param obj4 DATAVALUE
	 */
	public void add(ProcedureParameterDirection direction, ProcedureParameterDataType dataType, 
			        String dataName, Object dataValue){
		ProcedureParameter p = new ProcedureParameter(direction, dataType, dataName, dataValue);
		parameterList.add(p);
	}
	
	/**
	 * @return
	 */
	public int size(){
		return parameterList.size();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public ProcedureParameter getParameter(int index){
		return parameterList.get(index);
	}

	/**
	 * 获取对应的参数值
	 * @param index
	 * @return
	 */
	public Object getParamterValue(int index){
		ProcedureParameter pp = parameterList.get(index);
		return pp.getValue();
	}
}