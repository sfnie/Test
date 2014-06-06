package org.ratchetgx.orion.ssm.menu.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.menu.service.MenuItemService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "menuItem")
public class MenuItemController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private MenuItemService menuItemService;

    @Autowired
    public void setMenuItemService(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    /**
     * 列出功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String menuWid = request.getParameter("menuWid");
        final String parentMenuItemWid = request.getParameter("menuItemWid");

        log.info("menuWid=" + menuWid + ";parentMenuItemWid="
                + parentMenuItemWid);

        List<Map<String, String>> menuItems = menuItemService.listItems(
                menuWid, parentMenuItemWid);
        JSONArray moduleArray = new JSONArray();
        Iterator<Map<String, String>> menuItemItr = menuItems.iterator();
        while (menuItemItr.hasNext()) {
            Map<String, String> map = menuItemItr.next();

            JSONObject obj = new JSONObject();
            obj.put("id", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("iconPath", map.get("icon_path"));
            obj.put("path", map.get("path"));
            obj.put("moduleWid", map.get("module_wid"));
            obj.put("menuWid", map.get("menu_wid"));
            obj.put("memo", map.get("memo"));
            obj.put("parent_wid", map.get("parent_wid"));

            obj.put("text", map.get("name"));

            moduleArray.put(obj);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(moduleArray.toString());

        return null;
    }

    @RequestMapping(value = "load")
    public ModelAndView load(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String menuItemWid = request.getParameter("menuItemWid");
        log.info("menuItemWid:" + menuItemWid);

        Map<String, String> menuItem = menuItemService.loadItem(menuItemWid);

        JSONObject obj = new JSONObject();
        obj.put("wid", menuItem.get("wid"));
        obj.put("name", menuItem.get("name"));
        obj.put("iconPath", menuItem.get("icon_path"));
        obj.put("path", menuItem.get("path"));
        obj.put("moduleWid", menuItem.get("module_wid"));
        obj.put("moduleName", menuItem.get("module_name"));
        obj.put("menuWid", menuItem.get("menu_wid"));
        obj.put("memo", menuItem.get("memo"));
        obj.put("parent_wid", menuItem.get("parent_wid"));

        JSONObject rev = new JSONObject();
        rev.put("success", true);
        rev.put("data", obj);

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 增加功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "add")
    public ModelAndView add(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String name = request.getParameter("name");
        final String path = request.getParameter("path");
        final String iconPath = request.getParameter("iconPath");
        final String moduleWid = request.getParameter("moduleWid");
        final String menuWid = request.getParameter("menuWid");
        final String memo = request.getParameter("memo");
        final String parent_wid = request.getParameter("parent_wid");

        log.info(name + ":" + path + ":" + moduleWid + ":" + memo + ":"
                + parent_wid + ":" + iconPath);

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.add(name, path, moduleWid, menuWid, memo,
                    parent_wid, iconPath);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 编辑功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "edit")
    public ModelAndView edit(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String wid = request.getParameter("wid");

        final String name = request.getParameter("name");
        final String iconPath = request.getParameter("iconPath");
        final String path = request.getParameter("path");
        final String moduleWid = request.getParameter("moduleWid");
        final String menuWid = request.getParameter("menuWid");
        final String memo = request.getParameter("memo");
        final String parentWid = request.getParameter("parent_wid");

        log.info(wid + ":" + name + ":" + path + ":" + moduleWid + ":" + memo
                + ":" + parentWid + ":" + iconPath);

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.edit(wid, name, path, moduleWid, menuWid, memo,
                    parentWid, iconPath);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除菜单项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "remove")
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuItemWid = request.getParameter("menuItemWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.delete(menuItemWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除菜单项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "up")
    public ModelAndView up(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuItemWid = request.getParameter("menuItemWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.up(menuItemWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除菜单项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "down")
    public ModelAndView down(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuItemWid = request.getParameter("menuItemWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.down(menuItemWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 排序菜单项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "sort")
    public ModelAndView sort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuItemWid = request.getParameter("menuItemWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.sort(menuItemWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 排序顶级菜单项
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "topSort")
    public ModelAndView topSort(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuWid = request.getParameter("menuWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuItemService.topSort(menuWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 从模块树拖曳至菜单树事件响应
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "dd")
    public ModelAndView dd(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String moduleWid = request.getParameter("moduleWid");
        final String menuItemWid = request.getParameter("menuItemWid");
        final String menuWid = request.getParameter("menuWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            Map map = menuItemService.dd(moduleWid, menuItemWid,menuWid);

            JSONObject obj = new JSONObject();

            obj.put("wid", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("iconPath", map.get("icon_path"));
            obj.put("path", map.get("path"));
            obj.put("moduleWid", map.get("module_wid"));
            obj.put("moduleName", map.get("module_name"));
            obj.put("menuWid", map.get("menu_wid"));
            obj.put("memo", map.get("memo"));
            obj.put("parent_wid", map.get("parent_wid"));

            rev.put("data", obj);

        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }
}
