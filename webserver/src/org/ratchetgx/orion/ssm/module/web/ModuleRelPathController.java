package org.ratchetgx.orion.ssm.module.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.module.service.ModuleRelPathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "moduleRelPath")
public class ModuleRelPathController extends MultiActionController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private ModuleRelPathService moduleRelPathService;

    @Autowired
    public void setModuleRelPathService(
            ModuleRelPathService moduleRelPathService) {
        this.moduleRelPathService = moduleRelPathService;
    }

    /**
     * 列出模块路径
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String module_id = request.getParameter("module_id");

        final JSONObject rev = new JSONObject();

        List<Map<String, String>> paths = moduleRelPathService.list(module_id);
        JSONArray objs = new JSONArray();

        Iterator<Map<String, String>> pathItr = paths.iterator();
        while (pathItr.hasNext()) {
            Map<String, String> path = pathItr.next();
            JSONObject obj = new JSONObject();
            obj.put("id", path.get("wid"));
            obj.put("path", path.get("path"));
            obj.put("memo", path.get("memo"));
            obj.put("module_id", path.get("module_Wid"));
            obj.put("indexed", path.get("indexed"));
            objs.put(obj);
        }

        rev.put("modulepaths", objs);

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
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
        final String path = request.getParameter("path");
        final String indexed = request.getParameter("indexed");
        final String memo = request.getParameter("memo");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleRelPathService.add(module_id, path, indexed, memo);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 删除模块路径
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "remove")
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String idsStr = request.getParameter("idsStr");
        final JSONArray ids = new JSONArray(idsStr);

        List<String> modulePathWids = new ArrayList<String>();
        for (int i = 0; i < ids.length(); i++) {
            modulePathWids.add(ids.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleRelPathService.delete(modulePathWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 加载功能
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "load")
    public ModelAndView load(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String moduleWid = request.getParameter("id");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            Map<String, String> modulePath = moduleRelPathService
                    .load(moduleWid);

            JSONObject obj = new JSONObject();

            obj.put("id", modulePath.get("mid"));
            obj.put("path", modulePath.get("path"));
            obj.put("memo", modulePath.get("memo"));
            obj.put("module_id", modulePath.get("module_id"));
            obj.put("indexed", modulePath.get("indexed"));

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
     * 设置默认模块路径
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "setAsDefault")
    public ModelAndView setAsDefault(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String moduleWid = request.getParameter("moduleWid");
        String pathWid = request.getParameter("pathWid");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleRelPathService.setAsDefault(moduleWid, pathWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }
}
