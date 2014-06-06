package org.ratchetgx.orion.ssm.ugr.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.UgrUserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/ugrUsers")
public class UgrUserController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private UgrUserService ugrUserService;

    @Autowired
    public void setUgrUserService(UgrUserService ugrUserService) {
        this.ugrUserService = ugrUserService;
    }

    @RequestMapping(value="list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<Map<String, String>> users = ugrUserService.listUsers();
        JSONObject rev = new JSONObject();
        JSONArray userArray = new JSONArray();
        Iterator<Map<String, String>> userItr = users.iterator();
        while (userItr.hasNext()) {
            Map<String, String> user = userItr.next();
            JSONObject userObject = new JSONObject();
            userObject.put("wid", user.get("wid"));
            userObject.put("name", user.get("name"));
            userArray.put(userObject);
        }
        rev.put("ugrUsers", userArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value="add")
    public ModelAndView add(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String ugrUser = request.getParameter("ugrUser");
        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrUserService.addUser(ugrUser);
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
        String pv_ugrUsers = request.getParameter("ugrUsers");
        final JSONArray ja_ugrUsers = new JSONArray(pv_ugrUsers);
        List<String> ugrUsers = new ArrayList<String>();
        for (int i = 0; i < ja_ugrUsers.length(); i++) {
            ugrUsers.add(ja_ugrUsers.getString(i));
        }

        log.info(ugrUsers + ";" + ugrUsers.size());

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrUserService.deleteUsers(ugrUsers);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }
}
