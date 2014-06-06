package org.ratchetgx.orion.widgets.selectrange;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.ehcache.Cache;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.common.util.IPreparedStatementProcessor;
import org.ratchetgx.orion.common.util.IResultSetProcessor;
import org.ratchetgx.orion.common.util.Pagination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 * 选择范围访问
 * 
 * @author hrfan
 */
public class SelectRangeVisitor {

	public static String SELECTRANGE_DEFINE_COMBOBOX = "selectRange.define.combobox";
	public static String SELECTRANGE_DEFINE_COMBOTREE = "selectRange.define.combotree";
	public static String SELECTRANGE_DEFINE_COMBOTABLE = "selectRange.define.combotable";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 选择范围定义缓存
	public void setSelectRangeDefineCache(Cache selectRangeDefineCache) {
		this.selectRangeDefineCache = selectRangeDefineCache;
	}

	@SuppressWarnings("unchecked")
	public boolean isCombobox(String selectRange) {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		return comboboxRanges.containsKey(selectRange)
				|| combotableRanges.containsKey(selectRange);
	}

	@SuppressWarnings("unchecked")
	public boolean isCombotree(String selectRange) {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		return combotreeRanges.containsKey(selectRange);
	}

	@SuppressWarnings("unchecked")
	public boolean isCombotable(String selectRange) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		return combotableRanges.containsKey(selectRange);
	}

	/**
	 * 下拉框获取数据,无级联
	 * 
	 * @param selectRange
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> cacheListCombobox(String selectRange) {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		final Map<String, Object> combobox = new LinkedHashMap<String, Object>();

		ComboboxSelectRange sr = comboboxRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return combobox;
		}

		if (sr.getPairs() != null) {
			return sr.getPairs();
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				ComboboxDefine cc = (ComboboxDefine) cls.newInstance();
				cc.setDbUtil(dbUtil);
				combobox.putAll(cc.getData());
			} catch (Exception ex) {
				log.error("", ex);
			}
			return combobox;
		}

		String epstarTableName = sr.getEpstarTableName();
		String epstarColumnName = sr.getEpstarColumnName();
		List<String> selectRangeResultList = new ArrayList<String>();
		if (epstarTableName != null && epstarColumnName != null) {
			if (log.isDebugEnabled()) {
				log.debug("正在读取" + sr.getName() + ",selectRange信息。");
			}
			String sql = "select selectRange from mod_bizobj t1 inner join mod_bizobjprpty t2 on (t1.wid = t2.bo_wid) "
					+ " where t1.wid = (select wid from mod_bizobj where ename = ? ) and t2.ename = ?";

			List list = jdbcTemplate.queryForList(sql, new Object[] {
					epstarTableName, epstarColumnName });
			if (list.size() > 0) {
				Map map = (Map) list.get(0);
				selectRangeResultList.add((String) map.get("selectRange"));
			}
			if (selectRangeResultList.size() == 0) {
				if (log.isDebugEnabled()) {
					log.debug("selectRange没定义。");
				}
			} else {
				String selectRangeDefinition = selectRangeResultList.get(0);
				if (log.isDebugEnabled()) {
					log.debug("selectRangeDefinition : "
							+ selectRangeDefinition);
				}
				if (!StringUtils.isBlank(selectRangeDefinition)) {
					SAXReader saxReader = new SAXReader();
					Document document = null;
					try {
						try {
							document = saxReader.read(new ByteArrayInputStream(
									selectRangeDefinition.getBytes("utf-8")));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Element root = document.getRootElement();
						String datasrc = root.attributeValue("datasrc");
						if (log.isDebugEnabled()) {
							log.debug("selectRange类型为" + datasrc);
						}
						if ("sql".equals(datasrc)) {
							String elementText = root.getText();

							if (elementText.indexOf("@") > -1) {
								String className = elementText.substring(1);
								try {
									ComboboxDefine cc = (ComboboxDefine) Class
											.forName(
													"org.ratchetgx.orion.widgets.selectrange.impl."
															+ className)
											.newInstance();

									cc.setDbUtil(dbUtil);
									combobox.putAll(cc.getData());
								} catch (ClassNotFoundException e) {
									log.error(e.getMessage(), e);
								} catch (InstantiationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								jdbcTemplate.query(elementText,
										new ResultSetExtractor() {
											public Object extractData(
													ResultSet rs)
													throws SQLException,
													DataAccessException {
												while (rs.next()) {
													combobox.put(
															rs.getString(1),
															rs.getObject(2));
												}
												return null;
											}
										});
							}
						} else {
							Iterator iter = root.elementIterator();
							while (iter.hasNext()) {
								Element element = (Element) iter.next();
								combobox.put(element.attributeValue("value"),
										element.getText());
							}
						}
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("epstar建模中读取" + combobox.size() + "条数据。");
			}
			return combobox;
		}

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(sr.getValueColumn());
		sql.append(",");
		sql.append(sr.getLabelColumn());
		sql.append(" FROM ");
		sql.append(sr.getTable());
		if (sr.getFilter() != null && sr.getFilter().trim().length() > 0) {
			sql.append(" WHERE ");
			sql.append(sr.getFilter());
		}
		if (sr.getOrder() != null && sr.getOrder().trim().length() > 0) {
			sql.append(" ORDER BY ");
			sql.append(sr.getOrder());
		}
		try {
			dbUtil.execute(sql.toString(), new IResultSetProcessor() {
				public void process(ResultSet rs) throws SQLException {
					while (rs.next()) {
						combobox.put(rs.getString(1), rs.getObject(2));
					}
				}
			});
		} catch (SQLException ex) {
			log.error("", ex);
		}

		return combobox;
	}

	/**
	 * 获取下拉框数据，根据级联
	 * 
	 * @param selectRange
	 * @param cascadeName
	 * @param cascadeValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> cacheListComboboxCascade(String selectRange,
			String cascadeName, final Object cascadeValue) {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		final Map<String, Object> combobox = new LinkedHashMap<String, Object>();

		ComboboxSelectRange sr = comboboxRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return combobox;
		}

		if (sr.getPairs() != null) {
			return sr.getPairs();
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				ComboboxDefine cc = (ComboboxDefine) cls.newInstance();
				if (!cc.getSupportedCascades().contains(cascadeName)) {
					return combobox;
				}
				cc.setCascade(cascadeName);
				cc.setCascadeValue(cascadeValue);
				cc.setDbUtil(dbUtil);
				combobox.putAll(cc.getData());
			} catch (Exception ex) {
				log.error("", ex);
			}

			return combobox;
		}

		// cascadeName不被sr支持
		if (!cascadeSupported(sr.getCascades(), cascadeName)) {
			return combobox;
		}

		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			sql.append(sr.getValueColumn());
			sql.append(",");
			sql.append(sr.getLabelColumn());
			sql.append(" FROM ");
			sql.append(sr.getTable());
			sql.append(" WHERE ");
			sql.append(cascadeName);
			sql.append(" = ?");
			if (sr.getFilter() != null && sr.getFilter().trim().length() > 0) {
				sql.append(" AND ");
				sql.append(sr.getFilter());
			}
			if (sr.getOrder() != null && sr.getOrder().trim().length() > 0) {
				sql.append(" ORDER BY ");
				sql.append(sr.getOrder());
			}
			dbUtil.execute(sql.toString(), new IPreparedResultSetProcessor() {
				public void processPreparedStatement(PreparedStatement pstmt)
						throws SQLException {
					pstmt.setObject(1, cascadeValue);
				}

				public void processResultSet(ResultSet rs) throws SQLException {
					while (rs.next()) {
						combobox.put(rs.getString(1), rs.getObject(2));
					}
				}
			});
		} catch (Exception ex) {
			log.error("", ex);
		}

		return combobox;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listCombotable(String selectRange,
			String condition, Pagination pagination) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		final List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		final CombotableSelectRange sr = combotableRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return dataList;
		}

		StringBuilder sql = new StringBuilder("SELECT ");
		List<Map> columns = (List<Map>) sr.getDisplay().get("columns");
		Iterator columnItr = columns.iterator();
		while (columnItr.hasNext()) {
			Map columnMap = (Map) columnItr.next();
			sql.append(columnMap.get("name"));
			sql.append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM ");
		sql.append(sr.getTable());
		boolean hasFilter = false;
		if (sr.getFilter() != null && sr.getFilter().trim().length() > 0) {
			hasFilter = true;
			sql.append(" WHERE (");
			sql.append(sr.getFilter());
			sql.append(") ");
		}
		if (!StringUtils.isEmpty(condition)) {
			if (hasFilter) {
				sql.append(" AND (");

				columnItr = columns.iterator();
				while (columnItr.hasNext()) {
					Map columnMap = (Map) columnItr.next();
					sql.append(columnMap.get("name"));
					sql.append(" like '%");
					sql.append(condition);
					sql.append("%' OR ");
				}
				sql.delete(sql.length() - 4, sql.length());
				sql.append(")");
			} else {
				sql.append(" WHERE ");
				columnItr = columns.iterator();
				while (columnItr.hasNext()) {
					Map columnMap = (Map) columnItr.next();
					sql.append(columnMap.get("name"));
					sql.append(" like '%");
					sql.append(condition);
					sql.append("%' OR ");
				}
				sql.delete(sql.length() - 4, sql.length());
			}
		}

		String order = sr.getOrder();
		if (!StringUtils.isBlank(order)) {
			sql.append(" order by " + order);
		}
		log.info("sql=" + sql);
		try {
			if (pagination == null) {// 不分页
				dbUtil.execute(sql.toString(), new IResultSetProcessor() {
					public void process(ResultSet rs) throws SQLException {
						while (rs.next()) {
							Map<String, Object> dataMap = new HashMap<String, Object>();
							List<Map> columns = (List<Map>) sr.getDisplay()
									.get("columns");
							Iterator columnItr = columns.iterator();
							while (columnItr.hasNext()) {
								Map columnMap = (Map) columnItr.next();
								String columnName = (String) columnMap
										.get("name");
								dataMap.put(columnName,
										rs.getString(columnName));
							}
							dataList.add(dataMap);
						}
					}
				});
			} else {// 分页
				dbUtil.execute(sql.toString(), new IResultSetProcessor() {
					public void process(ResultSet rs) throws SQLException {
						while (rs.next()) {
							Map<String, Object> dataMap = new HashMap<String, Object>();
							List<Map> columns = (List<Map>) sr.getDisplay()
									.get("columns");
							Iterator columnItr = columns.iterator();
							while (columnItr.hasNext()) {
								Map columnMap = (Map) columnItr.next();
								String columnName = (String) columnMap
										.get("name");
								dataMap.put(columnName,
										rs.getString(columnName));
							}
							dataList.add(dataMap);
						}
					}
				}, pagination);
			}
		} catch (SQLException ex) {
			log.error("", ex);
		}

		return dataList;
	}

	/**
	 * 获取表格选择范围数据，根据级联
	 * 
	 * @param selectRange
	 * @param cascadeName
	 * @param cascadeValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listCombotableCascade(String selectRange,
			String condition, Pagination pagination, String cascadeName,
			final Object cascadeValue) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		final List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		final CombotableSelectRange sr = combotableRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return dataList;
		}

		// cascadeName不被sr支持
		if (!cascadeSupported(sr.getCascades(), cascadeName)) {
			return dataList;
		}

		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			List<Map> columns = (List<Map>) sr.getDisplay().get("columns");
			Iterator columnItr = columns.iterator();
			while (columnItr.hasNext()) {
				Map columnMap = (Map) columnItr.next();
				sql.append(columnMap.get("name"));
				sql.append(",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" FROM ");
			sql.append(sr.getTable());
			sql.append(" WHERE ");
			sql.append(cascadeName);
			sql.append(" = ?");
			if (sr.getFilter() != null && sr.getFilter().trim().length() > 0) {
				sql.append(" AND ");
				sql.append(sr.getFilter());
			}

			if (!StringUtils.isEmpty(condition)) {
				sql.append(" AND (");

				columnItr = columns.iterator();
				while (columnItr.hasNext()) {
					Map columnMap = (Map) columnItr.next();
					sql.append(columnMap.get("name"));
					sql.append(" like '%");
					sql.append(condition);
					sql.append("%' OR ");
				}
				sql.delete(sql.length() - 4, sql.length());
				sql.append(")");
			}

			String order = sr.getOrder();
			if (!StringUtils.isBlank(order)) {
				sql.append(" order by " + order);
			}

			if (pagination == null) {
				dbUtil.execute(sql.toString(),
						new IPreparedResultSetProcessor() {
							public void processPreparedStatement(
									PreparedStatement pstmt)
									throws SQLException {
								pstmt.setObject(1, cascadeValue);
							}

							public void processResultSet(ResultSet rs)
									throws SQLException {
								while (rs.next()) {
									Map<String, Object> dataMap = new HashMap<String, Object>();
									List<Map> columns = (List<Map>) sr
											.getDisplay().get("columns");
									Iterator columnItr = columns.iterator();
									while (columnItr.hasNext()) {
										Map columnMap = (Map) columnItr.next();
										String columnName = (String) columnMap
												.get("name");
										dataMap.put(columnName,
												rs.getString(columnName));
									}
									dataList.add(dataMap);
								}
							}
						});
			} else {
				dbUtil.execute(sql.toString(),
						new IPreparedResultSetProcessor() {
							public void processPreparedStatement(
									PreparedStatement pstmt)
									throws SQLException {
								pstmt.setObject(1, cascadeValue);
							}

							public void processResultSet(ResultSet rs)
									throws SQLException {
								while (rs.next()) {
									Map<String, Object> dataMap = new HashMap<String, Object>();
									List<Map> columns = (List<Map>) sr
											.getDisplay().get("columns");
									Iterator columnItr = columns.iterator();
									while (columnItr.hasNext()) {
										Map columnMap = (Map) columnItr.next();
										String columnName = (String) columnMap
												.get("name");
										dataMap.put(columnName,
												rs.getString(columnName));
									}
									dataList.add(dataMap);
								}
							}
						}, pagination);
			}
		} catch (Exception ex) {
			log.error("", ex);
		}

		return dataList;
	}

	/**
	 * 下拉树获取数据，无级联
	 * 
	 * @param selectRange
	 * @param parentDefineName
	 * @param parentValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> cacheListCombotree(String selectRange,
			String parentDefineName, final Object parentValue) {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		final List<Map<String, Object>> combotree = new ArrayList<Map<String, Object>>();

		CombotreeSelectRange sr = combotreeRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return combotree;
		}

		if (parentDefineName == null) {// 树形数据必须设置层级列名
			return combotree;
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				CombotreeDefine cc = (CombotreeDefine) cls.newInstance();
				if (!cc.getSupportedParentDefines().contains(parentDefineName)) {
					return combotree;
				}
				cc.setParentDefine(parentDefineName);
				cc.setParentValue(parentValue);
				cc.setDbUtil(dbUtil);
				combotree.addAll(cc.getData());
			} catch (Exception ex) {
				log.error("", ex);
			}
			return combotree;
		}

		if (!parentDefineSupported(sr.getParentDefines(), parentDefineName)) {
			return combotree;
		}

		String order = sr.getOrder();

		try {
			if (parentValue == null || "".equals(parentValue.toString().trim())) {// 顶级数据
				StringBuilder sql = new StringBuilder("SELECT ");
				sql.append(sr.getValueColumn());
				sql.append(",");
				sql.append(sr.getLabelColumn());
				sql.append(",");
				sql.append(sr.getLabelFullColumn());
				sql.append(" FROM ");
				sql.append(sr.getTable());
				sql.append(" WHERE ");
				sql.append(parentDefineName);
				sql.append(" IS NULL");
				if (!StringUtils.isBlank(order)) {
					sql.append(" order by " + order);
				}
				dbUtil.execute(sql.toString(), new IResultSetProcessor() {
					public void process(ResultSet rs) throws SQLException {
						while (rs.next()) {
							Map m = new HashMap();
							m.put("value", rs.getObject(1));
							m.put("label", rs.getString(2));
							m.put("fullLabel", rs.getString(3));
							combotree.add(m);
						}
					}
				});

			} else {// 子级数据
				StringBuilder sql = new StringBuilder("SELECT ");
				sql.append(sr.getValueColumn());
				sql.append(",");
				sql.append(sr.getLabelColumn());
				sql.append(",");
				sql.append(sr.getLabelFullColumn());
				sql.append(" FROM ");
				sql.append(sr.getTable());
				sql.append(" WHERE ");
				sql.append(parentDefineName);
				sql.append(" = ?");
				if (!StringUtils.isBlank(order)) {
					sql.append(" order by " + order);
				}
				dbUtil.execute(sql.toString(),
						new IPreparedResultSetProcessor() {
							public void processPreparedStatement(
									PreparedStatement pstmt)
									throws SQLException {
								pstmt.setObject(1, parentValue);
							}

							public void processResultSet(ResultSet rs)
									throws SQLException {
								while (rs.next()) {
									Map m = new HashMap();
									m.put("value", rs.getObject(1));
									m.put("label", rs.getString(2));
									m.put("fullLabel", rs.getString(3));
									combotree.add(m);
								}
							}
						});
				if (!StringUtils.isBlank(order)) {
					sql.append(" order by " + order);
				}
			}

			// 是否为叶子节点
			StringBuilder leftSql = new StringBuilder();
			leftSql.append("SELECT 1 FROM ").append(sr.getTable())
					.append(" WHERE ").append(parentDefineName)
					.append(" = ? AND ROWNUM = 1");
			dbUtil.execute(leftSql.toString(),
					new IPreparedStatementProcessor() {
						public void process(PreparedStatement pstmt)
								throws SQLException {
							for (int i = 0; i < combotree.size(); i++) {
								Map m = combotree.get(i);
								pstmt.setObject(1, m.get("value"));
								boolean notleft = pstmt.executeQuery().next();
								m.put("left", !notleft);
							}
						}
					});

		} catch (Exception ex) {
			log.error("", ex);
		}

		return combotree;
	}

	/**
	 * 下拉框获取数据，有级联
	 * 
	 * @param selectRange
	 * @param parentDefineName
	 * @param parentValue
	 * @param cascadeName
	 * @param cascadeValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> cacheListCombotreeCascade(
			String selectRange, String parentDefineName,
			final Object parentValue, String cascadeName,
			final Object cascadeValue) {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		final List<Map<String, Object>> combotree = new ArrayList<Map<String, Object>>();

		CombotreeSelectRange sr = combotreeRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return combotree;
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				CombotreeDefine cc = (CombotreeDefine) cls.newInstance();
				if (!cc.getSupportedParentDefines().contains(parentDefineName)
						|| !cc.getSupportedCascades().contains(cascadeName)) {
					return combotree;
				}
				cc.setParentDefine(parentDefineName);
				cc.setParentValue(parentValue);
				cc.setCascade(cascadeName);
				cc.setCascadeValue(cascadeValue);
				cc.setDbUtil(dbUtil);
				combotree.addAll(cc.getData());
			} catch (Exception ex) {
				log.error("", ex);
			}
			return combotree;
		}

		if (parentDefineName == null) {// 树形数据必须设置层级列名
			return combotree;
		}

		if (!parentDefineSupported(sr.getParentDefines(), parentDefineName)) {
			return combotree;
		}

		if (!cascadeSupported(sr.getCascades(), cascadeName)) {
			return combotree;
		}

		try {
			if (parentValue == null) {// 顶级数据
				StringBuilder sql = new StringBuilder("SELECT ");
				sql.append(sr.getValueColumn());
				sql.append(",");
				sql.append(sr.getLabelColumn());
				sql.append(",");
				sql.append(sr.getLabelFullColumn());
				sql.append(" FROM ");
				sql.append(sr.getTable());
				sql.append(" WHERE ");
				sql.append(parentDefineName);
				sql.append(" IS NULL");
				sql.append(" AND ");
				sql.append(cascadeName);
				sql.append(" = ?");

				dbUtil.execute(sql.toString(),
						new IPreparedResultSetProcessor() {
							public void processPreparedStatement(
									PreparedStatement pstmt)
									throws SQLException {
								pstmt.setObject(1, cascadeValue);
							}

							public void processResultSet(ResultSet rs)
									throws SQLException {
								while (rs.next()) {
									Map m = new HashMap();
									m.put("value", rs.getObject(1));
									m.put("label", rs.getString(2));
									m.put("fullLabel", rs.getString(3));
									combotree.add(m);
								}
							}
						});

			} else {// 子级数据
				StringBuilder sql = new StringBuilder("SELECT ");
				sql.append(sr.getValueColumn());
				sql.append(",");
				sql.append(sr.getLabelColumn());
				sql.append(" FROM ");
				sql.append(sr.getTable());
				sql.append(" WHERE ");
				sql.append(parentDefineName);
				sql.append(" = ?");
				sql.append(" AND ");
				sql.append(cascadeName);
				sql.append(" = ?");

				dbUtil.execute(sql.toString(),
						new IPreparedResultSetProcessor() {
							public void processPreparedStatement(
									PreparedStatement pstmt)
									throws SQLException {
								pstmt.setObject(1, parentValue);
								pstmt.setObject(2, cascadeValue);
							}

							public void processResultSet(ResultSet rs)
									throws SQLException {
								while (rs.next()) {
									Map m = new HashMap();
									m.put("value", rs.getObject(1));
									m.put("label", rs.getString(2));
									m.put("fullLabel", rs.getString(3));
									combotree.add(m);
								}
							}
						});
			}

			// 是否为叶子节点
			StringBuilder leftSql = new StringBuilder();
			leftSql.append("SELECT 1 FROM ").append(sr.getTable())
					.append(" WHERE ").append(parentDefineName)
					.append(" = ? AND ROWNUM = 1");
			dbUtil.execute(leftSql.toString(),
					new IPreparedStatementProcessor() {
						public void process(PreparedStatement pstmt)
								throws SQLException {
							for (int i = 0; i < combotree.size(); i++) {
								Map m = combotree.get(i);
								pstmt.setObject(1, m.get("value"));
								boolean notleft = pstmt.executeQuery().next();
								m.put("left", !notleft);
							}
						}
					});

		} catch (Exception ex) {
			log.error("", ex);
		}

		return combotree;
	}

	/**
	 * 获取选择范围的默认层级名称
	 * 
	 * @param sr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String cacheGetDefaultParentDefineName(String selectRange) {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		CombotreeSelectRange sr = combotreeRanges.get(selectRange);
		if (sr == null) {
			return null;
		}

		List<String> parentDefines = sr.getParentDefines();
		if (parentDefines == null || parentDefines.isEmpty()) {
			return null;
		}

		return parentDefines.get(0);
	}

	/**
	 * 获取某选择范围所有数据
	 * 
	 * @param selectRange
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Object, String> cacheGetVLs(String selectRange) {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		final Map<Object, String> vls = new HashMap<Object, String>();

		ComboboxSelectRange sr = comboboxRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return vls;
		}

		if (sr.getPairs() != null) {
			Iterator iter = sr.getPairs().keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				vls.put(key, (String) sr.getPairs().get(key));
			}
			return vls;
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				ComboboxDefine cc = (ComboboxDefine) cls.newInstance();
				cc.setDbUtil(dbUtil);
				vls.putAll(cc.getVLs());
			} catch (Exception ex) {
				log.error("", ex);
			}

			return vls;
		}

		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(sr.getValueColumn());
		sql.append(",");
		sql.append(sr.getLabelColumn());
		sql.append(" FROM ");
		sql.append(sr.getTable());
		if (sr.getFilter() != null && sr.getFilter().trim().length() > 0) {
			sql.append(" WHERE ");
			sql.append(sr.getFilter());
		}
		try {
			dbUtil.execute(sql.toString(), new IResultSetProcessor() {
				public void process(ResultSet rs) throws SQLException {
					while (rs.next()) {
						vls.put(rs.getObject(1).toString(), rs.getString(2));
					}
				}
			});
		} catch (SQLException ex) {
			log.error("", ex);
		}

		return vls;
	}

	public String cacheGetVL(String selectRange, Object key) {
		Map<Object, String> vls = cacheGetVLs(selectRange);
		return vls.get(key);
	}

	/**
	 * 获取某表格选择范围显示文本
	 * 
	 * @param baseType
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getTableFL(String baseType, final String value) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		final Map<Object, String> vfls = new HashMap<Object, String>();
		String result = StringUtils.EMPTY;

		CombotableSelectRange sr = combotableRanges.get(baseType);
		if (sr == null) {
			log.info("\"" + value + "\"选择范围未找到.");
			return result;
		}

		StringBuilder sql = new StringBuilder("SELECT ");

		Map<String, List> map = sr.getDisplay();
		List columns = (List) map.get("columns");
		Map valueColumn = null;
		Map labelColumn = null;
		if (columns != null) {
			for (int i = 0; i < columns.size(); i++) {
				Map column = (Map) columns.get(i);
				String flag = (String) column.get("flag");
				if ("value".equals(flag)) {
					valueColumn = column;
				} else if ("label".equals(flag)) {
					labelColumn = column;
				}
			}
		}

		if (valueColumn == null || labelColumn == null) {
			log.error("请配置value或label。" + value);
			return result;
		}

		sql.append(labelColumn.get("name"));
		sql.append(" FROM ");
		sql.append(sr.getTable());
		sql.append(" WHERE ").append(valueColumn.get("name")).append(" = ?");

		try {
			dbUtil.execute(sql.toString(), new IPreparedResultSetProcessor() {

				public void processResultSet(ResultSet rs) throws SQLException {
					if (rs.next()) {
						vfls.put(value, rs.getString(1));
					}
				}

				public void processPreparedStatement(PreparedStatement pstmt)
						throws SQLException {
					pstmt.setString(1, value);

				}
			});

		} catch (Exception ex) {
			log.error("", ex);
		}
		result = vfls.get(value);
		return result == null ? StringUtils.EMPTY : result;
	}

	/**
	 * 
	 * 获取某树选择范围所有显示文本
	 * 
	 * @param selectRange
	 * @return
	 */
	public Map<Object, String> cacheGetVFLs(String selectRange) {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		final Map<Object, String> vfls = new HashMap<Object, String>();

		CombotreeSelectRange sr = combotreeRanges.get(selectRange);
		if (sr == null) {
			log.info("\"" + selectRange + "\"选择范围未找到.");
			return vfls;
		}

		if (sr.getClassDefine() != null) {
			String className = sr.getClassDefine();
			Class cls;
			try {
				cls = Class.forName(className);
				CombotreeDefine cc = (CombotreeDefine) cls.newInstance();
				cc.setDbUtil(dbUtil);
				vfls.putAll(cc.getVFLs());
			} catch (Exception ex) {
				log.error("", ex);
			}
			return vfls;
		}

		try {
			StringBuilder sql = new StringBuilder("SELECT ");
			sql.append(sr.getValueColumn());
			sql.append(",");
			sql.append(sr.getLabelColumn());
			sql.append(",");
			sql.append(sr.getLabelFullColumn());
			sql.append(" FROM ");
			sql.append(sr.getTable());

			dbUtil.execute(sql.toString(), new IResultSetProcessor() {
				public void process(ResultSet rs) throws SQLException {
					while (rs.next()) {
						vfls.put(rs.getObject(1).toString(), rs.getString(3));
					}
				}
			});
		} catch (Exception ex) {
			log.error("", ex);
		}

		return vfls;
	}

	public String cacheGetVFL(String selectRange, Object key) {
		Map<Object, String> vfls = cacheGetVFLs(selectRange);
		return vfls.get(key);
	}

	/*
	 * 级联名称cascadeName是否被cascades支持
	 */
	private boolean cascadeSupported(List<String> cascades, String cascadeName) {
		if (cascades == null || cascades.isEmpty()) {
			return false;
		}

		Iterator iter = cascades.iterator();
		while (iter.hasNext()) {
			String cascade = (String) iter.next();
			if (cascade.equals(cascadeName)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * 层级名称parentDefine是否被parentDefines支持
	 */
	private boolean parentDefineSupported(List<String> parentDefines,
			String parentDefineName) {
		if (parentDefines == null || parentDefines.isEmpty()) {
			return false;
		}

		Iterator iter = parentDefines.iterator();
		while (iter.hasNext()) {
			String parentDefine = (String) iter.next();
			if (parentDefine.equals(parentDefine)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 把map中键值对转换成排序列表
	 * 
	 * @param map
	 * @param order
	 * @return
	 */
	public List<String[]> sortMap(Map map, final String order) {
		List ses = new ArrayList<String[]>();
		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String value = (String) iter.next();
			String label = (String) map.get(value);
			String[] e = { value, label };
			ses.add(e);
		}
		Collections.sort(ses, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				if ("desc".equalsIgnoreCase(order)) {
					return o2[0].compareTo(o1[0]);
				} else {
					return o1[0].compareTo(o2[0]);
				}
			}
		});
		return ses;
	}

	public List<String> getComboboxNames() {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		List<String> names = new ArrayList<String>();
		names.addAll(comboboxRanges.keySet());
		return names;
	}

	public List<String> getCombotableNames() {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		List<String> names = new ArrayList<String>();
		names.addAll(combotableRanges.keySet());
		return names;
	}

	public List<String> getCombotreeNames() {
		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		List<String> names = new ArrayList<String>();
		names.addAll(combotreeRanges.keySet());
		return names;
	}

	public Map<String, JSONObject> getCombotableConfigs() throws JSONException {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		Map<String, JSONObject> combotableConfigs = new HashMap<String, JSONObject>();
		Iterator eItr = combotableRanges.entrySet().iterator();
		while (eItr.hasNext()) {
			Map.Entry e = (Map.Entry) eItr.next();
			String name = (String) e.getKey();
			CombotableSelectRange sr = (CombotableSelectRange) e.getValue();

			JSONObject config = new JSONObject();
			config.put("title", sr.getDisplay().get("title"));
			JSONArray columns = new JSONArray();
			Iterator columnItr = ((List<Map>) sr.getDisplay().get("columns"))
					.iterator();
			while (columnItr.hasNext()) {
				Map column = (Map) columnItr.next();
				JSONObject colObj = new JSONObject();
				colObj.put("header", column.get("header"));
				colObj.put("name", column.get("name"));
				colObj.put("width", column.get("width"));
				columns.put(colObj);
			}
			config.put("columns", columns);

			combotableConfigs.put(name, config);
		}

		return combotableConfigs;
	}

	/**
	 * 获取选择范围的列名称
	 * 
	 * @param selectRangeName
	 *            选择范围名称
	 * @return 列名称列表
	 */
	public List<String> listColumnNamesOfSR(String selectRangeName) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		List<String> columnNames = new ArrayList<String>();

		CombotableSelectRange combotableSelectRange = combotableRanges
				.get(selectRangeName);
		Map display = combotableSelectRange.getDisplay();
		List<Map> columns = (List<Map>) display.get("columns");
		Iterator columnItr = columns.iterator();
		while (columnItr.hasNext()) {
			Map columnMap = (Map) columnItr.next();
			columnNames.add((String) columnMap.get("name"));
		}

		return columnNames;
	}

	public boolean isValueColumn(String selectRangeName, String columnName) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		if (selectRangeName == null || columnName == null) {
			return false;
		}
		CombotableSelectRange combotableSelectRange = combotableRanges
				.get(selectRangeName);
		Map display = combotableSelectRange.getDisplay();
		List<Map> columns = (List<Map>) display.get("columns");
		Iterator columnItr = columns.iterator();
		while (columnItr.hasNext()) {
			Map columnMap = (Map) columnItr.next();
			if ("value".equals(columnMap.get("flag"))
					&& columnName.equals(columnMap.get("name"))) {
				return true;
			}
		}
		return false;
	}

	public boolean isLabelColumn(String selectRangeName, String columnName) {
		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		if (selectRangeName == null || columnName == null) {
			return false;
		}
		CombotableSelectRange combotableSelectRange = combotableRanges
				.get(selectRangeName);
		Map display = combotableSelectRange.getDisplay();
		List<Map> columns = (List<Map>) display.get("columns");
		Iterator columnItr = columns.iterator();
		while (columnItr.hasNext()) {
			Map columnMap = (Map) columnItr.next();
			if ("label".equals(columnMap.get("flag"))
					&& columnName.equals(columnMap.get("name"))) {
				return true;
			}
		}
		return false;
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DbUtil dbUtil;

	private Cache selectRangeDefineCache;
}