package org.ratchetgx.orion.security;

import org.springframework.security.core.GrantedAuthority;

public class SsfwGrantedAuthority implements GrantedAuthority {

	private String role;

	public SsfwGrantedAuthority(String role) {
		this.role = role;
	}

	
	public String getAuthority() {
		return this.role;
	}

}
