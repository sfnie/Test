package org.ratchetgx.orion.common.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ratchetgx.orion.common.entity.SsMenu;
import org.ratchetgx.orion.common.entity.SsMenuitem;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 认证和授权工具类
 * 
 * @author hrfan
 * 
 */
@Component
public class AaAUtil {

	private Logger log = LoggerFactory.getLogger(AaAUtil.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 获取角色的菜单
	 * 
	 * @param role
	 * @return
	 */
	public SsMenu getMenuByRole(String role) {
		return null;
	}
	/**
	 * 获取某用户的菜单列表
	 * 
	 * @param userID
	 * @return
	 */
	public List<SsMenu> getMenusByUserID(String userID) {
		return null;
	}
	/**
	 * 根据用户Id，获取菜单项。
	 * 
	 * @param userId
	 *            用户ID
	 * @return 菜单项列表
	 */
	public List<SsMenuitem> getMenuitemsByUserId(String userId) {
		SsfwUserDetails userDetails = (SsfwUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		Collection<GrantedAuthority> authorites = userDetails.getAuthorities();
		// 用户还没角色
		if (CollectionUtils.isEmpty(authorites)) {
			if (log.isDebugEnabled()) {
				log.debug("用户ID:" + userId + "，没有角色，菜单项为空。");
			}
			return new ArrayList<SsMenuitem>();
		}
		// 查询已授权的菜单项
		StringBuffer strBuffer = new StringBuffer(
				"select menuitem.wid,menuitem.name from ss_menu menu join SS_MENUITEM menuitem on "
						+ " (menu.wid = menuitem.menu_wid) where menu.role in (");
		List<String> sqlParam = new ArrayList<String>();
		for (Iterator<GrantedAuthority> iter = authorites.iterator(); iter
				.hasNext();) {
			GrantedAuthority arantedAuthority = iter.next();
			String role = arantedAuthority.getAuthority();
			strBuffer.append("?").append(",");
			sqlParam.add(role);
		}
		String sql = strBuffer.substring(0, strBuffer.length() - 1) + ")";
		List<SsMenuitem> list = jdbcTemplate.query(sql, sqlParam.toArray(),
				new RowMapper<SsMenuitem>() {
					public SsMenuitem mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						SsMenuitem ssMenuitem = new SsMenuitem();
						ssMenuitem.setWid(rs.getString(1));
						ssMenuitem.setName(rs.getString(2));
						return ssMenuitem;
					}
				});
		return list;
	}
	/**
	 * 获取菜单项
	 * 
	 * @param menuItemId
	 *            菜单项Id
	 * @return 菜单项对象
	 */
	public SsMenuitem getMenuitemById(String menuItemId) {
		return jdbcTemplate.queryForObject("select wid,name from SS_MENUITEM where wid = ?",
				new Object[] { menuItemId }, new RowMapper<SsMenuitem>() {
					public SsMenuitem mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						SsMenuitem ssMenuitem = new SsMenuitem();
						ssMenuitem.setWid(rs.getString(1));
						ssMenuitem.setName(rs.getString(2));
						return ssMenuitem;
					}
				});
	}
}
