package org.ratchetgx.orion.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

public class SqlAndValue {

	private String sql;
	private List<String> columnNames;
	private List<RelOperEnum> relOpers;
	private List<Object> values;
	private List<String> value;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<RelOperEnum> getRelOpers() {
		return relOpers;
	}

	public void setRelOpers(List<RelOperEnum> relOpers) {
		this.relOpers = relOpers;
	}

	public List<Object> getValues() {
		return values;
	}

	public void setValues(List<Object> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "SqlAndValue{" + "sql=" + sql + ", columnNames=" + columnNames
				+ ", relOpers=" + relOpers + ", values=" + values + '}';
	}

	void setValue(List<String> values) {
		this.value = values;
	}

	public static void main(String[] args) throws SQLException {
		oracle.jdbc.pool.OracleConnectionPoolDataSource ds = new OracleConnectionPoolDataSource();
		ds.setURL("jdbc:oracle:thin:@172.16.1.11:1521:urpdb");
		ds.setUser("URP_YJS_APP_SHJD");
		ds.setPassword("wiscom");

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = ds.getConnection();
			String sql = "update T_XJGL_XJXX_YJSRXQXX set KSBH=?,BLRXZGNX=?,ZHBYSJ=? where XH=? and BLRXZGNX > ?";
			pstmt = conn.prepareStatement(sql);
			String ksbh = "";
			Integer blrxzgnx = 0;
			Date zhbysj = new Date();
			String xh = "sfnie";
			pstmt.setObject(1, ksbh);
			pstmt.setObject(2, blrxzgnx);
			pstmt.setObject(3, new java.sql.Date(zhbysj.getTime()));
			pstmt.setObject(4, xh);
			pstmt.setObject(5, 100);
			pstmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			pstmt.close();
			conn.close();
		}
	}
}