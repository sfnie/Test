package org.ratchetgx.orion.common.util;

import java.util.List;

public interface CallProcedureService {

	/**
	 * 执行存储过程
	 * 
	 * @param ProcedureName
	 * @param ps
	 * @param processor
	 * @throws Exception
	 */
	public void execProucedure(String ProcedureName,
			List<ProcedureParameter> ps, IProcedureParameterProcessor processor)
			throws Exception;

	/**
	 * 执行存储过程
	 * 
	 * @param ProcedureName
	 * @param ps
	 * @throws Exception
	 */
	public void execProucedure(String ProcedureName, List<ProcedureParameter> ps)
			throws Exception;
}
