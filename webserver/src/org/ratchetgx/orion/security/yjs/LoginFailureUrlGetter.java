package org.ratchetgx.orion.security.yjs;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.ratchetgx.orion.security.DefaultFailureUrlGetter;

public class LoginFailureUrlGetter implements DefaultFailureUrlGetter {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public String getDefaultFailureUrl(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception) throws Exception {
		
		String loginPageUrl = request.getHeader("Referer");
		if (loginPageUrl.indexOf("?") != -1) {
			loginPageUrl = loginPageUrl.substring(0, loginPageUrl.indexOf("?"));
		}
		log.debug("loginPageUrl==============:" + loginPageUrl);
		
		/** 准备错误提示信息 */
		String errorMsg = "";
		if (exception instanceof UsernameNotFoundException) {
			errorMsg = "用户名不存在";
		} else {
			log.debug("LoginFailureUrlGetter:exception.getMessage():" + exception.getMessage());
			errorMsg = "用户名或密码错误";
		}
		
		return loginPageUrl += "?errorMsg=" + URLEncoder.encode(errorMsg, "UTF-8");
	}

}
