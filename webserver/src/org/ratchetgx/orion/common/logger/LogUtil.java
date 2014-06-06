package org.ratchetgx.orion.common.logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.ratchetgx.orion.common.cache.CacheConstants;
import org.ratchetgx.orion.common.util.BizobjUtilHelper;
import org.ratchetgx.orion.common.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 日志工具类
 * 
 * @author zjliu
 * 
 */
public class LogUtil {
	private static Logger log = LoggerFactory.getLogger(LogUtil.class);
	@Autowired
	private DbUtil dbUtil;
	@Autowired
	private BizobjUtilHelper bizhelper;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private Cache loggerConfigCache;

	public void setLoggerConfigCache(Cache loggerConfigCache) {
		this.loggerConfigCache = loggerConfigCache;
	}

	/**
	 * 
	 * @param logmap
	 *            : 日志信息MAP
	 * @param datamap
	 *            ： 业务操作数据MAP
	 * @throws SQLException
	 */

	@Transactional
	public void addLog(final HashMap<String, Object> logmap,
			final Map<String, String> datamap) throws SQLException {

		// 记录T_SYS_LOG表日志
		final String logsql = "insert into t_sys_log(wid,actid,acttable,userid,username,acttime,actip,acturl) values(?,?,?,?,?,?,?,?)";
		jdbcTemplate.execute(logsql, new PreparedStatementCallback<Object>() {

			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				String wid = (String) logmap.get("wid");
				String actid = (String) logmap.get("actid");
				String acttable = (String) logmap.get("acttable");
				String userid = (String) logmap.get("userid");
				String username = (String) logmap.get("username");
				Date acttime = (Date) logmap.get("acttime");
				String actip = (String) logmap.get("actip");
				String acturl = (String) logmap.get("acturl");

				pstmt.setString(1, wid);
				pstmt.setString(2, actid);
				pstmt.setString(3, acttable);
				pstmt.setString(4, userid);
				pstmt.setString(5, username);
				pstmt.setTimestamp(6, new Timestamp(acttime.getTime()));
				pstmt.setString(7, actip);
				pstmt.setString(8, acturl);

				pstmt.executeUpdate();

				return null;
			}
		});

		// 只有新增、更新、删除才进行审计列转行操作插入到t_sys_log_detailed审计表
		if (logmap.get("acttable") != null
				&& ((logmap.get("acttype")).equals("3") || (logmap
						.get("acttype")).equals("4"))
				|| (logmap.get("acttype")).equals("5")) {

			final HashMap<String, Integer> colmap = (HashMap<String, Integer>) bizhelper
					.getColumnTypes(logmap.get("acttable").toString());

			final String sql = "insert into t_sys_log_detailed(wid,logwid,clomname,clomvalue,actid) values(?,?,?,?,?)";

			jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>() {

				public Object doInPreparedStatement(PreparedStatement pstmt)
						throws SQLException, DataAccessException {
					Iterator itr = colmap.keySet().iterator();
					while (itr.hasNext()) {
						String columnKey = (String) itr.next();
						if(datamap.containsKey(columnKey)){
							pstmt.setString(1, dbUtil.getSysguid());
							pstmt.setString(2, logmap.get("wid").toString());
							pstmt.setString(3, columnKey);
							pstmt.setString(
									4,
									datamap.get(columnKey) != null ? String
											.valueOf(datamap.get(columnKey)) : "");
							pstmt.setString(5, logmap.get("actid") != null ? logmap
									.get("actid").toString() : "");
							pstmt.addBatch();
						}
					}

					pstmt.executeBatch();

					return null;
				}
			});

		}

	}

	/**
	 * 
	 * @param logmap
	 *            : 日志信息MAP
	 * @param datamap
	 *            ： 业务操作数据MAP
	 * @throws SQLException
	 */

	@Transactional
	public void addSqlLog(final HashMap<String, Object> logmap)
			throws SQLException {

		final String sql = "insert into t_sys_log(wid,actid,acttable,userid,username,acttime,acttype,actip,acturl) values(?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.execute(sql, new PreparedStatementCallback<Object>() {

			public Object doInPreparedStatement(PreparedStatement pstmt)
					throws SQLException, DataAccessException {
				
				String userid = (String) logmap.get("userid");
				String username = (String) logmap.get("username");
				Date acttime = (Date) logmap.get("acttime");
				String acttype = (String) logmap.get("acttype");
				String actip = (String) logmap.get("actip");
				String acturl = (String) logmap.get("acturl");
				
				pstmt.setString(1, dbUtil.getSysguid());
				pstmt.setString(2, "");
				pstmt.setString(3, "");
				pstmt.setString(4,userid);
				pstmt.setString(5,username);
				pstmt.setTimestamp(6, new Timestamp(acttime.getTime()));
				pstmt.setString(7,acttype);
				pstmt.setString(8,actip);
				pstmt.setString(9,acturl);
				
				pstmt.executeUpdate();
				
				return null;
			}
		});

	}

	@Transactional
	public HashMap<String, String> getLogCfg(String bizobj) {
		String keyStr = CacheConstants.CACHE_ELEMENT_KEY + "." + bizobj;
		Element element = loggerConfigCache.get(keyStr);
		if (element == null) {
			synchronized (this) {
				element = loggerConfigCache.get(keyStr);
				if (element == null) {// 调用实际的方法
					try {
						final String sql = "select onoff,threadmodel from t_sys_logcfg where upper(tablename)='"
								+ bizobj.toUpperCase() + "' and rownum=1";
						final HashMap<String, String> rsmap = new HashMap<String, String>();
						jdbcTemplate.query(sql,
								new ResultSetExtractor<Object>() {
									public Object extractData(ResultSet rs)
											throws SQLException,
											DataAccessException {
										while (rs.next()) {
											rsmap.put("onoff",
													rs.getString("onoff"));
											rsmap.put("threadmodel",
													rs.getString("threadmodel"));
										}
										return null;
									}
								});
						element = new Element(keyStr, rsmap);
						loggerConfigCache.put(element);
					} catch (Throwable e) {
					}

				}
			}
		}

		return (HashMap<String, String>) element.getObjectValue();

	}
	
	public String getIpAddr(HttpServletRequest request){
	     	String ip = request.getHeader("x-forwarded-for")!=null ? request.getHeader("x-forwarded-for"):""; 
		     if(ip == null ||ip==""||ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    	 ip = request.getHeader("Proxy-Client-IP")!=null ? request.getHeader("Proxy-Client-IP") :"";            
		    }             
		     if(ip == null ||ip==""||ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    	 ip = request.getHeader("WL-Proxy-Client-IP")!=null?request.getHeader("WL-Proxy-Client-IP"):"";            
		    }             
		     if(ip == null ||ip==""|| ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
		    	 ip = request.getRemoteAddr()!=null ? request.getRemoteAddr():"";            
		    }             
	    	 
		return ip;      
	}
	
	

}
