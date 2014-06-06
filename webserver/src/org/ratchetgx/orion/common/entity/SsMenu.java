package org.ratchetgx.orion.common.entity;

import java.io.Serializable;
import java.util.List;

public class SsMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	private String wid;
	private String name;
	private String memo;
	private String role;
	private List<SsMenuitem> menuitemList;

	public SsMenu() {
	}

	public SsMenu(String wid) {
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public List<SsMenuitem> getMenuitemList() {
		return menuitemList;
	}

	public void setMenuitemList(List<SsMenuitem> menuitemList) {
		this.menuitemList = menuitemList;
	}


	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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
		if (!(object instanceof SsMenu)) {
			return false;
		}
		SsMenu other = (SsMenu) object;
		if ((this.wid == null && other.wid != null)
				|| (this.wid != null && !this.wid.equals(other.wid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "org.ratchetgx.orion.common.entity.ss.SsMenu[ wid=" + wid + " ]";
	}

}
