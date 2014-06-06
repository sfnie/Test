/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
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
@RequestMapping(value = "/combobox")
public class ComboboxController {

	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SelectRangeVisitor selectRangeVisitor;

	/**
	 * 
	 * @param name
	 *            选择范围名称
	 * @param cascol
	 *            级联名称
	 * @param casval
	 *            级联值
	 * @param order
	 *            值排序
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String execute(@RequestParam("name") String name,
			@RequestParam("order") String order, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String cascol = request.getParameter("cascol");
		String casval = request.getParameter("casval");
		JSONArray jsonArray = new JSONArray();

		Map combobox = null;
		// 如果没有级联
		if (cascol == null || "".equals(cascol.trim())) {
			combobox = selectRangeVisitor.cacheListCombobox(name);
		} else {// 如果有级联
			combobox = selectRangeVisitor.cacheListComboboxCascade(name,
					cascol, casval);
		}

		Iterator iter = null;
		if (StringUtils.isEmpty(order)) {
			iter = combobox.keySet().iterator();
		} else {
			iter = sortKeys(combobox.keySet(), order).iterator();
		}

		while (iter.hasNext()) {
			String value = (String) iter.next();
			String label = (String) combobox.get(value);
			JSONObject o = new JSONObject();
			try {
				o.put("id", value);
				o.put("label", label);
			} catch (JSONException ex) {
				log.error("", ex);
				continue;
			}
			jsonArray.put(o);
		}

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(jsonArray.toString());

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
