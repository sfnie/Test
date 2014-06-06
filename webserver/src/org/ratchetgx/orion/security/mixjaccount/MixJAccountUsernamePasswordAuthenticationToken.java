package org.ratchetgx.orion.security.mixjaccount;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MixJAccountUsernamePasswordAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	public MixJAccountUsernamePasswordAuthenticationToken(Object principal,
			Object credentials, String mode) {
		super(principal, credentials);
		this.mode = mode;
	}

	public MixJAccountUsernamePasswordAuthenticationToken(Object principal,
			Object credentials,
			Collection<? extends GrantedAuthority> authorities, String mode) {
		super(principal, credentials, authorities);
		this.mode = mode;
	}

	private String mode = "db";

	public String getMode() {
		return mode;
	}

}
