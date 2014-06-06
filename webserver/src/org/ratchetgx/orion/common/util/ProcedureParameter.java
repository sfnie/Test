package org.ratchetgx.orion.common.util;

public class ProcedureParameter {
	
	/** 参数方向 */
	private ProcedureParameterDirection parameterDirection; 
	
	/** 参数数据类型 */
	private ProcedureParameterDataType parameterDataType; 
	
	/** 参数名称 */
	private String parameterName; 
	
	/** 参数值 */
	private Object parameterValue; 

	public ProcedureParameter(ProcedureParameterDirection parameterDirection,
			ProcedureParameterDataType parameterDataType, String parameterName,
			Object parameterValue) {
		this.parameterDirection = parameterDirection;
		this.parameterDataType = parameterDataType;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}

	public ProcedureParameterDirection getDirection() {
		return parameterDirection;
	}

	public ProcedureParameterDataType getDataType() {
		return parameterDataType;
	}

	public String getName() {
		return parameterName;
	}

	public Object getValue() {
		return parameterValue;
	}

	public void setDirection(
			ProcedureParameterDirection parameterDirection) {
		this.parameterDirection = parameterDirection;
	}

	public void setDataType(ProcedureParameterDataType parameterDataType) {
		this.parameterDataType = parameterDataType;
	}

	public void setName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setValue(Object parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(parameterDirection);
		sb.append(";");
		sb.append(parameterDataType);
		sb.append(";");
		sb.append(parameterName);
		sb.append(";");
		sb.append(parameterValue);
		return sb.toString();
	}

}
