/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.selectrange.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author hrfan
 */
@Controller
@RequestMapping(value = "/combotree")
public class CombotreeController {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SelectRangeVisitor selectRangeVisitor;

    /**
     *
     * @param name 选择范围名称
     * @param pcol 层级名称
     * @param pval 层级值
     * @param cascol 级联名称
     * @param casval 级联值
     * @param order 值排序
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
    public String execute(
            @RequestParam("name") String name,
            @RequestParam("pcol") String pcol,
            @RequestParam("pval") String pval,
            @RequestParam("cascol") String cascol,
            @RequestParam("casval") String casval,
            @RequestParam("order") String order,
            HttpServletResponse response) throws IOException {
        JSONArray jsonArray = new JSONArray();

        if (pcol == null || "".equals(pcol.trim())) {
            pcol = selectRangeVisitor.cacheGetDefaultParentDefineName(name);
        }
        
        List<Map<String, Object>> combotree = null;
        if (cascol == null || "".equals(cascol.trim())) {
            combotree = selectRangeVisitor.cacheListCombotree(name, pcol, pval);
        } else {//级联
            combotree = selectRangeVisitor.cacheListCombotreeCascade(name, pcol, pval, cascol, casval);
        }

        Iterator iter = combotree.iterator();
        while (iter.hasNext()) {
            Map m = (Map) iter.next();
            String value = (String) m.get("value");
            String label = (String) m.get("label");
            String fullLabel = (String) m.get("fullLabel");
            Boolean leaf = (Boolean) m.get("left");

            JSONObject o = new JSONObject();
            JSONObject metadata = new JSONObject();
            try {
                o.put("id", value);
                o.put("data", label);
                o.put("leaf", leaf);
                if (!leaf) {
                    o.put("state", "closed");
                }
                o.put("metadata", metadata);
                metadata.put("id", value);
                metadata.put("title", label);
                metadata.put("label", fullLabel);
            } catch (Exception ex) {
                log.error("", ex);
                continue;
            }

            jsonArray.put(o);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(jsonArray.toString());

        return null;
    }
}
