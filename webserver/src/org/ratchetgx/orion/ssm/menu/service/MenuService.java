package org.ratchetgx.orion.ssm.menu.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.menu.dao.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {
	private MenuDao menuDao;

        @Autowired
	public void setMenuDao(MenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public List<Map<String, String>> list() {
		return menuDao.list();
	}

	public Map<String, String> load(final String menuWid) {
		return menuDao.load(menuWid);
	}

	public void edit(final String wid, final String name, final String memo,final String role) {
		menuDao.edit(wid, name, memo,role);
	}

	@Transactional
	public void delete(final String menuWid) {
		menuDao.delete(menuWid);
	}
	
	@Transactional
	public void delete(final List<String> menuWids) {
		Iterator<String> menuWidItr = menuWids.iterator();
		while(menuWidItr.hasNext()){
			String menuWid = menuWidItr.next();
			menuDao.delete(menuWid);
		}
	}

    public List<String> listUnSelectedRoles() {
        return menuDao.listUnSelectedRoles();
    }

}
