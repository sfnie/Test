package org.ratchetgx.orion.ssm.ugr.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.UgrRoleService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/ugrRoles")
public class UgrRoleController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private UgrRoleService ugrRoleService;

    @Autowired
    public void setUgrRoleService(UgrRoleService ugrRoleService) {
        this.ugrRoleService = ugrRoleService;
    }

    @RequestMapping(value="list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<String> roles = ugrRoleService.listRoles();
        JSONObject rev = new JSONObject();
        JSONArray roleArray = new JSONArray();
        Iterator<String> roleItr = roles.iterator();
        while (roleItr.hasNext()) {
            String role = roleItr.next();
            JSONObject roleObject = new JSONObject();
            roleObject.put("ugrRole", role);
            roleArray.put(roleObject);
        }
        rev.put("ugrRoles", roleArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="add")
    public ModelAndView add(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String ugrRole = request.getParameter("ugrRole");
        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrRoleService.addRole(ugrRole);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="delete")
    public ModelAndView delete(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String pv_ugrRoles = request.getParameter("ugrRoles");
        final JSONArray ja_ugrRoles = new JSONArray(pv_ugrRoles);
        List<String> ugrRoles = new ArrayList<String>();
        for (int i = 0; i < ja_ugrRoles.length(); i++) {
            ugrRoles.add(ja_ugrRoles.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrRoleService.deleteRoles(ugrRoles);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }
}
