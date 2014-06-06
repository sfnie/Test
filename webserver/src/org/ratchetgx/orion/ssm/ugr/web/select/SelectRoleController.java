package org.ratchetgx.orion.ssm.ugr.web.select;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.select.SelectRoleService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/select/selectRole")
public class SelectRoleController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private SelectRoleService selectRoleService;

    @Autowired
    public void setSelectRoleService(SelectRoleService selectRoleService) {
        this.selectRoleService = selectRoleService;
    }

    @RequestMapping(value="listSelectedOfGroup")
    public ModelAndView listSelectedOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");

        List<String> roles = selectRoleService.listSelectedOfGroup(groupWid);
        JSONObject rev = new JSONObject();
        JSONArray roleArray = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("role", role);
            roleArray.put(groupObject);
        }
        rev.put("roles", roleArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="listUnSelectedOfGroup")
    public ModelAndView listUnSelectedOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");

        List<String> roles = selectRoleService.listUnSelectedOfGroup(groupWid);
        JSONObject rev = new JSONObject();
        JSONArray roleArray = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("role", role);
            roleArray.put(groupObject);
        }
        rev.put("roles", roleArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    @RequestMapping(value="listSelectedOfUser")
    public ModelAndView listSelectedOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");

        List<String> roles = selectRoleService.listSelectedOfUser(userWid);
        JSONObject rev = new JSONObject();
        JSONArray roleArray = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("role", role);
            roleArray.put(groupObject);
        }
        rev.put("roles", roleArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;

    }

    @RequestMapping(value="listUnSelectedOfUser")
    public ModelAndView listUnSelectedOfUser(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String userWid = request.getParameter("userWid");

        List<String> roles = selectRoleService.listUnSelectedOfUser(userWid);
        JSONObject rev = new JSONObject();
        JSONArray roleArray = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();
            JSONObject groupObject = new JSONObject();
            groupObject.put("role", role);
            roleArray.put(groupObject);
        }
        rev.put("roles", roleArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 取消角色与某用户组的关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="cancelOfGroup")
    public ModelAndView cancelOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");
        String rolesStr = request.getParameter("roles");

        JSONArray roleArray = new JSONArray(rolesStr);
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < roleArray.length(); i++) {
            roles.add(roleArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectRoleService.cancelOfGroup(groupWid, roles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    /**
     * 添加角色与某用户组的对应关系
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value="addOfGroup")
    public ModelAndView addOfGroup(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String groupWid = request.getParameter("groupWid");
        String rolesStr = request.getParameter("roles");

        JSONArray roleArray = new JSONArray(rolesStr);
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < roleArray.length(); i++) {
            roles.add(roleArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectRoleService.addOfGroup(groupWid, roles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);

        return null;
    }

    /**
     * 取消角色与某用户的对应关系
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
        String rolesStr = request.getParameter("roles");

        JSONArray roleArray = new JSONArray(rolesStr);
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < roleArray.length(); i++) {
            roles.add(roleArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectRoleService.cancelOfUser(userWid, roles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    /**
     * 添加角色与某用户的对应关系
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
        String rolesStr = request.getParameter("roles");

        JSONArray roleArray = new JSONArray(rolesStr);
        List<String> roles = new ArrayList<String>();
        for (int i = 0; i < roleArray.length(); i++) {
            roles.add(roleArray.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            selectRoleService.addOfUser(userWid, roles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }
}
