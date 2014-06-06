package org.ratchetgx.orion.ssm.ugr.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.ssm.ugr.service.UgrGroupService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
@RequestMapping(value = "ugr/ugrGroups")
public class UgrGroupController extends MultiActionController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private UgrGroupService ugrGroupService;

    @Autowired
    public void setUgrGroupService(UgrGroupService ugrGroupService) {
        this.ugrGroupService = ugrGroupService;
    }

    @RequestMapping(value = "list")
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<Map<String, String>> groups = ugrGroupService.listGroups();
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
        rev.put("ugrGroups", groupArray);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value = "add")
    public ModelAndView add(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String ugrGroup = request.getParameter("ugrGroup");
        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrGroupService.addGroup(ugrGroup);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }

    @RequestMapping(value = "delete")
    public ModelAndView delete(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String pv_ugrGroups = request.getParameter("ugrGroups");
        final JSONArray ja_ugrGroups = new JSONArray(pv_ugrGroups);
        List<String> ugrGroups = new ArrayList<String>();
        for (int i = 0; i < ja_ugrGroups.length(); i++) {
            ugrGroups.add(ja_ugrGroups.getString(i));
        }

        final JSONObject rev = new JSONObject();
        rev.put("success", true);
        try {
            ugrGroupService.deleteGroups(ugrGroups);
        } catch (Exception e) {
            log.error("", e);
            rev.put("success", false);
        }

        response.setContentType("text/json;charset=utf-8");
        response.getWriter().println(rev);
        return null;
    }
}
