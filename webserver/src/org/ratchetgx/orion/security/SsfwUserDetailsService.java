package org.ratchetgx.orion.security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ratchetgx.orion.security.ids.IdsAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SsfwUserDetailsService implements UserDetailsService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public AttachInfoGetter attachInfoGetter;

	public void setAttachInfoGetter(AttachInfoGetter attachInfoGetter) {
		this.attachInfoGetter = attachInfoGetter;
	}

	public UserDetails loadUserByUsername(final String loginName)
			throws UsernameNotFoundException, DataAccessException {
		final String bh = loginName.toLowerCase();
		SsfwUserDetails userDetails = null;
		try {
			// 获取用户基本信息
			String sql = "SELECT wid,lower(bh) as bh,name,password,email FROM ss_user where LOWER(bh)=?";
			Map<String, String> baseInfo = jdbcTemplate.query(sql,
					new PreparedStatementSetter() {

						public void setValues(PreparedStatement pstmt)
								throws SQLException {
							pstmt.setString(1, bh);
						}
					}, new ResultSetExtractor<Map<String, String>>() {

						public Map<String, String> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Map<String, String> baseInfo = null;
							if (rs.next()) {
								String username = rs.getString("name");
								String password = rs.getString("password");
								String email = rs.getString("email");
								String wid = rs.getString("wid");
								String bh = rs.getString("bh");

								baseInfo = new HashMap<String, String>();
								baseInfo.put("name", username);
								baseInfo.put("password", password);
								baseInfo.put("email", email);
								baseInfo.put("wid", wid);
								baseInfo.put("bh", bh);
							}

							return baseInfo;
						}
					});

			if (baseInfo == null) {
				baseInfo = loadUserByBHFromView(loginName);
			}

			if (baseInfo == null) {
				throw new UsernameNotFoundException(loginName);
			}

			// 获取用户组列表
			sql = "SELECT distinct g.name FROM ss_user u LEFT JOIN ss_group_rel_user r ON u.wid = r.user_wid LEFT JOIN ss_group g ON r.group_wid = g.wid WHERE LOWER(u.bh) = ?";
			Collection<String> groups = jdbcTemplate.query(sql,
					new PreparedStatementSetter() {

						public void setValues(PreparedStatement pstmt)
								throws SQLException {
							pstmt.setString(1, bh);
						}
					}, new ResultSetExtractor<Collection<String>>() {

						public Collection<String> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Collection<String> groups = new ArrayList<String>();
							while (rs.next()) {
								groups.add(rs.getString("name"));
							}

							return groups;
						}
					});

			// 获取用户授权列表
			Collection<String> roles = new ArrayList<String>();
			// 添加从视图中获取的角色信息
			if (baseInfo.get("role") != null) {
				String role = baseInfo.get("role");
				if (role.indexOf(",") > 0) {
					String[] roleArray = role.split(",");
					for (int i = 0; i < roleArray.length; i++) {
						roles.add(roleArray[i]);
					}
				} else {
					roles.add(baseInfo.get("role"));
				}
			}

			sql = "SELECT r.role FROM ss_user u LEFT JOIN ss_user_rel_role r ON u.wid = r.user_wid WHERE LOWER(u.bh) = ?";
			Collection<String> userRoles = jdbcTemplate.query(sql,
					new PreparedStatementSetter() {

						public void setValues(PreparedStatement pstmt)
								throws SQLException {
							pstmt.setString(1, bh);
						}
					}, new ResultSetExtractor<Collection<String>>() {

						public Collection<String> extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							Collection<String> userRoles = new ArrayList<String>();
							while (rs.next()) {
								String role = rs.getString("role");
								if (role != null) {
									userRoles.add(role);
								}
							}

							return userRoles;
						}
					});

			roles.addAll(userRoles);

			sql = "SELECT r.role FROM ss_group g LEFT JOIN ss_group_rel_role r ON g.wid = r.group_wid WHERE g.name = ? and r.role is not null";
			Iterator<String> iter = groups.iterator();
			while (iter.hasNext()) {
				final String group = (String) iter.next();
				Collection<String> groupRoles = jdbcTemplate.query(sql,
						new PreparedStatementSetter() {

							public void setValues(PreparedStatement pstmt)
									throws SQLException {
								pstmt.setString(1, group);
							}
						}, new ResultSetExtractor<Collection<String>>() {

							public Collection<String> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								Collection<String> groupRoles = new ArrayList<String>();
								while (rs.next()) {
									String role = rs.getString("role");
									if (role != null) {
										groupRoles.add(role);
									}
								}

								return groupRoles;
							}
						});
				Iterator<String> groupRoleItr = groupRoles.iterator();
				while (groupRoleItr.hasNext()) {
					String groupRole = groupRoleItr.next();
					if (!roles.contains(groupRole)) {
						roles.add(groupRole);
					}
				}
			}

			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			Iterator<String> roleItr = roles.iterator();
			while (roleItr.hasNext()) {
				String role = roleItr.next();
				if (role != null) {
					SsfwGrantedAuthority authority = new SsfwGrantedAuthority(
							role);
					authorities.add(authority);
				}
			}

			// 从SS_V_USER_PASSWORD视图中获取用户密码
			sql = "SELECT password FROM ss_v_user_password WHERE LOWER(bh) = ?";
			String password = jdbcTemplate.query(sql,
					new PreparedStatementSetter() {

						public void setValues(PreparedStatement pstmt)
								throws SQLException {
							pstmt.setString(1, bh);
						}
					}, new ResultSetExtractor<String>() {

						public String extractData(ResultSet rs)
								throws SQLException, DataAccessException {
							String password = null;

							if (rs.next()) {
								password = rs.getString("password");
							}

							return password;
						}
					});
			if (password != null) {
				baseInfo.put("password", password);
			}

			userDetails = new SsfwUserDetails(baseInfo.get("wid"),
					baseInfo.get("bh"), baseInfo.get("name"),
					baseInfo.get("password"), authorities,
					baseInfo.get("email"), groups);

			userDetails.setUserLoginType(IdsAuthenticationFilter.DB_IDENTIFIER);

			if (attachInfoGetter != null) {
				log.debug("填充用户信息。");
				Map attachedInfo = attachInfoGetter.getAttachInfo(baseInfo
						.get("bh"));
				if (attachedInfo != null) {
					userDetails.getAttached().putAll(attachedInfo);
				}
			}

		} catch (UsernameNotFoundException e) {
			log.error("", e);
			throw e;
		} catch (DataAccessException e) {
			log.error("", e);
			throw e;
		}

		return userDetails;
	}

	/**
	 * 从视图中获取用户信息和角色列表
	 * 
	 * @param bh
	 * @return
	 * @throws UsernameNotFoundException
	 * @throws DataAccessException
	 */
	public Map<String, String> loadUserByBHFromView(final String loginName)
			throws UsernameNotFoundException, DataAccessException {
		String sql = "SELECT wid,bh,name,password,email,role FROM ss_v_user WHERE LOWER(bh) = ?";
		Map<String, String> baseInfo = jdbcTemplate.query(sql,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, loginName.toLowerCase());
					}
				}, new ResultSetExtractor<Map<String, String>>() {

					public Map<String, String> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						Map<String, String> baseInfo = null;
						if (rs.next()) {
							String username = rs.getString("name");
							String email = rs.getString("email");
							String role = rs.getString("role");
							String wid = rs.getString("wid");
							String bh = rs.getString("bh");
							baseInfo = new HashMap<String, String>();
							baseInfo.put("name", username);
							baseInfo.put("email", email);
							baseInfo.put("role", role);
							baseInfo.put("wid", wid);
							baseInfo.put("bh", bh);
						}

						return baseInfo;
					}
				});

		return baseInfo;
	}
}
