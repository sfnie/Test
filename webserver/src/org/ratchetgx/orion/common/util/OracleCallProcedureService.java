package org.ratchetgx.orion.common.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import oracle.jdbc.OracleTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("oracleCallProcedureService")
public class OracleCallProcedureService implements CallProcedureService {

	private static Logger log = LoggerFactory
			.getLogger(OracleCallProcedureService.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DbUtil dbUtil;

	public void execProucedure(String procedureName,
			List<ProcedureParameter> ppl) throws Exception {
		try {
			execProucedure(procedureName, ppl, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void execProucedure(String procedureName,
			List<ProcedureParameter> ppl, IProcedureParameterProcessor processor)
			throws Exception {
		log.debug("开始调用存储过程" + procedureName + ":");
		long startTime = System.currentTimeMillis();
		try {
			// 判断参数列表中的参数类型、参数数据类型、传入的参数
			checkParameter(ppl);
			String callSql = getProcedureCallSql(procedureName, ppl);
			log.info(callSql);
			callEpstarProcedure(callSql, ppl, processor);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info(procedureName + "执行耗时"
				+ (System.currentTimeMillis() - startTime) + "ms");
	}

	private void callEpstarProcedure(final String callSql,
			final List<ProcedureParameter> ppl,
			final IProcedureParameterProcessor processor) {

		jdbcTemplate.execute(new CallableStatementCreator() {

			public CallableStatement createCallableStatement(Connection conn)
					throws SQLException {
				CallableStatement cstmt = conn.prepareCall(callSql);

				for (int i = 0; i < ppl.size(); i++) {
					ProcedureParameter pp = ppl.get(i);
					// 处理输入参数
					if (pp.getDirection() == ProcedureParameterDirection.IN) {
						if (pp.getDataType() == ProcedureParameterDataType.INT) {
							cstmt.setInt(i + 1,
									(Integer) pp.getValue());
						} else if (pp.getDataType() == ProcedureParameterDataType.STRING) {
							cstmt.setString(i + 1,
									(String) pp.getValue());
						} else if (pp.getDataType() == ProcedureParameterDataType.CALENDAR) {
							Date date = (Date) pp.getValue();
							cstmt.setTimestamp(i + 1,
									new Timestamp(date.getTime()));
						} else if (pp.getDataType() == ProcedureParameterDataType.FLOAT) {
							cstmt.setDouble(i + 1,
									(Double) pp.getValue());
						} else {
							String errorMsg = "输入参数\"" + pp.getName()
									+ "\"类型错误。";
							log.error(errorMsg);
							throw new SQLException(errorMsg);
						}
					}

					// 处理输出参数
					if (pp.getDirection() == ProcedureParameterDirection.OUT) {
						if (pp.getDataType() == ProcedureParameterDataType.INT) {
							cstmt.registerOutParameter(i + 1, Types.INTEGER);
						} else if (pp.getDataType() == ProcedureParameterDataType.STRING) {
							cstmt.registerOutParameter(i + 1, Types.VARCHAR);
						} else if (pp.getDataType() == ProcedureParameterDataType.CALENDAR) {
							cstmt.registerOutParameter(i + 1, Types.TIMESTAMP);
						} else if (pp.getDataType() == ProcedureParameterDataType.FLOAT) {
							cstmt.registerOutParameter(i + 1, Types.NUMERIC);
						} else if (pp.getDataType() == ProcedureParameterDataType.RESULTSET) {
							cstmt.registerOutParameter(i + 1,
									OracleTypes.CURSOR);
						} else if (pp.getDataType() == ProcedureParameterDataType.HASHMAPLIST) {
							cstmt.registerOutParameter(i + 1,
									OracleTypes.CURSOR);
						} else {
							String errorMsg = "输出参数\"" + pp.getName()
									+ "\"类型错误。";
							log.error(errorMsg);
							throw new SQLException(errorMsg);
						}
					}
				}

				return cstmt;
			}
		}, new CallableStatementCallback<Object>() {
			public Object doInCallableStatement(CallableStatement cstmt)
					throws SQLException, DataAccessException {
				Boolean result = cstmt.execute();
				log.info("存储过程执行完成后参数列表：");
				for (int i = 0; i < ppl.size(); i++) {
					ProcedureParameter pp = ppl.get(i);
					// 处理输出参数
					if (pp.getDirection() == ProcedureParameterDirection.OUT) {
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
							pp.setValue((ResultSet) cstmt
									.getObject(i + 1));
						}
						if (pp.getDataType() == ProcedureParameterDataType.HASHMAPLIST) {
							ResultSet rs = (ResultSet) cstmt.getObject(i + 1);
							pp.setValue(dbUtil.resultSet2ListToUpperCase(rs));
						}
					}

					log.info(i + ":" + pp.toString());
				}

				if (processor != null) {
					//processor.process(ppl);
				}

				return result;
			}
		});

	}

	private void checkParameter(List<ProcedureParameter> ppl) throws Exception {
		log.debug("开始检查输入参数的合法性");
		boolean checked = true;
		StringBuilder sb = new StringBuilder();
		// 判断参数列表中的参数类型、参数数据类型、传入的参数
		for (int i = 0; i < ppl.size(); i++) {
			ProcedureParameter pp = ppl.get(i);
			if (pp.getDirection() == ProcedureParameterDirection.IN) {
				// 整数
				if (pp.getDataType() == ProcedureParameterDataType.INT) {

					if (null == pp.getValue()) {
						sb.append("输入参数" + i + ":" + pp.getName()
								+ " 传入的参数值对象不能为空!\n");
						checked = checked || false;
					} else {
						log.debug("输入参数" + i + ":" + pp.getName()
								+ "=" + pp.getValue()
								+ "是字符串参数,获取参数的类型"
								+ pp.getValue().getClass().getName());
					}

				}
				// 字符串
				if (pp.getDataType() == ProcedureParameterDataType.STRING) {
					if (null == pp.getValue()) {
						sb.append("输入参数" + i + ":" + pp.getName()
								+ " 传入的参数值对象不能为空!\n");
						checked = checked || false;
					} else {
						log.debug("输入参数" + i + ":" + pp.getName()
								+ "=" + pp.getValue()
								+ "是字符串参数,获取参数的类型"
								+ pp.getValue().getClass().getName());
					}
				}
			}
		}

		if (!checked) {
			throw new Exception(sb.toString());
		}

		log.debug("输入参数的合法性检查完毕！检查结果为：" + checked);
	}

	private String getProcedureCallSql(String ProcedureName,
			List<ProcedureParameter> ppl) {

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
