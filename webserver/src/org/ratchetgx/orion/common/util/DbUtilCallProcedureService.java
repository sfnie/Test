package org.ratchetgx.orion.common.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import oracle.jdbc.OracleTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component(value = "dbUtilCallProcedureService")
public class DbUtilCallProcedureService  {

	private static Logger log = LoggerFactory
			.getLogger(DbUtilCallProcedureService.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DbUtil dbUtil;

	public int execProucedure(String procedureName,
								ProcedureParameterList ppl, IProcedureParameterProcessor processor)
								throws Exception {
		
		log.debug("开始调用存储过程" + procedureName + ":");
		
		long startTime = System.currentTimeMillis();
		int bSucc = 0;		/** 调用存储过程成功与否标识 0：失败 1：成功 */
		try {
			/** 判断参数列表中的参数类型、参数数据类型、传入的参数 */
			boolean checked = checkParameter(procedureName, ppl, processor);
			if (!checked) {
				log.error("输入参数不合法，调用存储过程失败:" + procedureName);
				return bSucc;
			}
			bSucc = callProcedure(procedureName, ppl, processor);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("存储过程" + procedureName + "执行耗时" + (System.currentTimeMillis() - startTime) + "ms");
		return bSucc;
	}

	/**
	 * 调用存储过程
	 * 
	 * @param callSql
	 * @param ppl
	 * @param processor
	 */
	private int callProcedure(
			final String procedureName,
			final ProcedureParameterList ppl,
			final IProcedureParameterProcessor processor) {

		final String callSql = getProcedureCallSql(procedureName, ppl);
		log.debug("开始将存储过程" + callSql + "提交至数据库:");
		
		//执行存储过程成功与否标识
		int successFlag = 1;
		jdbcTemplate.execute(new CallableStatementCreator() {

			public CallableStatement createCallableStatement(Connection conn) throws SQLException {
				
				CallableStatement cstmt = conn.prepareCall(callSql);
				for (int i = 0; i < ppl.size(); i++) {
					ProcedureParameter pp = ppl.getParameter(i);
					
					/** 处理输入参数 */
					if (pp.getDirection() == ProcedureParameterDirection.IN) {
						
						if (pp.getDataType() == ProcedureParameterDataType.INT) {
							cstmt.setInt(i + 1, (Integer)pp.getValue());
						} else if (pp.getDataType() == ProcedureParameterDataType.STRING) {
							cstmt.setString(i + 1, (String)pp.getValue());
						} else if (pp.getDataType() == ProcedureParameterDataType.CALENDAR) {
							Date date = (Date)pp.getValue();
							cstmt.setTimestamp(i + 1, new Timestamp(date.getTime()));
						} else if (pp.getDataType() == ProcedureParameterDataType.FLOAT) {
							cstmt.setDouble(i + 1, (Double)pp.getValue());
						} else {
							String errorMsg = "输入参数\"" + pp.getValue() + "\"类型错误。";
							log.error(errorMsg);
							throw new SQLException(errorMsg);
						}
						
						/** 打印日志 */
						log.debug("输入参数" + i + "-" + pp.getName() + ":" + pp.getDataType() + ":" + pp.getValue());
					}

					/** 处理输出参数 */
					if (pp.getDirection() == ProcedureParameterDirection.OUT) {
						if (pp.getDataType() == ProcedureParameterDataType.INT){
							cstmt.registerOutParameter(i + 1, Types.INTEGER);
						} else if (pp.getDataType() == ProcedureParameterDataType.STRING) {
							cstmt.registerOutParameter(i + 1, Types.VARCHAR);
						} else if (pp.getDataType() == ProcedureParameterDataType.CALENDAR) {
							cstmt.registerOutParameter(i + 1, Types.TIMESTAMP);
						} else if (pp.getDataType() ==  ProcedureParameterDataType.FLOAT) {
							cstmt.registerOutParameter(i + 1, Types.NUMERIC);
						} else if (pp.getDataType() == ProcedureParameterDataType.RESULTSET) {
							cstmt.registerOutParameter(i + 1, OracleTypes.CURSOR);
						} else if (pp.getDataType() == ProcedureParameterDataType.HASHMAPLIST) {
							cstmt.registerOutParameter(i + 1, OracleTypes.CURSOR);
						} else {
							String errorMsg = "输出参数\"" + pp.getName() + "\"类型错误。";
							log.error(errorMsg);
							throw new SQLException(errorMsg);
						}
						
						/** 打印日志 */
						log.debug("输出参数" + i + "-" + pp.getName() + ":" + pp.getDataType());
					}
				}
				return cstmt;
			}
			
		}, new CallableStatementCallback<Object>() {
			
			public Object doInCallableStatement(CallableStatement cstmt) throws SQLException, DataAccessException {
				
				Boolean result = cstmt.execute();
				log.debug("存储过程执行完毕！输出参数:");
				for (int i = 0; i < ppl.size(); i++) {
					ProcedureParameter pp = ppl.getParameter(i);
					
					/** 处理输出参数 */
					if (pp.getDirection() ==  ProcedureParameterDirection.OUT) {
						if (pp.getDataType() == ProcedureParameterDataType.INT) {
							pp.setValue(cstmt.getInt(i + 1));
						}
						if (pp.getDataType() == ProcedureParameterDataType.STRING) {
							pp.setValue(cstmt.getString(i + 1));
						}
						if (pp.getDataType() == ProcedureParameterDataType.CALENDAR) {
							pp.setValue(cstmt.getDate(i + 1));
						}
						if (pp.getDataType() == ProcedureParameterDataType.FLOAT) {
							pp.setValue(cstmt.getDouble(i + 1));
						}
						if (pp.getDataType() == ProcedureParameterDataType.RESULTSET) {
							ResultSet rs = (ResultSet) cstmt.getObject(i + 1);
							pp.setValue(rs);
						}
						if (pp.getDataType() == ProcedureParameterDataType.HASHMAPLIST) {
							ResultSet rs = (ResultSet) cstmt.getObject(i + 1);
							pp.setValue(dbUtil.resultSet2ListToUpperCase(rs));
							rs.close();
						}
						
						/** 打印日志 */
						String logStr = "输出参数" + i + "-" + pp.getName() + ":" + pp.getDataType();
						if (pp.getDataType() != ProcedureParameterDataType.RESULTSET 
								&& pp.getDataType() != ProcedureParameterDataType.HASHMAPLIST) {
							logStr += ":" + pp.getValue();
						}
						log.debug(logStr);
					}
				}
				
				/** 执行用于自定义的扩展接口 */
				if (processor != null) {
					processor.process(ppl);
				}
				return result;
			}
			
		});
		//如果没有抛出错误，则说明调用存储过程成功
		return successFlag;
	}

	/**
	 * 检查输入参数的合法性
	 * @param ppl
	 * @throws Exception
	 */
	private boolean checkParameter(String procedureName,
								ProcedureParameterList ppl, IProcedureParameterProcessor processor) 
							throws Exception {
		boolean checked = true;
		
		log.debug("开始检查输入参数的合法性:");
		/** 校验参数列表中的参数类型、参数数据类型、传入的参数的合法性 */
		for (int i = 0; i < ppl.size(); i++) {
			ProcedureParameter pp = ppl.getParameter(i);
					
			if (pp.getDirection() == ProcedureParameterDirection.IN) {
				
				String logStr = "";
				
				if (pp.getName() == null) {
					logStr = " => 参数名称未正确指定!";
					checked = false;
				}
				
				if (pp.getDataType() == null) {
					logStr = " => 参数类型未正确指定!";
					checked = false;
				}
				
				if (pp.getValue() == null) {
					logStr = " => 传入的参数值不可为null!";
					checked = false;
				}
				
				/** 整型 */
				if (pp.getDataType() == ProcedureParameterDataType.INT) {

					if (!"java.lang.Integer".equals(pp.getValue().getClass().getName())) { 
						logStr = ":参数值的类型"
								+ pp.getValue().getClass().getName()
								+ " => 参数值类型不匹配!";
						checked = false;
					} else {
						logStr = ":参数值的类型" + pp.getValue().getClass().getName();
					}
				}
				
				/** 字符串型 */
				if (pp.getDataType()== ProcedureParameterDataType.STRING) {
					if (!"java.lang.String".equals(pp.getValue().getClass().getName())) { 
						logStr = ":参数值的类型"
								+ pp.getValue().getClass().getName()
								+ " => 参数值类型不匹配!";
						checked = false;
					} else {
						logStr = ":参数值的类型" + pp.getValue().getClass().getName();
					}
				}
				
				/** 打印日志 */
				log.debug("输入参数" + i + "-" + pp.getName() + ":" + pp.getDataType() + ":" + pp.getValue() + logStr);
			}
		}
		log.debug("输入参数的合法性检查完毕！检查结果为：" + checked);

		return checked;
	}

	/**
	 * 根据存储过程名称,拼装调用存储过程的语句
	 * 
	 * @param ProcedureName
	 * @param ppl
	 * @return
	 */
	private String getProcedureCallSql(String ProcedureName,
			ProcedureParameterList ppl) {

		String callName = "{call " + ProcedureName + "(";
		for (int i = 0; i < ppl.size(); i++) {
			if (i == ppl.size() - 1)
				callName += "?";
			else
				callName += "?,";
		}
		callName += ")}";

		return callName;
	}
}