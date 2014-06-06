package org.ratchetgx.orion.common.shortcutmenu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.entity.SsMenuitem;
import org.ratchetgx.orion.common.shortcutmenu.ShortcutMenuService;
import org.ratchetgx.orion.common.util.AaAUtil;
import org.ratchetgx.orion.common.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class ShortcutMenuServiceImpl implements ShortcutMenuService {

	private Logger log = LoggerFactory.getLogger(ShortcutMenuServiceImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private AaAUtil aaAUtil;

	@Autowired
	private DbUtil dbUtil;

	public void addShortcutMenuItem(final String userId, final String menuItemId)
			throws Exception {
		// 获取已授权的菜单项
		List<SsMenuitem> menuItems = aaAUtil.getMenuitemsByUserId(userId);
		// 如果用户还没授权
		if (CollectionUtils.isEmpty(menuItems)) {
			throw new Exception("用户ID:" + userId + "没有权限，不能添加快捷菜单ID:"
					+ menuItemId + "。");
		}
		// 查找已授权的菜单项中是否包含要授权的菜单项
		int i = 0;
		for (; i < menuItems.size(); i++) {
			SsMenuitem ssMenu = menuItems.get(i);
			if (ssMenu.getWid().equals(menuItemId)) {
				break;
			}
		}
		if (i == menuItems.size()) {
			throw new Exception("用户ID:" + userId + "没有权限，不能添加快捷菜单ID:"
					+ menuItemId + "。");
		}
		// 是否已添加过
		int exist = jdbcTemplate
				.queryForInt(
						"select count(*) from SS_SHORTCUT_MENU where MENU_ITEM_ID = ? and USER_ID = ?",
						new Object[] { menuItemId, userId });
		if (exist > 0) {
			return;
		}
		// 保存菜单项
		jdbcTemplate
				.update("insert into SS_SHORTCUT_MENU (WID,MENU_ITEM_ID,USER_ID) values (?,?,?)",
						new Object[] { dbUtil.getSysguid(), menuItemId, userId });
	}

	public void removeShortcutMenuItem(String shortcutMenuId) {
		jdbcTemplate.update("delete from SS_SHORTCUT_MENU where wid = ?",
				new Object[] { shortcutMenuId });
	}

	public void removeShortcutMenuItem(String userId, String menuId) {
		jdbcTemplate
				.update("delete from SS_SHORTCUT_MENU where USER_ID = ? and MENU_ITEM_ID = ?",
						new Object[] { userId, menuId });
	}

	public List<SsMenuitem> getShortcutMenuItem(String userId) {
		// 获取已添加的菜单项
		List<Map<String, Object>> userShortcutMenu = jdbcTemplate.queryForList(
				"select MENU_ITEM_ID from SS_SHORTCUT_MENU where USER_ID = ?",
				new Object[] { userId });
		// 还没添加过菜单项
		if (CollectionUtils.isEmpty(userShortcutMenu)) {
			return new ArrayList<SsMenuitem>();
		}
		// 删除已没权限的菜单项
		List<SsMenuitem> menuItems = aaAUtil.getMenuitemsByUserId(userId);
		List<String> hasPermistionShortcutMenuItem = new ArrayList<String>();
		for (int i = 0; i < userShortcutMenu.size(); i++) {
			Map<String, Object> map = userShortcutMenu.get(i);
			String menuItemId = (String) map.get("MENU_ITEM_ID");
			int j = 0;
			for (; j < menuItems.size(); j++) {
				SsMenuitem ssMenuitem = menuItems.get(j);
				if (ssMenuitem.getWid().equals(menuItemId)) {
					break;
				}
			}
			// 找到
			if (j < menuItems.size()) {
				hasPermistionShortcutMenuItem.add(menuItemId);
			}
		}
		// TODO 删除已没有权限访问的快捷菜单
		if (log.isDebugEnabled()) {
			StringBuffer strBuffer = new StringBuffer("用户ID:" + userId
					+ "快捷菜单，");
			for (int i = 0; i < hasPermistionShortcutMenuItem.size(); i++) {
				String menuItemId = hasPermistionShortcutMenuItem.get(i);
				strBuffer.append(menuItemId).append(",");
			}
			strBuffer.append("。");
			log.debug(strBuffer.toString());
		}
		// 根据菜单项ID，获取菜单对象保存到result集合中返回。
		List<SsMenuitem> result = new ArrayList<SsMenuitem>(
				userShortcutMenu.size());
		for (int i = 0; i < userShortcutMenu.size(); i++) {
			Map<String, Object> map = userShortcutMenu.get(i);
			String menuItemId = (String) map.get("MENU_ITEM_ID");
			SsMenuitem ssMenuitem = aaAUtil.getMenuitemById(menuItemId);
			if (ssMenuitem != null) {
				result.add(ssMenuitem);
			}
		}
		return result;
	}
}
