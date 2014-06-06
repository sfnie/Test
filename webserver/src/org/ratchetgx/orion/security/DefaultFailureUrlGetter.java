package org.ratchetgx.orion.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

public interface DefaultFailureUrlGetter {
	public String getDefaultFailureUrl(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws Exception;
}
