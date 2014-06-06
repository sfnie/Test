package org.ratchetgx.orion.widgets.selectrange;

import java.util.List;
import java.util.Map;

/**
 * 表格选择范围配置
 * 
 * @author hrfan
 */
public class CombotableSelectRange implements SelectRange {

	// 名称
	private String name;
	// 描述
	private String description;
	// 表名
	private String table;
	// 过滤语句
	private String filter;
	private List<String> cascades;
	// 列定义
	private Map display;

	private String order;

	public CombotableSelectRange(String name, String description, String table,
			String filter, List<String> cascades, Map display) {
		this.name = name;
		this.description = description;
		this.table = table;
		this.filter = filter;
		this.cascades = cascades;
		this.display = display;
	}

	public CombotableSelectRange(String name, String description, String table,
			String filter, List<String> cascades, Map display, String order) {
		this.name = name;
		this.description = description;
		this.table = table;
		this.filter = filter;
		this.cascades = cascades;
		this.display = display;
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getTable() {
		return table;
	}

	public String getFilter() {
		return filter;
	}

	public Map getDisplay() {
		return display;
	}

	public List<String> getCascades() {
		return cascades;
	}

	public boolean isCached() {
		return false;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}