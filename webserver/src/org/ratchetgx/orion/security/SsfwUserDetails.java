package org.ratchetgx.orion.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SsfwUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wid;
	// 编号
	private String bh;
	// 用户名
	private String username;
	// 密码
	private String password;
	// email
	private String email;
	// 登陆类型
	private String userLoginType;

	// 授权
	private Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	// 所属用户组
	private Collection<String> groups = new ArrayList<String>();

	private Map attached = new HashMap();

	public SsfwUserDetails(String wid, String bh, String username,
			String password, Collection<GrantedAuthority> authorities,
			String email, Collection<String> groups) {
		if (authorities == null) {
			throw new RuntimeException(
					"authorities can't be null where SsfwUserDetails was constructed.");
		}
		if (groups == null) {
			throw new RuntimeException(
					"groups can't be null where SsfwUserDetails was constructed.");
		}
		this.wid = wid;
		this.bh = bh;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.email = email;
		this.groups = groups;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return Collections.unmodifiableCollection(this.authorities);
	}

	public String getWid() {
		return wid;
	}

	public String getBh() {
		return bh;
	}

	public String getPassword() {
		return this.password;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getEmail() {
		return this.email;
	}

	public Collection<String> getGroups() {
		return Collections.unmodifiableCollection(this.groups);
	}

	public Map getAttached() {
		return attached;
	}

	public String getUserLoginType() {
		return userLoginType;
	}

	public void setUserLoginType(String userLoginType) {
		this.userLoginType = userLoginType;
	}

	public String toString() {
		return "SsfwUserDetails [wid=" + wid + ", username=" + username
				+ ", password=" + password + ", email=" + email + "userType = "
				+ userLoginType + ", authorities=" + authorities + ", groups="
				+ groups + "]";
	}

}
