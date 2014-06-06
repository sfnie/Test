package org.ratchetgx.orion.common.util;

import java.sql.SQLException;
import java.util.List;

public interface IProcedureParameterProcessor {
	
	/**
	 * 存储过程执行后处理
	 * 
	 * @param ppl
	 * @throws SQLException
	 */
	public void process(final ProcedureParameterList ppl) throws SQLException;
	
}
