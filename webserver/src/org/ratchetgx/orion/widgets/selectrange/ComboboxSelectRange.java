package org.ratchetgx.orion.widgets.selectrange;

import java.util.List;
import java.util.Map;

/**
 * 下拉框选择范围配置
 * 
 * @author hrfan
 */
public class ComboboxSelectRange implements SelectRange {

	// 名称
	private String name;
	// 描述
	private String description;
	// 自定义实现类
	private String classDefine;
	// 表名
	private String table;
	// 值列名
	private String valueColumn;
	// 文本列名
	private String labelColumn;
	// 过滤语句
	private String filter;
	// 级联定义
	private List<String> cascades;
	// 自定义键值对
	private Map<String, Object> pairs;
	// 是否缓存
	private boolean cached = true;
	// 排序子句
	private String order;

	// 建模信息
	private String epstarTableName;

	private String epstarColumnName;

	public ComboboxSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String filter, List<String> cascades,
			Map<String, Object> pairs) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.filter = filter;
		this.cascades = cascades;
		this.pairs = pairs;
	}

	public ComboboxSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String filter, List<String> cascades,
			Map<String, Object> pairs, Boolean cached) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.filter = filter;
		this.cascades = cascades;
		this.pairs = pairs;
		this.cached = cached;
	}

	public ComboboxSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String filter, List<String> cascades,
			Map<String, Object> pairs, Boolean cached, String order,
			String epstarTableName, String epstarColumnName) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.filter = filter;
		this.cascades = cascades;
		this.pairs = pairs;
		this.cached = cached;
		this.order = order;
		this.epstarColumnName = epstarColumnName;
		this.epstarTableName = epstarTableName;
	}

	public String getEpstarTableName() {
		return epstarTableName;
	}

	public void setEpstarTableName(String epstarTableName) {
		this.epstarTableName = epstarTableName;
	}

	public String getEpstarColumnName() {
		return epstarColumnName;
	}

	public void setEpstarColumnName(String epstarColumnName) {
		this.epstarColumnName = epstarColumnName;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getClassDefine() {
		return classDefine;
	}

	public String getTable() {
		return table;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public String getLabelColumn() {
		return labelColumn;
	}

	public List<String> getCascades() {
		return cascades;
	}

	public Map<String, Object> getPairs() {
		return pairs;
	}

	public String getFilter() {
		return filter;
	}

	public boolean isCached() {
		return cached;
	}

	public String getOrder() {
		return order;
	}

}