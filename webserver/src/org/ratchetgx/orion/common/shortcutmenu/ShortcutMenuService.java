package org.ratchetgx.orion.common.shortcutmenu;

import java.util.List;

import org.ratchetgx.orion.common.entity.SsMenuitem;

public interface ShortcutMenuService {

	/**
	 * 添加菜单项
	 * 
	 * @param userId
	 *            用户ID
	 * @param menuItemId
	 *            菜单项ID
	 * @throws 没有权限添加快捷菜单        
	 */
	public void addShortcutMenuItem(String userId, String menuItemId)
			throws Exception;

	/**
	 * 删除菜单项
	 * 
	 * @param userId
	 *            用户ID
	 * @param menuItemId
	 *            菜单项ID
	 */
	public void removeShortcutMenuItem(String userId, String menuItemId);

	/**
	 * 删除菜单项
	 * 
	 * @param shortcutMenuId
	 *            菜单项ID
	 */
	public void removeShortcutMenuItem(String shortcutMenuId);

	/**
	 * 获取菜单项
	 * 
	 * @param userId
	 *            用户ID
	 * @return 菜单项集合
	 */
	public List<SsMenuitem> getShortcutMenuItem(String userId);
}
