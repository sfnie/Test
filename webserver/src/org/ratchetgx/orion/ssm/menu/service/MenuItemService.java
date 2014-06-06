package org.ratchetgx.orion.ssm.menu.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.menu.dao.MenuItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuItemService {

    private MenuItemDao menuItemDao;

    @Autowired
    public void setMenuItemDao(MenuItemDao menuItemDao) {
        this.menuItemDao = menuItemDao;
    }

    public List<Map<String, String>> listItems(final String menuWid,
            final String parentMenuItemWid) {
        return menuItemDao.listItems(menuWid, parentMenuItemWid);
    }

    public Map<String, String> loadItem(String menuItemWid) {
        return menuItemDao.loadItem(menuItemWid);
    }

    public void add(String name, String path, String moduleWid, String menuWid,
            String memo, String parentWid, String icon_path) {
        menuItemDao.add(name, path, moduleWid, menuWid, memo, parentWid,
                icon_path);
    }

    public void edit(String wid, String name, String path, String moduleWid,
            String menuWid, String memo, String parentWid, String icon_path) {
        menuItemDao.edit(wid, name, path, moduleWid, menuWid, memo, parentWid,
                icon_path);

    }

    public void delete(String menuItemWid) {
        menuItemDao.delete(menuItemWid);
    }

    @Transactional
    public void up(String menuItemWid) {
        menuItemDao.up(menuItemWid);
    }

    @Transactional
    public void down(String menuItemWid) {
        menuItemDao.down(menuItemWid);
    }

    @Transactional
    public void sort(String menuItemWid) {
        menuItemDao.sort(menuItemWid);
    }

    @Transactional
    public void topSort(String menuWid) {
        menuItemDao.topSort(menuWid);
    }

    public Map dd(String moduleWid, String menuItemWid,String menuWid) {
        return menuItemDao.dd(moduleWid,menuItemWid,menuWid);
    }
}
