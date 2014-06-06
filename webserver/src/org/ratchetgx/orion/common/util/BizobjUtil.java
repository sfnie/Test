/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 业务对象操作
 * 
 * @author hrfan
 */
@Component(value = "bizobjUtil")
public class BizobjUtil {

	private static Logger log = LoggerFactory.getLogger(BizobjUtil.class);
	@Autowired
	private DbUtil dbUtil;
	@Autowired
	private BizobjUtilHelper buHelper;
	@PersistenceContext
	private EntityManager em;

	public void persist(Object bizobj) {
		em.persist(bizobj);
	}

	public void merge(Object bizobj) {
		em.merge(bizobj);
	}

	public Object find(Class cls, Object id) {
		return em.find(cls, id);
	}

	public void remove(Object bizobj) {
		em.remove(bizobj);
	}

	public void insert(final String bizobj, final Map submitData)
			throws SQLException {
		insert(bizobj, submitData, new HashMap());
	}

	/**
	 * 插入业务对象
	 * 
	 * @param bizobj
	 *            业务对象名称
	 * @param submitData
	 *            业务对象数据
	 * @throws Exception
	 */
	public void insert(final String bizobj, final Map submitData,
			final Map<String, String> patterns) throws SQLException {
		final Map data = convertFromStringToObject(bizobj, submitData, patterns);

		final List<String> fitColumns = getFitColumns(bizobj, data);

		String wid = (String) submitData.get("wid");
		if (wid == null || "".equals(wid.trim())) {
			wid = dbUtil.getSysguid();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ");
		sb.append(bizobj);
		sb.append("(wid");
		Iterator itr = fitColumns.iterator();
		while (itr.hasNext()) {
			String columnName = (String) itr.next();
			sb.append(",");
			sb.append(columnName);
		}
		sb.append(") values(");
		sb.append("'");
		sb.append(wid);
		sb.append("'");
		itr = fitColumns.iterator();
		while (itr.hasNext()) {
			itr.next();
			sb.append(",?");
		}
		sb.append(")");

		log.debug(sb.toString());

		dbUtil.execute(sb.toString(), new IPreparedStatementProcessor() {
			public void process(PreparedStatement pstmt) throws SQLException {
				for (int i = 0; i < fitColumns.size(); i++) {
					String columnName = fitColumns.get(i);
					Object value = data.get(columnName);
					pstmt.setObject(i + 1, value);
				}

				pstmt.executeUpdate();
			}
		});

		submitData.put("wid", wid);
	}

	/**
	 * 删除业务对象
	 * 
	 * @param bizobj
	 *            业务对象名称
	 * @param wid
	 *            业务对象WID
	 * @throws SQLException
	 */
	public void delete(String bizobj, final String wid) throws SQLException {
		String sql = "delete from " + bizobj + " where wid=?";
		dbUtil.execute(sql, new IPreparedStatementProcessor() {
			public void process(PreparedStatement pstmt) throws SQLException {
				pstmt.setString(1, wid);
				pstmt.executeUpdate();
			}
		});
	}

	/**
	 * 删除符合条件的某业务对象记录
	 * 
	 * @param bizobj
	 * @param conditionGroup
	 * @throws SQLException
	 */
	public void delete(String bizobj, final ConditionGroup conditionGroup)
			throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(bizobj);

		final SqlAndValue sav = encodeCondtionGroup(conditionGroup);
		if (sav.getSql().length() > 0) {
			sql.append(" WHERE ");
			sql.append(sav.getSql());
		}

		log.debug(sql.toString());

		dbUtil.execute(sql.toString(), new IPreparedStatementProcessor() {
			public void process(PreparedStatement pstmt) throws SQLException {
				for (int i = 0; i < sav.getColumnNames().size(); i++) {
					RelOperEnum relOper = sav.getRelOpers().get(i);
					Object value = sav.getValues().get(i);
					if (relOper.equals(RelOperEnum.LIKE)) {// 是字符型且是LIKE
						value = "%" + value.toString() + "%";
					}
					pstmt.setObject(i + 1, value);
				}
				pstmt.executeUpdate();
			}
		});
	}

	public void update(String bizobj, final String wid, final Map submitData)
			throws SQLException {
		update(bizobj, wid, submitData, new HashMap());
	}

	/**
	 * 更新业务对象
	 * 
	 * @param bizobj
	 *            业务对象名称
	 * @param wid
	 *            业务对象WID
	 * @param submitData
	 *            业务对象数据
	 * @throws Exception
	 */
	public void update(String bizobj, final String wid, final Map submitData,
			final Map<String, String> patterns) throws SQLException {

		final Map data = convertFromStringToObject(bizobj, submitData, patterns);

		log.debug("data=" + data);

		final List<String> fitColumns = getFitColumns(bizobj, data);

		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(bizobj);
		sb.append(" set ");
		Iterator itr = fitColumns.iterator();
		while (itr.hasNext()) {
			String columnName = (String) itr.next();
			sb.append(columnName);
			sb.append("=?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" where wid=?");

		log.debug("update sql:" + sb.toString());

		dbUtil.execute(sb.toString(), new IPreparedStatementProcessor() {
			public void process(PreparedStatement pstmt) throws SQLException {
				for (int i = 0; i < fitColumns.size(); i++) {
					String columnName = fitColumns.get(i);
					Object value = data.get(columnName);
					pstmt.setObject(i + 1, value);
				}
				pstmt.setString(fitColumns.size() + 1, wid);
				pstmt.executeUpdate();
			}
		});
	}

	/**
	 * 查询业务对象
	 * 
	 * @param bizobj
	 *            业务对象名称
	 * @param wid
	 *            业务对象WID
	 * @return
	 * @throws SQLException
	 *             final表示不可重新赋值
	 */
	public Map query(final String bizobj, final String wid) throws SQLException {
		String sql = "select * from " + bizobj + " where wid=?";
		final Map data = new HashMap();
		dbUtil.execute(sql, new IPreparedResultSetProcessor() {
			public void processPreparedStatement(PreparedStatement pstmt)
					throws SQLException {
				pstmt.setString(1, wid);
			}

			public void processResultSet(ResultSet rs) throws SQLException {
				List<Map> datas = dbUtil.resultSet2List(rs);
				if (datas.size() > 0) {
					data.putAll((Map) datas.get(0));
				}
			}
		});
		return data;
	}

	public List<Map> query(final String bizobj,
			final ConditionGroup conditionGroup, Pagination pagination)
			throws SQLException {
		return query(bizobj, conditionGroup, pagination, "");
	}

	public List<Map> query(final String bizobj,
			final ConditionGroup conditionGroup, Pagination pagination,
			String order) throws SQLException {
		final List<Map> datas = new ArrayList<Map>();

		if (pagination == null) {
			pagination = new Pagination();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(bizobj);
		final SqlAndValue sav = encodeCondtionGroup(conditionGroup);
		if (sav != null) {
			if (sav.getSql().length() > 0) {
				sb.append(" WHERE ");
				sb.append(sav.getSql());
			}
		} else {
			sb.append(" WHERE ");
			sb.append(" 1=1 ");
		}
		if (order != null && !"".equals(order.trim())) {
			sb.append(" ORDER BY ");
			sb.append(order);
		}
		log.debug("sb====" + sb.toString());
		dbUtil.execute(sb.toString(), new IPreparedResultSetProcessor() {
			public void processPreparedStatement(PreparedStatement pstmt)
					throws SQLException {
				if (sav != null) {
					for (int i = 0; i < sav.getColumnNames().size(); i++) {
						RelOperEnum relOper = sav.getRelOpers().get(i);
						Object value = sav.getValues().get(i);
						if (relOper.equals(RelOperEnum.LIKE)) {// 是字符型且是LIKE
							value = "%" + value.toString() + "%";
						}
						pstmt.setObject(i + 1, value);
					}
				}
			}

			public void processResultSet(ResultSet rs) throws SQLException {
				datas.addAll(dbUtil.resultSet2List(rs));
			}
		}, pagination);

		return datas;
	}

	/*
	 * 根据设置的查询条件对象转换成相应的sql以及其他相关信息
	 */
	public SqlAndValue encodeCondtionGroup(ConditionGroup conditionGroup) {
		StringBuilder sql = new StringBuilder();
		List<String> columnNames = new ArrayList<String>();
		List<RelOperEnum> relOpers = new ArrayList<RelOperEnum>();
		List<Object> values = new ArrayList<Object>();
		List<String> commValues = new ArrayList<String>();
		if (conditionGroup != null) {
			// 处理条件组
			Iterator itr = conditionGroup.getConditionGroups().iterator();
			while (itr.hasNext()) {
				ConditionGroup cg = (ConditionGroup) itr.next();
				SqlAndValue cgSav = encodeCondtionGroup(cg);
				// 合并sql
				sql.append(" (");
				sql.append(cgSav.getSql());
				sql.append(" ) ");
				sql.append(conditionGroup.getAndOr().getDesc());
				// 合并字段
				columnNames.addAll(cgSav.getColumnNames());
				// 合并运算符
				relOpers.addAll(cgSav.getRelOpers());
				// 合并值
				values.addAll(cgSav.getValues());
			}
			// 删除sql后多余的AndOr
			if (conditionGroup.getConditionGroups().size() > 0) {
				sql.delete(sql.length() - 1
						- conditionGroup.getAndOr().getDesc().length(),
						sql.length());
			}

			// 处理条件
			itr = conditionGroup.getConditions().iterator();
			while (itr.hasNext()) {
				Condition cd = (Condition) itr.next();
				sql.append(" (");
				sql.append(cd.getColumnName());
				sql.append(" ");
				sql.append(cd.getRelOper().getDesc());
				sql.append(" ?");
				sql.append(" ) ");
				sql.append(conditionGroup.getAndOr().getDesc());

				// 合并字段
				columnNames.add(cd.getColumnName());
				// 合并运算符
				relOpers.add(cd.getRelOper());
				// 合并值
				values.add(cd.getValue());
			}
			// 删除sql后多余的AndOr
			if (conditionGroup.getConditions().size() > 0) {
				sql.delete(sql.length() - 1
						- conditionGroup.getAndOr().getDesc().length(),
						sql.length());
			}

			SqlAndValue sav = new SqlAndValue();
			sav.setSql(sql.toString());
			sav.setColumnNames(columnNames);
			sav.setRelOpers(relOpers);
			sav.setValues(values);
			return sav;
		} else
			return null;
	}

	public void save(final String bizobj, final Map submitData)
			throws SQLException {

		String wid = (String) submitData.get("wid");
		if (wid == null || "".equals(wid.trim())) {
			insert(bizobj, submitData);
		} else {
			update(bizobj, wid, submitData);
		}
	}

	public void save(final String bizobj, final Map submitData,
			final Map<String, String> patterns) throws SQLException {

		String wid = (String) submitData.get("wid");
		if (wid == null || "".equals(wid.trim())) {
			insert(bizobj, submitData, patterns);
		} else {
			update(bizobj, wid, submitData, patterns);
		}
	}

	/*
	 * 获取数据MAP的键名与业务对象字段名交集
	 */
	private List<String> getFitColumns(final String bizobj, final Map data)
			throws SQLException {
		final Map columnTypes = buHelper.getColumnTypes(bizobj);

		List<String> dataKeys = new ArrayList<String>();
		dataKeys.addAll(data.keySet());

		List<String> fitColumns = new ArrayList<String>();
		Iterator itr = columnTypes.keySet().iterator();
		while (itr.hasNext()) {
			String columnKey = (String) itr.next();
			boolean fit = false;
			for (int i = 0; i < dataKeys.size(); i++) {
				String dataKey = dataKeys.get(i);
				if (columnKey.equalsIgnoreCase(dataKey)) {
					fit = true;
					break;
				}
			}
			if (fit) {
				fitColumns.add(columnKey.toLowerCase());
			}
		}

		return fitColumns;
	}

	/*
	 * 把data中以bizobj字段为键的值，转换其数据库中字段的类型
	 */
	private Map<String, Object> convertFromStringToObject(String bizobj,
			Map<String, String> submitData, Map<String, String> patterns)
			throws SQLException {
		Map data = new HashMap();
		try {
			final Map columnTypes = buHelper.getColumnTypes(bizobj);
			Iterator keyItr = columnTypes.keySet().iterator();
			while (keyItr.hasNext()) {
				String columnName = (String) keyItr.next();
				String svalue = submitData.get(columnName);
				Integer columnType = (Integer) columnTypes.get(columnName);

				if (submitData.containsKey(columnName)) {
					data.put(
							columnName,
							dbUtil.convertFromStringToObject(
									svalue,
									columnType,
									(patterns == null || patterns
											.get(columnName) == null) ? "yyyy-MM-dd"
											: patterns.get(columnName)));
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new java.sql.SQLException(e.getMessage());
		}

		return data;
	}

}
