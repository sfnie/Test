/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.mixjaccount;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.common.util.IPreparedStatementProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import edu.sjtu.jaccount.JAccountManager;

/**
 * 
 * @author hrfan
 */
public class MixJaccountAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	@Autowired
	private DbUtil dbUtil;

	public MixJaccountAuthenticationFilter() {
		super("/j_spring_jaccount_security_check");
		setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		
		log.debug("Enter into attemptAuthentication.");
		
		String requestUriWithQueryString = request.getRequestURI();
		if (request.getQueryString() != null) {
			requestUriWithQueryString += "?" + request.getQueryString();
		}
		
		log.debug("requestURI With QueryString:" + requestUriWithQueryString);

		String mode = request.getParameter("mode");

		String bh = "";
		String password = "";
		UsernamePasswordAuthenticationToken authRequest;
		
		String loginPageUrl = request.getHeader("Referer");
		request.getSession().setAttribute(MixJAccountConstants.JACCOUNT_SESSION_LOGINED_URL, loginPageUrl);

		if ("db".equals(mode)) {// 数据库认证
			bh = request.getParameter("j_username");
			password = request.getParameter("j_password");
			
			log.debug("数据库登陆，bh=" + bh + ";password=" + password);

			// 校验用户名跟密码
			final boolean[] fixed = { false };
			
			String sql_1 = "select bh from ss_v_user t where t.bh=? and exists (select * from ss_v_user_password p where lower(p.pwdtype)='md5' and p.bh = t.bh and p.PASSWORD = md5(?))";
			String sql_2 = "select bh from ss_v_user t where t.bh=? and exists (select * from ss_v_user_password p where lower(p.pwdtype)='plain' and p.bh = t.bh and p.PASSWORD = ?)";
			String sql_3 = "select bh from ss_user t where t.bh=? and exists (select * from ss_v_user_password p where lower(p.pwdtype)='plain' and p.bh = t.bh and p.PASSWORD = ?)";
			try {
				final String f_bh = bh;
				final String f_password = password;
				dbUtil.execute(sql_1, new IPreparedResultSetProcessor() {

					public void processResultSet(ResultSet rs)
							throws SQLException {
						fixed[0] = rs.next();
					}

					public void processPreparedStatement(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, f_bh);
						pstmt.setString(2, f_password);
					}
				});
				
				if (!fixed[0]) {
					dbUtil.execute(sql_2, new IPreparedResultSetProcessor() {

						public void processResultSet(ResultSet rs)
								throws SQLException {
							fixed[0] = rs.next();
						}

						public void processPreparedStatement(
								PreparedStatement pstmt) throws SQLException {
							pstmt.setString(1, f_bh);
							pstmt.setString(2, f_password);
						}
					});
				}
				
				if (!fixed[0]) {
					dbUtil.execute(sql_3, new IPreparedResultSetProcessor() {

						public void processResultSet(ResultSet rs)
								throws SQLException {
							fixed[0] = rs.next();
						}

						public void processPreparedStatement(
								PreparedStatement pstmt) throws SQLException {
							pstmt.setString(1, f_bh);
							pstmt.setString(2, f_password);
						}
					});
				}

			} catch (SQLException e) {
				log.error("", e);
			}
			
			if(!fixed[0]){
				throw new MixJaccountAuthenticationException("数据库认证失败。");
			}

			log.debug("数据库认证:" + bh);
			
			authRequest = new MixJAccountUsernamePasswordAuthenticationToken(
					bh, password, "db");
			request.getSession().setAttribute(MixJAccountConstants.JACCOUNT_SESSION_LOGINED_MODE, "db");
			
			
		} else {// JACCOUNT认证
			
			JAccountManager jam = new JAccountManager(jamHelper.getSiteid(),
					getServletContext().getRealPath("/"));
			Hashtable ht = jam.checkLogin(request, response,
					request.getSession(), requestUriWithQueryString);
			
			if (ht == null) {
				throw new MixJaccountAuthenticationException("JAccount认证失败。");
			}
			
			/** 认证通过后的处理 */
			bh = (String) ht.get("id");
			log.debug("JACCOUNT认证:" + bh);
			
			authRequest = new MixJAccountUsernamePasswordAuthenticationToken(
					bh, "", "jaccount");
			request.getSession().setAttribute(MixJAccountConstants.JACCOUNT_SESSION_LOGINED_MODE, "jaccount");
			
			
			log.debug("===============begin jam Key-value====================");
			for (java.util.Iterator iter = ht.keySet().iterator(); iter.hasNext(); ) {
			   String key = (String)iter.next();
			   log.debug("key:" + key + ",value:" + ht.get(key));
			}
			
			/** 如登录用户为本科生，则进行相应处理 */
			final String jaData_student = (String)ht.get("student");
			final String jaData_id = (String)ht.get("id");
			final String jaData_chinesename = (String)ht.get("chinesename");
			
			if ("yes".equals(jaData_student)) { //如果是学生
				
				/** 判断登录用户是否为本科生 */
				String sql = "SELECT count(*) AS cnt FROM ("
						   + " SELECT xh FROM T_XJGL_XJXX_YJSJBXX UNION ALL "
				           + " SELECT xh FROM T_XJGL_XJXX_YJSJBXX_FZJ UNION ALL "
				           + " SELECT pth AS xh FROM T_XJGL_XJXX_PTSZCXX "
				           + ")xs WHERE xs.xh=?";
				final List rsList1 = new ArrayList();
				try {
					dbUtil.execute(sql, new IPreparedResultSetProcessor() {
	
						public void processResultSet(ResultSet rs) throws SQLException {
							if (rs.next()) {
								rsList1.add(rs.getInt("cnt"));
							}
						}
	
						public void processPreparedStatement(PreparedStatement pstmt) throws SQLException {
							pstmt.setString(1, jaData_id);
						}
					});
				} catch (SQLException e) {
					log.error("", e);
				}
				
				/** 如登录用户为本科生,则在本科生基本信息表中新增本科生记录 */
				if (rsList1.size() > 0 && ((Integer)rsList1.get(0)).intValue() == 0) {  
					
					/** 判断是否在本科生基本信息表中存在 */
					sql = "SELECT count(*) AS cnt FROM T_XJGL_XJXX_BKSJBXX WHERE xh=?";
					final List rsList2 = new ArrayList();
					try {
						dbUtil.execute(sql, new IPreparedResultSetProcessor() {
		
							public void processResultSet(ResultSet rs) throws SQLException {
								if (rs.next()) {
									rsList2.add(rs.getInt("cnt"));
								}
							}
							public void processPreparedStatement(PreparedStatement pstmt) throws SQLException {
								pstmt.setString(1, jaData_id);
							}
						});
					} catch (SQLException e) {
						log.error("", e);
					}
					
					/** 如本科生基本信息表中不存在本科生记录 */
					if (rsList2.size() > 0 && ((Integer)rsList2.get(0)).intValue() == 0) {  
						
						/** 判断是否是大四学生 */
						final List rsList3 = new ArrayList();
						Integer njxh = 0;
						if(!"7".equals(jaData_id.substring(0, 1))){
							String sNj = jaData_id.substring(1, 3);
							sql = "SELECT (substr(xndm,3,4) - " + sNj + " + 1) AS njxh FROM t_xb_xq WHERE sfdqxq='1'";
							log.debug("大四学生判断sql:" + sql);
							try {
								dbUtil.execute(sql, new IPreparedResultSetProcessor() {
									public void processResultSet(ResultSet rs) throws SQLException {
										if (rs.next()) {
											rsList3.add(rs.getInt("njxh"));
										}
									}
									public void processPreparedStatement(PreparedStatement pstmt) throws SQLException {
										
									}
								});
							} catch (SQLException e) {
								log.error("", e);
							}
							njxh = (Integer)rsList3.get(0);
						}
						
						/** 大四学生  2014-3-13加学号第一位为7的学生不校验是否大4 */
						if (njxh.intValue() >= 4 || "7".equals(jaData_id.substring(0, 1))) {  
							sql = "INSERT INTO T_XJGL_XJXX_BKSJBXX (WID,XH,XM,DLMM) VALUES(sys_guid(),?,?,substr(dbms_random.normal,16,6))";
							log.info(sql);
							try {
								dbUtil.execute(sql, new IPreparedStatementProcessor() {
				
									public void process(PreparedStatement pstmt)
											throws SQLException {
										pstmt.setString(1, jaData_id);
										pstmt.setString(2, jaData_chinesename);
										pstmt.executeUpdate();
									}
								});
							} catch (SQLException e) {
								log.error("", e);
							}
						}
						
						
					}
					
				}
				
			}
			
		}

		authRequest.setDetails(authenticationDetailsSource
				.buildDetails(request));

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private MixJAccountManagerHelper jamHelper;

	public void setJamHelper(MixJAccountManagerHelper jamHelper) {
		this.jamHelper = jamHelper;
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());
}
