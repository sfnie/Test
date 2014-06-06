package org.ratchetgx.orion.ssm.ugr.web.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.select.SelectGroupService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/select/selectGroup")
public class SelectGroupController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private SelectGroupService selectGroupService;

    @Autowired
    public void setSelectGroupService(SelectGroupService selectGroupService) {
        this.selectGroupService = selectGroupService;
    }

    @RequestMapping(value="listSelectedOfRole")
    public ModelAndView listSelectedOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");

        List<Map<String, String>> groups = selectGroupService
                .listSelectedOfRole(role);
        JSONObject rev = new JSONObject();
        JSONArray groupArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = groups.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("wid", map.get("wid"));
            groupObject.put("name", map.get("name"));
            groupArray.put(groupObject);
        }
        rev.put("groups", groupArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="listUnSelectedOfRole")
    public ModelAndView listUnSelectedOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");

        List<Map<String, String>> groups = selectGroupService
                .listUnSelectedOfRole(role);
        JSONObject rev = new JSONObject();
        JSONArray groupArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = groups.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("wid", map.get("wid"));
            groupObject.put("name", map.get("name"));
            groupArray.put(groupObject);
        }
        rev.put("groups", groupArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="listSelectedOfUser")
    public ModelAndView listSelectedOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");

        List<Map<String, String>> groups = selectGroupService
                .listSelectedOfUser(userWid);
        JSONObject rev = new JSONObject();
        JSONArray groupArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = groups.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("wid", map.get("wid"));
            groupObject.put("name", map.get("name"));
            groupArray.put(groupObject);
        }
        rev.put("groups", groupArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="listUnSelectedOfUser")
    public ModelAndView listUnSelectedOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");

        List<Map<String, String>> groups = selectGroupService
                .listUnSelectedOfUser(userWid);
        JSONObject rev = new JSONObject();
        JSONArray groupArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = groups.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("wid", map.get("wid"));
            groupObject.put("name", map.get("name"));
            groupArray.put(groupObject);
        }
        rev.put("groups", groupArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    /**
     * 取消用户组与某角色的对应关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="cancelOfRole")
    public ModelAndView cancelOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");
        String groupWidsStr = request.getParameter("groupWids");

        JSONArray groupWidArray = new JSONArray(groupWidsStr);
        List<String> groupWids = new ArrayList<String>();
        for (int i = 0; i < groupWidArray.length(); i++) {
            groupWids.add(groupWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectGroupService.cancelOfRole(role, groupWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    /**
     * 添加用户组与某角色的关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="addOfRole")
    public ModelAndView addOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");
        String groupWidsStr = request.getParameter("groupWids");

        JSONArray groupWidArray = new JSONArray(groupWidsStr);
        List<String> groupWids = new ArrayList<String>();
        for (int i = 0; i < groupWidArray.length(); i++) {
            groupWids.add(groupWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectGroupService.addOfRole(role, groupWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    /**
     * 取消用户与某用户的对应关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="cancelOfUser")
    public ModelAndView cancelOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");
        String groupWidsStr = request.getParameter("groupWids");

        JSONArray groupWidArray = new JSONArray(groupWidsStr);
        List<String> groupWids = new ArrayList<String>();
        for (int i = 0; i < groupWidArray.length(); i++) {
            groupWids.add(groupWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectGroupService.cancelOfUser(userWid, groupWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 添加用户与某用户的对应关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="addOfUser")
    public ModelAndView addOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");
        String groupWidsStr = request.getParameter("groupWids");

        JSONArray groupWidArray = new JSONArray(groupWidsStr);
        List<String> groupWids = new ArrayList<String>();
        for (int i = 0; i < groupWidArray.length(); i++) {
            groupWids.add(groupWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectGroupService.addOfUser(userWid, groupWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }
}
