package org.ratchetgx.orion.widgets.selectrange.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ratchetgx.orion.common.SsfwConstants;
import org.ratchetgx.orion.common.util.Pagination;
import org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/combotable")
public class CombotableController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SelectRangeVisitor selectRangeVisitor;

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping
    public String execute(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        //选择范围名称
        String selectRangeName = request.getParameter("name");
        String cascol = request.getParameter("cascol");
        String casval = request.getParameter("casval");
        String order = request.getParameter("order");
        //过滤条件
        String condition = request.getParameter("condition");
//        log.info("condition=" + condition);
        //当前页
        String currentPage = request.getParameter("currentPage");
        //每页记录数
        String pageCount = request.getParameter("pageCount");

        Pagination pagination = new Pagination();
        if (pageCount == null || "".equals(pageCount.trim())) {
            pagination.setPageCount(SsfwConstants.PAGE_COUNT);
        } else {
            pagination.setPageCount(Integer.parseInt(pageCount));
        }

        if (currentPage == null || "".equals(currentPage.trim())) {
            pagination.setCurrentPage(1);
        } else {
            pagination.setCurrentPage(Integer.parseInt(currentPage));
        }


        JSONArray jsonArray = new JSONArray();

        List<Map<String, Object>> dataList = null;
        if (cascol == null || "".equals(cascol.trim())) {
            dataList = selectRangeVisitor.listCombotable(selectRangeName, condition, pagination);
        } else {
            dataList = selectRangeVisitor.listCombotableCascade(selectRangeName, condition, pagination, cascol, casval);
        }
        Iterator iter = dataList.iterator();
        while (iter.hasNext()) {
            Map<String, Object> dataMap = (Map<String, Object>) iter.next();
            JSONObject o = new JSONObject();
            Iterator keyItr = dataMap.keySet().iterator();
            try {
                while (keyItr.hasNext()) {
                    String key = (String) keyItr.next();
                    Object val = dataMap.get(key);
                    o.put(key, val);
                    if (selectRangeVisitor.isValueColumn(selectRangeName, key)) {
                        o.put("value", val);
                    }
                    if (selectRangeVisitor.isLabelColumn(selectRangeName, key)) {
                        o.put("text", val);
                    }
                    
                }
            } catch (JSONException ex) {
                log.error("", ex);
                continue;
            }
            jsonArray.put(o);
        }

        JSONObject rev = new JSONObject();
        try {
            rev.put("data", jsonArray);
            rev.put("totalCount", pagination.getTotal());
        } catch (Exception ex) {
            log.error("", ex);
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(rev.toString());

        return null;
    }

    private List<String> sortKeys(Set keys, final String order) {
        List<String> skeys = new ArrayList<String>();
        skeys.addAll(keys);
        Collections.sort(skeys, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 != null && o2 != null) {
                    if ("desc".equals(order)) {
                        return o2.toString().compareTo(o1.toString());
                    } else {
                        return o1.toString().compareTo(o2.toString());
                    }
                }
                return 0;
            }
        });
        return skeys;
    }
}
