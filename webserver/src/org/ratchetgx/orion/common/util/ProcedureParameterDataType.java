package org.ratchetgx.orion.common.util;

public enum ProcedureParameterDataType {
	INT("整数型"), // 整数
	STRING("字符串型"), // 字符串
	CALENDAR("日期型"), // 日期
	FLOAT("浮点数型"), // 浮点数
	@Deprecated RESULTSET("数据集型"), // 数据集
	HASHMAPLIST("数据集List<Map>型");

	ProcedureParameterDataType(String desc) {
		this.desc = desc;
	}

	private String desc;

	@Override
	public String toString() {
		return "参数类型:" + desc;
	}

}
