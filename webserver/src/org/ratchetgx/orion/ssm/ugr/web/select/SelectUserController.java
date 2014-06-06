package org.ratchetgx.orion.ssm.ugr.web.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.select.SelectUserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/select/selectUser")
public class SelectUserController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private SelectUserService selectUserService;

    @Autowired
    public void setSelectUserService(SelectUserService selectUserService) {
        this.selectUserService = selectUserService;
    }

    @RequestMapping(value="listSelectedOfGroup")
    public ModelAndView listSelectedOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");

        List<Map<String, String>> users = selectUserService
                .listSelectedOfGroup(groupWid);
        JSONObject rev = new JSONObject();
        JSONArray userArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = users.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject userObject = new JSONObject();
            userObject.put("wid", map.get("wid"));
            userObject.put("name", map.get("name"));
            userObject.put("email", map.get("email"));
            userArray.put(userObject);
        }
        rev.put("users", userArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="listUnSelectedOfGroup")
    public ModelAndView listUnSelectedOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");

        List<Map<String, String>> users = selectUserService
                .listUnSelectedOfGroup(groupWid);
        JSONObject rev = new JSONObject();
        JSONArray userArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = users.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject userObject = new JSONObject();
            userObject.put("wid", map.get("wid"));
            userObject.put("name", map.get("name"));
            userObject.put("email", map.get("email"));
            userArray.put(userObject);
        }
        rev.put("users", userArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="listSelectedOfRole")
    public ModelAndView listSelectedOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");

        List<Map<String, String>> users = selectUserService
                .listSelectedOfRole(role);
        JSONObject rev = new JSONObject();
        JSONArray userArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = users.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject userObject = new JSONObject();
            userObject.put("wid", map.get("wid"));
            userObject.put("name", map.get("name"));
            userObject.put("email", map.get("email"));
            userArray.put(userObject);
        }
        rev.put("users", userArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="listUnSelectedOfRole")
    public ModelAndView listUnSelectedOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");

        List<Map<String, String>> users = selectUserService
                .listUnSelectedOfRole(role);
        JSONObject rev = new JSONObject();
        JSONArray userArray = new JSONArray();
        Iterator<Map<String, String>> groupItr = users.iterator();
        while (groupItr.hasNext()) {
            Map<String, String> map = groupItr.next();
            JSONObject userObject = new JSONObject();
            userObject.put("wid", map.get("wid"));
            userObject.put("name", map.get("name"));
            userObject.put("email", map.get("email"));
            userArray.put(userObject);
        }
        rev.put("users", userArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="cancelOfRole")
    public ModelAndView cancelOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String role = request.getParameter("role");
        String userWidsStr = request.getParameter("userWids");

        JSONArray userWidArray = new JSONArray(userWidsStr);
        List<String> userWids = new ArrayList<String>();
        for (int i = 0; i < userWidArray.length(); i++) {
            userWids.add(userWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectUserService.cancelOfRole(role, userWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;

    }

    @RequestMapping(value="addOfRole")
    public ModelAndView addOfRole(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String role = request.getParameter("role");
        String userWidsStr = request.getParameter("userWids");

        JSONArray userWidArray = new JSONArray(userWidsStr);
        List<String> userWids = new ArrayList<String>();
        for (int i = 0; i < userWidArray.length(); i++) {
            userWids.add(userWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectUserService.addOfRole(role, userWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="cancelOfGroup")
    public ModelAndView cancelOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");
        String userWidsStr = request.getParameter("userWids");

        JSONArray userWidArray = new JSONArray(userWidsStr);
        List<String> userWids = new ArrayList<String>();
        for (int i = 0; i < userWidArray.length(); i++) {
            userWids.add(userWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectUserService.cancelOfGroup(groupWid, userWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="addOfGroup")
    public ModelAndView addOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");
        String userWidsStr = request.getParameter("userWids");

        JSONArray userWidArray = new JSONArray(userWidsStr);
        List<String> userWids = new ArrayList<String>();
        for (int i = 0; i < userWidArray.length(); i++) {
            userWids.add(userWidArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectUserService.addOfGroup(groupWid, userWids);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }
}
