package org.ratchetgx.orion.widgets.selectrange.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ratchetgx.orion.common.util.BizobjUtilHelper;
import org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 处理下拉控件数据请求
 * 
 * @author hrfan
 */
@Controller
@RequestMapping(value = "/selectrange")
public class SelectrangeController {

	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SelectRangeVisitor selectRangeVisitor;

	@Autowired
	private BizobjUtilHelper bizobjUtilHelper;

	@RequestMapping
	public String execute(@RequestParam("keys") String[] keys,
			@RequestParam("baseTypes") String[] baseTypes,
			@RequestParam("values") String[] values,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		long startTime = System.currentTimeMillis();
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			if (!(baseTypes == null || baseTypes.length == 0 || values == null || values.length == 0)) {
				for (int i = 0; i < baseTypes.length; i++) {
					String label = "";

					if (selectRangeVisitor.isCombobox(baseTypes[i])) {
						Map<Object, String> vls = selectRangeVisitor
								.cacheGetVLs(baseTypes[i]);
						label = vls.get(values[i]);
					}

					if (selectRangeVisitor.isCombotree(baseTypes[i])) {
						Map<Object, String> vfls = selectRangeVisitor
								.cacheGetVFLs(baseTypes[i]);
						label = vfls.get(values[i]);
					}

					if (selectRangeVisitor.isCombotable(baseTypes[i])) {
						label = selectRangeVisitor.getTableFL(baseTypes[i],
								values[i]);
					}

					JSONObject o = new JSONObject();
					o.put("key", keys[i]);
					o.put("label", label);
					o.put("baseType", baseTypes[i]);
					o.put("value", values[i]);
					jsonArray.put(o);
				}
			}
			String[] p = request.getParameterValues("p");
			if (!ArrayUtils.isEmpty(p)) {
				JSONArray array = new JSONArray();
				for (String data : p) {
					String[] tableAndColumns = StringUtils.split(data, ":");
					if (tableAndColumns.length == 2) {
						String tableName = tableAndColumns[0];
						String[] columns = StringUtils.split(
								tableAndColumns[1], ",");
						try {
							Map<String, Integer> map = bizobjUtilHelper
									.getColumnPrecision(tableName);
							if (columns.length > 0 && map.size() > 0) {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("biz", tableName);
								JSONObject col = new JSONObject();
								for (String columnName : columns) {
									Integer columnPrecision = map
											.get(columnName);
									if (columnPrecision != null) {
										col.put(columnName, columnPrecision);
									}
								}
								jsonObject.put("columns", col);
								array.put(jsonObject);
							}
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
				if (array.length() > 0) {
					result.put("metaData", array);
				}
			}

		} catch (Exception ex) {
			log.error("", ex);
		}

		log.debug("查询耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");

		response.setContentType("application/json;charset=UTF-8");
		result.put("selectRange", jsonArray);
		response.getWriter().print(result.toString());

		return null;
	}
}
