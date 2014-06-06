package org.ratchetgx.orion.ssm.menu.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.menu.service.CreateMenuService;
import org.ratchetgx.orion.ssm.menu.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author hrfan
 *
 */
@Controller
@RequestMapping(value = "menu")
public class MenuController extends MultiActionController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private MenuService menuService;

    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
    private CreateMenuService createMenuService;

    @Autowired
    public void setCreateMenuService(CreateMenuService createMenuService) {
        this.createMenuService = createMenuService;
    }

    @RequestMapping(method = RequestMethod.GET,value="index")
    public String index() {
        return "menu/index";
    }

    @RequestMapping(value = "list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        JSONObject rev = new JSONObject();
        List<Map<String, String>> menus = menuService.list();
        JSONArray menuArray = new JSONArray();

        Iterator<Map<String, String>> menuItr = menus.iterator();
        while (menuItr.hasNext()) {
            Map<String, String> map = menuItr.next();

            JSONObject obj = new JSONObject();
            obj.put("wid", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("memo", map.get("memo"));
            obj.put("role", map.get("role"));

            obj.put("text", map.get("name"));

            menuArray.put(obj);
        }

        rev.put("menus", menuArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value = "load")
    public ModelAndView load(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String menuWid = request.getParameter("wid");

        JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            Map<String, String> map = menuService.load(menuWid);

            JSONObject obj = new JSONObject();
            obj.put("wid", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("memo", map.get("memo"));
            obj.put("role", map.get("role"));

            rev.put("data", obj);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除菜单
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "remove")
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String menuWidsStr = request.getParameter("menuWids");

        JSONArray menuWidArray = new JSONArray(menuWidsStr);
        List<String> menuWids = new ArrayList<String>();
        for (int i = 0; i < menuWidArray.length(); i++) {
            menuWids.add(menuWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuService.delete(menuWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 新增菜单
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
        final String memo = request.getParameter("memo");
        final String role = request.getParameter("role");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            createMenuService.createMenu(name, memo,role);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value = "edit")
    public ModelAndView edit(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String wid = request.getParameter("wid");
        final String name = request.getParameter("name");
        final String memo = request.getParameter("memo");
        final String role = request.getParameter("role");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            menuService.edit(wid, name, memo,role);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }
    
    @RequestMapping(value = "listUnSelectedRoles")
	public ModelAndView listUnSelected(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<String> roles = menuService.listUnSelectedRoles();
		JSONObject rev = new JSONObject();
		JSONArray roleArray = new JSONArray();
		Iterator<String> roleItr = roles.iterator();
		while (roleItr.hasNext()) {
			String role = roleItr.next();
			JSONObject roleObject = new JSONObject();
			roleObject.put("role", role);
			roleArray.put(roleObject);
		}
		rev.put("roles", roleArray);

		response.setContentType("text/json;charset=utf-8");
		response.getWriter().println(rev);

		return null;
	}
}
