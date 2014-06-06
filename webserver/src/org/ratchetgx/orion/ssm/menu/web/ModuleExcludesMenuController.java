/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.ssm.menu.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.menu.service.ModuleExcludesMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author hrfan
 */
@Controller
@RequestMapping(value = "moduleExcludesMenu")
public class ModuleExcludesMenuController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ModuleExcludesMenuService service;

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
        final String menuItemParentId = request.getParameter("parent_id");
        final String menuId = request.getParameter("menuId");

        List<Map<String, String>> modules = service.list(menuItemParentId, menuId);
        JSONArray moduleArray = new JSONArray();
        Iterator<Map<String, String>> moduleItr = modules.iterator();
        while (moduleItr.hasNext()) {
            Map<String, String> map = moduleItr.next();

            JSONObject obj = new JSONObject();
            obj.put("id", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("memo", map.get("memo"));
            obj.put("parent_wid", map.get("parent_wid"));

            obj.put("text", map.get("name"));

            moduleArray.put(obj);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(moduleArray.toString());

        return null;
    }
    
    /**
     * 列出功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "searchlist")
    public ModelAndView searchlist(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        final String name = request.getParameter("name");

        List<Map<String, String>> modules = service.searchlist(name);
        JSONArray moduleArray = new JSONArray();
        Iterator<Map<String, String>> moduleItr = modules.iterator();
        while (moduleItr.hasNext()) {
            Map<String, String> map = moduleItr.next();

            JSONObject obj = new JSONObject();
            obj.put("id", map.get("wid"));
            obj.put("name", map.get("name"));
            obj.put("memo", map.get("memo"));
            obj.put("parent_wid", map.get("parent_wid"));

            obj.put("text", map.get("wid"));
            obj.put("path", map.get("path"));
            obj.put("wids", map.get("wids"));
            moduleArray.put(obj);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(moduleArray.toString());

        return null;
    }
}
