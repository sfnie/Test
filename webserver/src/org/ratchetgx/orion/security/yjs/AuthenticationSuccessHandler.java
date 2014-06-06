package org.ratchetgx.orion.security.yjs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.ratchetgx.orion.security.ids.IdsAuthenticationSuccessHandler;

public class AuthenticationSuccessHandler extends IdsAuthenticationSuccessHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		super.onAuthenticationSuccess(request, response, authentication);
		
		String loginPageUrl = request.getHeader("Referer");
		if (loginPageUrl.indexOf("?") != -1) {
			loginPageUrl = loginPageUrl.substring(0, loginPageUrl.indexOf("?"));
		}
		request.getSession().setAttribute("LOGIN_URL", loginPageUrl);
		log.debug("loginPageUrl:" + loginPageUrl);

	}

	 

}
