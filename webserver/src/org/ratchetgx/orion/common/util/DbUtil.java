package org.ratchetgx.orion.common.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.lusd.util.StringUtil;

/**
 * 数据库工具类
 * 
 * @author hrfan
 * 
 */
@Component(value = "dbUtil")
public class DbUtil {

	private static Logger log = LoggerFactory.getLogger(DbUtil.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private DbUtilCallProcedureService procedure;


	/**
	 * 新版存储过程访问接口
	 * @param procedureName
	 * @param ppl
	 * @return flag 0：调用存储过程失败 1：调用存储过程成功
	 * @throws Exception
	 */
	public int executeProcedure(String procedureName, ProcedureParameterList ppl)
			throws Exception {
		int bSucc = 0;
		try {
			/** 调用DbUtilCallProcedureService中定义的接口 */
			bSucc = procedure.execProucedure(procedureName, ppl, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return bSucc;
	}
	/**
	 * 该方法不建议使用，调用存储过程可使用新版存储过程访问接口。
	 * 详细情况请参考DbUtil下的executeProcedure(String procedureName,ProcedureParameterList ppl)方法
	 * @param sql
	 * @param cstmtp
	 */
	@Deprecated
	public void execute(final String sql,
			final ICallableStatementProcessor cstmtp) {

		jdbcTemplate.execute(new CallableStatementCreator() {
			public CallableStatement createCallableStatement(Connection conn)
					throws SQLException {

				CallableStatement cstmt = conn.prepareCall(sql);
				cstmtp.processBefore(cstmt);

				return cstmt;
			}
		}, new CallableStatementCallback<Object>() {
			public Object doInCallableStatement(CallableStatement cstmt)
					throws SQLException, DataAccessException {

				cstmt.executeUpdate();

				cstmtp.processAfter(cstmt);
				
				cstmt.close();

				return null;
			}
		});
	}

	public void execute(String sql, final IResultSetProcessor rsp)
			throws SQLException {

		jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {
			public Object extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				rsp.process(rs);
				return null;
			}
		});

	}

	/**
	 * 批量更新
	 * 
	 * @param sqls
	 * @return
	 */
	public int[] batchUpdate(String[] sqls) throws SQLException {
		int[] ints = jdbcTemplate.batchUpdate(sqls);
		return ints;
	}

	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param rsp
	 * @param pagination
	 */
	public void execute(String sql, final IResultSetProcessor rsp,
			final Pagination pagination) throws SQLException {
		if (pagination.getCurrentPage() < 1) {
			pagination.setCurrentPage(1);
		}

		// 求总数
		String totalSql = "select count(1) from (" + sql + ")";
		jdbcTemplate.query(totalSql, new ResultSetExtractor<Map>() {
			public Map extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (rs.next()) {
					pagination.setTotal(rs.getInt(1));
				} else {
					pagination.setTotal(0);
				}
				return null;
			}
		});

		// 根据总数、当前页计算pagination中其他的属性
		pagination.setPages(new Double(Math.ceil(new Double(pagination
				.getTotal()) / pagination.getPageCount())).intValue());
		pagination.setPrevPage(pagination.getCurrentPage() == 1 ? 1
				: pagination.getCurrentPage() - 1);
		pagination.setNextPage(pagination.getCurrentPage() < pagination
				.getPages() ? pagination.getCurrentPage() + 1 : pagination
				.getCurrentPage());
		pagination.setEndPage(pagination.getPages() == 0 ? 1 : pagination
				.getPages());

		// 查找当前页对应的记录集
		// String pageSql = "SELECT b.* FROM (SELECT a.*, rownum AS rn FROM (" +
		// sql + ") a) b WHERE b.rn >" + (pagination.getCurrentPage() - 1) *
		// pagination.getPageCount() + " AND b.rn <=" +
		// pagination.getCurrentPage() * pagination.getPageCount();
		StringBuilder pageSql = new StringBuilder();
		pageSql.append("SELECT b.* FROM (SELECT a.*, rownum AS ");
		pageSql.append(Pagination.RN);
		pageSql.append(" FROM (");
		pageSql.append(sql);
		pageSql.append(") a) b WHERE b.");
		pageSql.append(Pagination.RN);
		pageSql.append(" > ");
		pageSql.append((pagination.getCurrentPage() - 1)
				* pagination.getPageCount());
		pageSql.append(" AND b.");
		pageSql.append(Pagination.RN);
		pageSql.append(" <= ");
		pageSql.append(pagination.getCurrentPage() * pagination.getPageCount());

		jdbcTemplate.query(pageSql.toString(),
				new ResultSetExtractor<Object>() {
					public Object extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						rsp.process(rs);
						return null;
					}
				});

	}

	public void execute(final String sql, final IPreparedResultSetProcessor prsp)
			throws SQLException {

		jdbcTemplate.execute(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				prsp.processPreparedStatement(pstmt);

				return pstmt;
			}
		}, new PreparedStatementCallback<Object>() {
			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				ResultSet rs = pstmt.executeQuery();
				prsp.processResultSet(rs);

				pstmt.close();
				
				return null;
			}
		});

	}

	public void execute(final String sql,
			final IPreparedResultSetProcessor prsp, final Pagination pagination)
			throws SQLException {

		if (pagination.getCurrentPage() < 1) {
			pagination.setCurrentPage(1);
		}

		// 求总数
		final String totalSql = "select count(1) from (" + sql + ")";
		jdbcTemplate.execute(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pstmt = conn.prepareStatement(totalSql);
				prsp.processPreparedStatement(pstmt);

				return pstmt;
			}
		}, new PreparedStatementCallback<Object>() {
			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					pagination.setTotal(rs.getInt(1));
				} else {
					pagination.setTotal(0);
				}
				
				pstmt.close();

				return null;
			}
		});

		// 根据总数、当前页计算pagination中其他的属性
		pagination.setPages(new Double(Math.ceil(new Double(pagination
				.getTotal()) / pagination.getPageCount())).intValue());
		pagination.setPrevPage(pagination.getCurrentPage() == 1 ? 1
				: pagination.getCurrentPage() - 1);
		pagination.setNextPage(pagination.getCurrentPage() < pagination
				.getPages() ? pagination.getCurrentPage() + 1 : pagination
				.getCurrentPage());
		pagination.setEndPage(pagination.getPages() == 0 ? 1 : pagination
				.getPages());

		// 查找当前页对应的记录集
		// final String pageSql =
		// "SELECT b.* FROM (SELECT a.*, rownum AS rn FROM (" + sql +
		// ") a) b WHERE b.rn >" + (pagination.getCurrentPage() - 1) *
		// pagination.getPageCount() + " AND b.rn <=" +
		// pagination.getCurrentPage() * pagination.getPageCount();
		final StringBuilder pageSql = new StringBuilder();
		pageSql.append("SELECT b.* FROM (SELECT a.*, rownum AS ");
		pageSql.append(Pagination.RN);
		pageSql.append(" FROM (");
		pageSql.append(sql);
		pageSql.append(") a) b WHERE b.");
		pageSql.append(Pagination.RN);
		pageSql.append(" > ");
		pageSql.append((pagination.getCurrentPage() - 1)
				* pagination.getPageCount());
		pageSql.append(" AND b.");
		pageSql.append(Pagination.RN);
		pageSql.append(" <= ");
		pageSql.append(pagination.getCurrentPage() * pagination.getPageCount());
		jdbcTemplate.execute(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement pstmt = conn.prepareStatement(pageSql
						.toString());
				prsp.processPreparedStatement(pstmt);

				return pstmt;
			}
		}, new PreparedStatementCallback<Object>() {
			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				ResultSet rs = pstmt.executeQuery();
				prsp.processResultSet(rs);
				
				pstmt.close();

				return null;
			}
		});

	}

	public void execute(final String sql,
			final IPreparedStatementProcessor pstmtp) throws SQLException {
		jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>() {
			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				pstmtp.process(pstmt);
				
				pstmt.close();
				return null;
			}
		});
	}

	public void execute(final String sql, final ICallableResultSetProcessor crsp)
			throws SQLException {

		jdbcTemplate.execute(new CallableStatementCreator() {
			public CallableStatement createCallableStatement(Connection conn)
					throws SQLException {

				CallableStatement cstmt = conn.prepareCall(sql);
				crsp.processCallableStatement(cstmt);

				return cstmt;
			}
		}, new CallableStatementCallback<Object>() {
			public Object doInCallableStatement(CallableStatement cstmt)
					throws SQLException, DataAccessException {

				ResultSet rs = cstmt.executeQuery();
				crsp.processResultSet(rs);
				
				cstmt.close();
				
				return null;
			}
		});

	}

	/**
	 * 把rs中的所有记录转换成Map并保存在List中返回
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public List<Map> resultSet2List(ResultSet rs) throws SQLException {
		List<Map> rcList = new ArrayList<Map>();

		ResultSetMetaData rsmd = rs.getMetaData();
		String[] columnNames = new String[rsmd.getColumnCount()];
		int[] columnTypes = new int[rsmd.getColumnCount()];
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			String columnName = rsmd.getColumnName(i + 1);
			if (Pagination.RN.equals(columnName)) {
				continue;
			}
			columnNames[i] = columnName.toLowerCase();
			columnTypes[i] = rsmd.getColumnType(i + 1);
		}

		while (rs.next()) {
			Map<String, Object> rc = new HashMap<String, Object>();
			for (int i = 0; i < columnNames.length; i++) {
				rc.put(columnNames[i],
						getPropertyValue(rs, columnNames[i], columnTypes[i]));
			}
			rcList.add(rc);
		}

		return rcList;
	}

	/**
	 * 把rs中的所有记录转换成Map并保存在List中返回
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public List<Map> resultSet2ListToUpperCase(ResultSet rs)
			throws SQLException {
		List<Map> rcList = new ArrayList<Map>();

		ResultSetMetaData rsmd = rs.getMetaData();
		String[] columnNames = new String[rsmd.getColumnCount()];
		int[] columnTypes = new int[rsmd.getColumnCount()];
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			String columnName = rsmd.getColumnName(i + 1);
			columnNames[i] = columnName.toUpperCase();
			columnTypes[i] = rsmd.getColumnType(i + 1);
		}

		while (rs.next()) {
			Map<String, Object> rc = new HashMap<String, Object>();
			for (int i = 0; i < columnNames.length; i++) {
				rc.put(columnNames[i],
						getPropertyValue(rs, columnNames[i], columnTypes[i]));
			}
			rcList.add(rc);
		}

		return rcList;
	}

	/**
	 * 根据字段类型获取结果集的字段值
	 * 
	 * @param rs
	 * @param property
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public Object getPropertyValue(ResultSet rs, String property, int type)
			throws SQLException {
		Object value = null;

		switch (type) {
		case Types.BIT:
			value = rs.getBytes(property);
			break;
		case Types.TINYINT:
			value = rs.getInt(property);
			break;
		case Types.SMALLINT:
			value = rs.getInt(property);
			break;
		case Types.INTEGER:
			value = rs.getInt(property);
			break;
		case Types.BIGINT:
			value = rs.getInt(property);
			break;
		case Types.FLOAT:
			value = rs.getDouble(property);
			break;
		case Types.REAL:
			value = rs.getDouble(property);
			break;
		case Types.DOUBLE:
			value = rs.getDouble(property);
			break;
		case Types.NUMERIC:
			String svalue = rs.getString(property);
			if (!rs.wasNull()) {
				if (svalue.indexOf(".") < 0) {
					value = Integer.parseInt(svalue);
				} else {
					value = Double.parseDouble(svalue);
				}
			}
			break;
		case Types.VARCHAR:
			value = rs.getString(property);
			break;
		case Types.DATE:
			value = rs.getTimestamp(property);
			break;
		case Types.CLOB:
			value = rs.getString(property);
			break;
		default:
			value = rs.getObject(property);
		}
		return value;
	}

	public List paginateList(List list, Pagination pagination) {
		if (pagination.getCurrentPage() < 1) {
			pagination.setCurrentPage(1);
		}

		pagination.setTotal(list.size());

		// 根据总数、当前页计算pagination中其他的属性
		pagination.setPages(new Double(Math.ceil(new Double(pagination
				.getTotal()) / pagination.getPageCount())).intValue());
		pagination.setPrevPage(pagination.getCurrentPage() == 1 ? 1
				: pagination.getCurrentPage() - 1);
		pagination.setNextPage(pagination.getCurrentPage() < pagination
				.getPages() ? pagination.getCurrentPage() + 1 : pagination
				.getCurrentPage());
		pagination.setEndPage(pagination.getPages() == 0 ? 1 : pagination
				.getPages());

		int start = (pagination.getCurrentPage() - 1)
				* pagination.getPageCount();
		int end = (start + pagination.getPageCount()) < list.size() ? start
				+ pagination.getPageCount() : list.size();
		return list.subList(start, end);
	}

	public String getSysguid() {
		String sql = "select sys_guid() from dual";
		return jdbcTemplate.queryForObject(sql, String.class);
	}

	/**
	 * 转换字符串变成其他类型的值
	 * 
	 * @param svalue
	 *            字符串
	 * @param type
	 *            类型
	 * @return
	 * @see java.sql.Types
	 */
	public Object convertFromStringToObject(String svalue, int type,
			String pattern) throws Exception {
		Object value = svalue;

		switch (type) {
		case Types.TINYINT:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Integer.parseInt(svalue);
			}
			break;
		case Types.SMALLINT:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Integer.parseInt(svalue);
			}
			break;
		case Types.INTEGER:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Integer.parseInt(svalue);
			}
			break;
		case Types.BIGINT:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Integer.parseInt(svalue);
			}
			break;
		case Types.FLOAT:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Double.parseDouble(svalue);
			}
			break;
		case Types.REAL:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Double.parseDouble(svalue);
			}
			break;
		case Types.DOUBLE:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Double.parseDouble(svalue);
			}
			break;
		case Types.NUMERIC:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				value = Double.parseDouble(svalue);
			}
			break;
		case Types.DATE:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				value = new java.sql.Date(sdf.parse(svalue).getTime());
			}
			break;
		case Types.TIMESTAMP:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			} else {
				log.info("pattern=" + pattern + ";svalue="+ svalue);
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				value = new Timestamp(sdf.parse(svalue).getTime());
			}
			break;
		default:
			if (StringUtil.isEmpty(svalue)) {
				value = null;
			}
		}

		return value;
	}

	public static void convertDateToString(Map<String, Object> data,
			Map<String, String> patterns, String defaultPattern) {
		Iterator<String> keyItr = data.keySet().iterator();
		while (keyItr.hasNext()) {
			String key = keyItr.next();
			Object value = data.get(key);
			String pattern = defaultPattern;
			if (patterns != null && patterns.containsKey(key)) {
				pattern = patterns.get(key);
			}
			data.put(key, convertDateToString(value, pattern));
		}
	}

	public static String convertDateToString(Object value, String pattern) {
		if (value == null) {
			return null;
		}

		if (value instanceof java.util.Date) {
			return convertDateToString((java.util.Date) value, pattern);
		}

		if (value instanceof java.sql.Date) {
			return convertDateToString((java.sql.Date) value, pattern);
		}

		if (value instanceof java.sql.Timestamp) {
			return convertDateToString((java.sql.Timestamp) value, pattern);
		}

		if (value instanceof oracle.sql.TIMESTAMP) {
			String cv = "";
			try {
				cv = convertDateToString((oracle.sql.TIMESTAMP) value, pattern);
			} catch (SQLException e) {
				log.error("", e);
			}
			return cv;
		}

		return value.toString();
	}

	public static String convertDateToString(java.util.Date value,
			String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(((java.util.Date) value).getTime());
	}

	public static String convertDateToString(java.sql.Date value, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(((java.sql.Date) value).getTime());
	}

	public static String convertDateToString(java.sql.Timestamp value,
			String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(((java.sql.Timestamp) value).getTime());
	}

	public static String convertDateToString(oracle.sql.TIMESTAMP value,
			String pattern) throws SQLException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(((oracle.sql.TIMESTAMP) value).timestampValue()
				.getTime());
	}

}
