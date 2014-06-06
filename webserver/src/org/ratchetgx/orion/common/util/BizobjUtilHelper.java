package org.ratchetgx.orion.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "bizobjUtilHelper")
public class BizobjUtilHelper {
	private static Logger log = LoggerFactory.getLogger(BizobjUtilHelper.class);
	@Autowired
	private DbUtil dbUtil;

	/**
	 * 获取业务对象的字段信息,wid不包含
	 * 
	 * @param bizobj
	 * @return
	 */
	public Map<String, Integer> getColumnTypes(String bizobj)
			throws SQLException {
		final Map<String, Integer> columnTypes = new HashMap<String, Integer>();

		String sql = "select * from " + bizobj + " where rownum = 0";
		dbUtil.execute(sql, new IResultSetProcessor() {
			public void process(ResultSet rs) throws SQLException {
				int cc = rs.getMetaData().getColumnCount();
				for (int i = 0; i < cc; i++) {
					String columnName = rs.getMetaData().getColumnName(i + 1)
							.toLowerCase();
					if ("wid".equalsIgnoreCase(columnName)) {
						continue;
					}
					int columnType = rs.getMetaData().getColumnType(i + 1);
					columnTypes.put(columnName, columnType);
				}
			}
		});

		return columnTypes;
	}

	/**
	 * 获取业务对象的字段长度信息,wid不包含
	 * 
	 * @param bizobj
	 * @return
	 */
	public Map<String, Integer> getColumnPrecision(String bizobj)
			throws SQLException {
		final Map<String, Integer> columnPrecisions = new HashMap<String, Integer>();
		String sql = "select * from " + bizobj + " where rownum = 0";
		dbUtil.execute(sql, new IResultSetProcessor() {
			public void process(ResultSet rs) throws SQLException {
				int cc = rs.getMetaData().getColumnCount();
				for (int i = 0; i < cc; i++) {
					String columnName = rs.getMetaData().getColumnName(i + 1)
							.toLowerCase();
					if ("wid".equalsIgnoreCase(columnName)) {
						continue;
					}
					int columnPrecision = rs.getMetaData().getPrecision(i + 1);
					columnPrecisions.put(columnName, columnPrecision);
				}
			}
		});

		return columnPrecisions;
	}

}
