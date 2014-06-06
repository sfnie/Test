package org.ratchetgx.orion.common.util;

public enum ProcedureParameterDirection {

	IN("输入"), OUT("输出");

	ProcedureParameterDirection(String desc) {
		this.desc = desc;
	}

	private String desc;

	@Override
	public String toString() {
		return "参数方向:" + desc;
	}
}
