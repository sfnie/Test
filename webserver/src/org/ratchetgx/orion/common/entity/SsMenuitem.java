package org.ratchetgx.orion.common.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SsMenuitem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String wid;
	private String name;
	private String iconPath;
	private String path;
	private String memo;
	private int indexed;
	private String moduleId;
	private List<SsMenuitem> children;
	private SsMenuitem parent;
	private SsMenu menu;

	public SsMenuitem() {
	}

	public SsMenuitem(String wid) {
		this.wid = wid;
	}

	public String getWid() {
		return wid;
	}

	public void setWid(String wid) {
		this.wid = wid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getIndexed() {
		return indexed;
	}

	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public List<SsMenuitem> getChildren() {
		Collections.sort(children, new Comparator<SsMenuitem>() {
			public int compare(SsMenuitem o1, SsMenuitem o2) {
				return o1.indexed - o2.indexed;
			}
		});

		return children;
	}

	public void setChildren(List<SsMenuitem> children) {
		this.children = children;
	}

	public SsMenuitem getParent() {
		return parent;
	}

	public void setParent(SsMenuitem parent) {
		this.parent = parent;
	}

	public SsMenu getMenu() {
		return menu;
	}

	public void setMenu(SsMenu menu) {
		this.menu = menu;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (wid != null ? wid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SsMenuitem)) {
			return false;
		}
		SsMenuitem other = (SsMenuitem) object;
		if ((this.wid == null && other.wid != null)
				|| (this.wid != null && !this.wid.equals(other.wid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "org.ratchetgx.orion.common.entity.ss.SsMenuitem[ wid=" + wid + " ]";
	}
}
