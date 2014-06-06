package org.ratchetgx.orion.widgets.selectrange;

import java.util.List;

/**
 * 下拉树选择范围配置
 * 
 * @author hrfan
 */
public class CombotreeSelectRange implements SelectRange {

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
	// 全文本列名（下拉树使用）
	private String labelFullColumn;
	// 级联定义
	private List<String> cascades;
	// 层级定义
	private List<String> parentDefines;
	// 是否缓存
	private boolean cached = true;

	private String order;

	public CombotreeSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String labelFullColumn, List<String> cascades,
			List<String> parentDefines) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.labelFullColumn = labelFullColumn;
		this.cascades = cascades;
		this.parentDefines = parentDefines;
	}

	public CombotreeSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String labelFullColumn, List<String> cascades,
			List<String> parentDefines, Boolean cached) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.labelFullColumn = labelFullColumn;
		this.cascades = cascades;
		this.parentDefines = parentDefines;
		this.cached = cached;
	}

	public CombotreeSelectRange(String name, String description,
			String classDefine, String table, String valueColumn,
			String labelColumn, String labelFullColumn, List<String> cascades,
			List<String> parentDefines, Boolean cached, String order) {
		this.name = name;
		this.description = description;
		this.classDefine = classDefine;
		this.table = table;
		this.valueColumn = valueColumn;
		this.labelColumn = labelColumn;
		this.labelFullColumn = labelFullColumn;
		this.cascades = cascades;
		this.parentDefines = parentDefines;
		this.cached = cached;
		this.order = order;
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

	public String getLabelFullColumn() {
		return labelFullColumn;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<String> getCascades() {
		return cascades;
	}

	public List<String> getParentDefines() {
		return parentDefines;
	}

	public boolean isCached() {
		return cached;
	}

}