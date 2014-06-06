package org.ratchetgx.orion.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;


public interface DefaultTargetUrlGetter {
	public String getDefaultTargetUrl(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws Exception;
}
