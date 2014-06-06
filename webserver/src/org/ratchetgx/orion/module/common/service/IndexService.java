/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.module.common.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.common.entity.SsMenu;
import org.ratchetgx.orion.common.entity.SsMenuitem;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.security.SsfwGrantedAuthority;
import org.ratchetgx.orion.security.SsfwUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author hrfan
 */
@Service
public class IndexService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private DbUtil dbUtil;

	public List<SsMenuitem> createMenu() {
		SsfwUserDetails userDetails = (SsfwUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		List<SsMenuitem> menuItems = new ArrayList<SsMenuitem>();
		// 取得用户角色
		Collection<SsfwGrantedAuthority> list = (Collection) userDetails
				.getAuthorities();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			SsfwGrantedAuthority ga = (SsfwGrantedAuthority) iter.next();
			String role = ga.getAuthority();
			try {
				menuItems.addAll(createMenu(role));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return menuItems;
	}

	public List<SsMenuitem> createAnonymousMenu() {
		List<SsMenuitem> menuItems = new ArrayList<SsMenuitem>();
		Collection<SsfwGrantedAuthority> list = new ArrayList<SsfwGrantedAuthority>();
		list.add(new SsfwGrantedAuthority("ROLE_ANONYMOUS"));
		Iterator<SsfwGrantedAuthority> iter = list.iterator();
		while (iter.hasNext()) {
			SsfwGrantedAuthority ga = (SsfwGrantedAuthority) iter.next();
			String role = ga.getAuthority();
			try {
				menuItems.addAll(createMenu(role));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return menuItems;
	}

	/**
	 * 创建role角色的菜单
	 * 
	 * @param role
	 * @return
	 */
	public List<SsMenuitem> createMenu(final String role) throws Exception {
		List<SsMenuitem> menuItems = new ArrayList<SsMenuitem>();
		final List<SsMenu> menuList = new ArrayList<SsMenu>();
		try {
			dbUtil.execute(
					"select WID,NAME,MEMO,ROLE from SS_MENU where ROLE = ?",
					new IPreparedResultSetProcessor() {
						public void processResultSet(ResultSet rs)
								throws SQLException {
							while (rs.next()) {
								SsMenu ssMenu = new SsMenu();
								ssMenu.setWid(rs.getString(1));
								ssMenu.setName(rs.getString(2));
								ssMenu.setMemo(rs.getString(3));
								menuList.add(ssMenu);
							}
						}

						public void processPreparedStatement(
								PreparedStatement ps) throws SQLException {
							ps.setString(1, role);
						}
					});

			if (!CollectionUtils.isEmpty(menuList)) {
				SsMenu menu = menuList.get(0);
				menuItems = getRecursionMenuitemByParent(null, menu);
			}
			return menuItems;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		return menuItems;
	}

	private List<SsMenuitem> getRecursionMenuitemByParent(
			final SsMenuitem parent, final SsMenu menu) throws Exception {
		final List<SsMenuitem> menuitemList = new ArrayList<SsMenuitem>();
		if (menu != null) {
			StringBuffer sql = new StringBuffer(
					"select WID,NAME,ICON_PATH,MODULE_WID,MENU_WID,MEMO,PARENT_WID,INDEXED,PATH from "
							+ " SS_MENUITEM where MENU_WID = ?");
			if (parent == null) {
				sql.append(" and PARENT_WID is null");
			} else {
				sql.append(" and PARENT_WID = ?");
			}

			sql.append(" order by INDEXED");
			dbUtil.execute(sql.toString(), new IPreparedResultSetProcessor() {
				public void processResultSet(ResultSet rs) throws SQLException {
					while (rs.next()) {
						SsMenuitem menuItem = new SsMenuitem();
						menuItem.setWid(rs.getString(1));
						menuItem.setName(rs.getString(2));
						menuItem.setIconPath(rs.getString(3));
						menuItem.setModuleId(rs.getString(4));
						menuItem.setMenu(menu);
						menuItem.setMemo(rs.getString(6));
						menuItem.setParent(parent);
						menuItem.setIndexed(rs.getInt(8));
						menuItem.setPath(rs.getString(9));
						menuitemList.add(menuItem);
					}
				}

				public void processPreparedStatement(PreparedStatement ps)
						throws SQLException {
					ps.setString(1, menu.getWid());
					if (parent != null) {
						ps.setString(2, parent.getWid());
					}
				}
			});

			for (Iterator<SsMenuitem> iterator = menuitemList.iterator(); iterator
					.hasNext();) {
				final SsMenuitem menuitem = (SsMenuitem) iterator.next();
				try {
					List<SsMenuitem> children = getRecursionMenuitemByParent(
							menuitem, menu);
					menuitem.setChildren(children);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}

		return menuitemList;
	}

	public int getMenuIndexed(final String menuItemWid) throws Exception {
		String sql = "select INDEXED from SS_MENUITEM where WID = "
				+ " (select PARENT_WID from SS_MENUITEM where WID = ?) ";
		final List<Integer> list = new ArrayList<Integer>();
		dbUtil.execute(sql, new IPreparedResultSetProcessor() {
			public void processResultSet(ResultSet rs) throws SQLException {
				if (rs.next()) {
					list.add(rs.getInt(1));
				}
			}

			public void processPreparedStatement(PreparedStatement ps)
					throws SQLException {
				ps.setString(1, menuItemWid);
			}
		});
		if (list.size() == 0) {
			return 0;
		}
		return list.get(0);
	}

	public String getMenuNames(final String menuItemWid) throws Exception {
		final List<String> list = new ArrayList<String>();
		String sql = "select m1.name,m2.name from ss_menuitem m1 inner "
				+ " join ss_menuitem m2 on (m1.wid = m2.parent_wid) where m2.wid = ?";

		dbUtil.execute(sql, new IPreparedResultSetProcessor() {

			public void processResultSet(ResultSet rs) throws SQLException {
				if (rs.next()) {
					String path = rs.getString(1) + "->" + rs.getString(2);
					list.add(path);
				}
			}

			public void processPreparedStatement(PreparedStatement ps)
					throws SQLException {
				ps.setString(1, menuItemWid);
			}
		});

		return list.get(0);
	}

	public String getMenupath(final String menuItemWid) throws Exception {
		final List<String> list = new ArrayList<String>();
		dbUtil.execute("select PATH,MODULE_WID from SS_MENUITEM where WID = ?",
				new IPreparedResultSetProcessor() {

					public void processResultSet(ResultSet rs)
							throws SQLException {
						if (rs.next()) {
							list.add(rs.getString(1));
							list.add(rs.getString(2));
						}
					}

					public void processPreparedStatement(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, menuItemWid);
					}
				});

		String path = list.get(0);
		final String moduleId = list.get(1);
		if (StringUtils.isBlank(path) && !StringUtils.isBlank(moduleId)) {
			final List<String> modulePath = new ArrayList<String>();
			dbUtil.execute(
					"select PATH from SS_MODULEPATH where MODULE_WID = ? "
							+ "order by INDEXED",
					new IPreparedResultSetProcessor() {

						public void processResultSet(ResultSet rs)
								throws SQLException {
							if (rs.next()) {
								modulePath.add(rs.getString(1));
							}
						}

						public void processPreparedStatement(
								PreparedStatement ps) throws SQLException {
							ps.setString(1, moduleId);
						}
					});

			path = modulePath.get(0);
		}
		return path;
	}
}
