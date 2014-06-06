package org.ratchetgx.orion.ssm.module.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.module.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * 功能模块
 *
 * @author hrfan
 *
 */
@Controller
@RequestMapping(value = "module")
public class ModuleController extends MultiActionController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private ModuleService moduleService;

    @Autowired
    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @RequestMapping(method = RequestMethod.GET,value="index")
    public String index() {
        return "module/index";
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
        final String parent_id = request.getParameter("parent_id");

        List<Map<String, String>> modules = moduleService.list(parent_id);
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
     * 删除功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "remove")
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String moduleWid = request.getParameter("id");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleService.delete(moduleWid);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

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

        final String name = request.getParameter("cname");
        final String memo = request.getParameter("memo");
        final String parentModuleWid = request.getParameter("parent_id");

        log.info(name + ":" + memo + ":" + parentModuleWid);

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleService.add(name, memo, parentModuleWid);
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

        final String moduleWid = request.getParameter("id");
        final String name = request.getParameter("cname");
        final String memo = request.getParameter("memo");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            moduleService.edit(moduleWid, name, memo);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 加载功能模块
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "load")
    public ModelAndView load(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String module_id = request.getParameter("module_id");

        final JSONObject rev = new JSONObject();
        rev.put("success", true);

        try {
            Map<String, String> module = moduleService.load(module_id);

            JSONObject data = new JSONObject();

            data.put("id", module.get("wid"));
            data.put("cname", module.get("name"));
            data.put("memo", module.get("memo"));
            data.put("parent_id", module.get("parent_id"));

            data.put("text", module.get("name"));

            rev.put("data", data);

        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }
}
