package org.ratchetgx.orion.security.yjs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.ratchetgx.orion.security.SsfwGrantedAuthority;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.ratchetgx.orion.security.SsfwUserDetailsService;
import org.ratchetgx.orion.security.ids.IdsAuthenticationFilter;

public class ZzfwsUserDetailsService extends SsfwUserDetailsService {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		super.setJdbcTemplate(jdbcTemplate);
		this.jdbcTemplate = jdbcTemplate;
	}

	public ZzfwsUserDetailsService() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(final String loginName)
			throws UsernameNotFoundException, DataAccessException {
		final String bh = loginName.toLowerCase();
		//继承父类的方法，获取父类的方法信息
		SsfwUserDetails userDetails = (SsfwUserDetails)super.loadUserByUsername(loginName);		
		
		//取得父类获取的角色信息
		Collection<String> roles = new ArrayList<String>();
		
		Collection<GrantedAuthority> authority = (Collection<GrantedAuthority>) userDetails.getAuthorities();
		Iterator roleItr = authority.iterator();		
		while (roleItr.hasNext()) {
			SsfwGrantedAuthority ga = (SsfwGrantedAuthority) roleItr.next();
			String role = ga.getAuthority();
			roles.add(role);			
		}
		
		//根据用户编号获取用户的角色信息
		String sql = "SELECT bh,NAME,ROLE FROM ss_v_user  WHERE LOWER(bh) = ?";
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
         
		//将和父类重复的角色删除
		roles.removeAll(userRoles);
		//添加至角色列表中
		roles.addAll(userRoles);
		
		// 校验是否新生信息，如果是新增，只保留新生权限	
		CheckStusent checkStusent= new CheckStusent();
		checkStusent.setJdbcTemplate(jdbcTemplate);
		Collection<String> allroles = checkStusent.eval(bh, roles);		
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		Iterator<String> allroleItr = allroles.iterator();
		while (allroleItr.hasNext()) {
				String role = allroleItr.next();
				if (role != null) {
					SsfwGrantedAuthority ssfwAuthority = new SsfwGrantedAuthority(role);
					authorities.add(ssfwAuthority);
				}
		}
		
		
		userDetails = new SsfwUserDetails(userDetails.getWid(),
				userDetails.getBh(), userDetails.getUsername(),
				userDetails.getPassword(), authorities,
				userDetails.getEmail(), userDetails.getGroups());

		userDetails.setUserLoginType(IdsAuthenticationFilter.DB_IDENTIFIER);		
		 
		return userDetails;
	}

}
