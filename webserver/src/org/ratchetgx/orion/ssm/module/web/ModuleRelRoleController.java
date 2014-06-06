package org.ratchetgx.orion.ssm.module.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.module.service.ModuleRelRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * 模块角色对应关系
 *
 * @author hrfan
 *
 */
@Controller
@RequestMapping(value = "moduleRelRole")
public class ModuleRelRoleController extends MultiActionController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private ModuleRelRoleService moduleRelRoleService;

    @Autowired
    public void setModuleRelRoleService(
            ModuleRelRoleService moduleRelRoleService) {
        this.moduleRelRoleService = moduleRelRoleService;
    }

    /**
     * 增加角色
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "add")
    public ModelAndView add(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String module_id = request.getParameter("module_id");
        final String role = request.getParameter("role");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleRelRoleService.add(module_id, role);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除角色
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "remove")
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        final String module_id = request.getParameter("module_id");
        final String roleStr = request.getParameter("role");

        log.info(module_id + ":" + roleStr);

        final JSONArray roleArray = new JSONArray(roleStr);
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < roleArray.length(); i++) {
            roles.add(roleArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleRelRoleService.delete(module_id, roles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 列出模块已选角色
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "listSelected")
    public ModelAndView listSelected(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String module_id = request.getParameter("module_id");

        List<String> roles = moduleRelRoleService.listSelected(module_id);
        JSONArray objs = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();

            JSONObject obj = new JSONObject();

            obj.put("role", role);
            obj.put("text", role);

            objs.put(obj);
        }

        JSONObject rev = new JSONObject();
        rev.put("roles", objs);

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev.toString());

        return null;
    }

    /**
     * 列出模块未选角色
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "listUnSelected")
    public ModelAndView listUnSelected(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String module_id = request.getParameter("module_id");

        List<String> roles = moduleRelRoleService.listUnSelected(module_id);
        JSONArray objs = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();

            JSONObject obj = new JSONObject();

            obj.put("role", role);
            obj.put("text", role);

            objs.put(obj);
        }

        JSONObject rev = new JSONObject();
        rev.put("roles", objs);

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev.toString());

        return null;
    }
}
