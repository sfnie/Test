/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.ssm.menu.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import Freeze.LinkedList;

/**
 * 
 * @author hrfan
 */
@Repository
public class ModuleExcludesMenuDao {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, String>> list(final String parentModuleWid,
			String menuId) {
		String sql = "select WID,NAME,MEMO,PARENT_WID from SS_MODULE where PARENT_WID = ?";

		if ("000000".equals(parentModuleWid) || parentModuleWid == null) {
			sql += " or PARENT_WID is null";
		}

		log.info(sql);

		List<Map<String, String>> modules = (List<Map<String, String>>) jdbcTemplate
				.query(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, parentModuleWid);

					}
				}, new ResultSetExtractor() {
					public List<Map<String, String>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Map<String, String>> modules = new ArrayList<Map<String, String>>();

						while (rs.next()) {
							Map<String, String> map = new HashMap<String, String>();

							map.put("wid", rs.getString("wid"));
							map.put("name", rs.getString("name"));
							map.put("memo", rs.getString("memo"));
							map.put("parent_wid", rs.getString("parent_wid"));

							modules.add(map);
						}

						return modules;
					}
				});

		return modules;

	}

	public List<Map<String, String>> searchlist(final String name) {
		String sql = "select WID,NAME,MEMO,PARENT_WID from SS_MODULE where name=?";

		log.info(sql);

		List<Map<String, String>> modules = (List<Map<String, String>>) jdbcTemplate
				.query(sql, new PreparedStatementSetter() {
					public void setValues(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, name);

					}
				}, new ResultSetExtractor() {
					public List<Map<String, String>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Map<String, String>> modules = new ArrayList<Map<String, String>>();

						while (rs.next()) {
							Map<String, String> map = new HashMap<String, String>();

							map.put("wid", rs.getString("wid"));
							map.put("name", rs.getString("name"));
							map.put("memo", rs.getString("memo"));
							map.put("parent_wid", rs.getString("parent_wid"));
							String path = getPath(rs.getString("parent_wid"));
							String[] str=path.split(";");
							map.put("path", str[0]+rs.getString("name"));
							map.put("wids", str[1]+rs.getString("wid"));
							modules.add(map);
						}

						return modules;
					}
				});

		return modules;

	}

	private String wid;

	public String getPath(final String parent_wid) {
		String sql = "select WID,NAME,MEMO,PARENT_WID from SS_MODULE where wid=?";

		log.info(sql);
		log.info(parent_wid);
		wid = parent_wid;
		String path = "";
		String wids="";
		List<String> pathList=new ArrayList<String>();
		List<String> widList=new ArrayList<String>();
		while (wid!=null) {
			List<Map<String, String>> modules = (List<Map<String, String>>) jdbcTemplate
					.query(sql, new PreparedStatementSetter() {
						public void setValues(PreparedStatement pstmt)
								throws SQLException {
							pstmt.setString(1, wid);

						}
					}, new ResultSetExtractor() {
						public List<Map<String, String>> extractData(
								ResultSet rs) throws SQLException,
								DataAccessException {
							List<Map<String, String>> modules = new ArrayList<Map<String, String>>();

							while (rs.next()) {
								Map<String, String> map = new HashMap<String, String>();

								map.put("wid", rs.getString("wid"));
								map.put("name", rs.getString("name"));
								map.put("memo", rs.getString("memo"));
								map.put("parent_wid",
										rs.getString("parent_wid"));
								wid = rs.getString("parent_wid");
								modules.add(map);
							}

							return modules;
						}
					});

			if (modules != null && modules.size() > 0) {
				String name=modules.get(0).get("name");
				String wid=modules.get(0).get("wid");
				//path += name;
				pathList.add(name);
				widList.add(wid);
			}
			
		}
		for(int i=pathList.size()-1;i>=0;i--)
		{
			path+=pathList.get(i)+"-->";
			wids+=widList.get(i)+"-->";
		}
		return path+";"+wids;
	}
}
