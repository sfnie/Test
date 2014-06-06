package org.ratchetgx.orion.ssm.menu.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ratchetgx.orion.ssm.menu.dao.CreateMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 根据功能模块层级结构创建类似的菜单项层级结构。
 * 
 * @author hrfan
 * 
 */
@Service
public class CreateMenuService {
	private CreateMenuDao createMenuDao;

        @Autowired
	public void setCreateMenuDao(CreateMenuDao createMenuDao) {
		this.createMenuDao = createMenuDao;
	}

	/**
	 * 创建菜单，并按照当前模块层级结构初始化菜单项
	 * 
	 * @param name
	 * @param memo
	 */
	@Transactional
	public void createMenu(final String name, final String memo,final String role) {
		String menuWid = createMenuDao.getSysguid();
		createMenuDao.createMenu(menuWid, name, memo,role);

		Iterator<Map<String, String>> moduleItr = createMenuDao.listModules(
				null).iterator();
		while (moduleItr.hasNext()) {
			Map<String, String> module = moduleItr.next();
			createMenuItem(menuWid, module,null);
		}
	}

	/*
	 * 创建menuWid菜单的一个菜单项，其对应于模块module
	 */
	private void createMenuItem(String menuWid, Map<String, String> module,String parentWid) {
		Map<String, String> menuItem = createMenuItemByModule(menuWid, module);

		createMenuDao.createMenuItem(menuItem,parentWid);

		Iterator<Map<String, String>> childModuleItr = createMenuDao
				.listModules(module.get("wid")).iterator();
		while (childModuleItr.hasNext()) {
			Map<String, String> childModule = childModuleItr.next();
			createMenuItem(menuWid, childModule,menuItem.get("wid"));
		}
	}

	/*
	 * 创建与模块匹配的菜单
	 */
	private Map<String, String> createMenuItemByModule(String menuWid,
			Map<String, String> module) {
		String wid = createMenuDao.getSysguid();
		String moduleName = module.get("name");
		String moduleWid = module.get("wid");
		String path = getDefaultPathOfModule(module);

		Map<String, String> menuItem = new HashMap<String, String>();
		menuItem.put("wid", wid);
		menuItem.put("name", moduleName);
		menuItem.put("menuWid", menuWid);
		menuItem.put("moduleWid", moduleWid);
		menuItem.put("path", path);

		return menuItem;
	}

	/*
	 * 模块的默认路径
	 */
	private String getDefaultPathOfModule(Map<String, String> module) {
		String moduleWid = module.get("wid");
		String path = createMenuDao.getDefaultPathOfModule(moduleWid).get(
				"path");

		return path;
	}
}
