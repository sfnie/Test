package org.ratchetgx.orion.security.mixjaccount;

import org.springframework.security.core.AuthenticationException;

public class MixJaccountAuthenticationException extends AuthenticationException {

	public MixJaccountAuthenticationException(String msg) {
		super(msg);
	}

}
