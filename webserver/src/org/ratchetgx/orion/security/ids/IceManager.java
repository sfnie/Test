/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.ids;

import com.wiscom.is.IdentityFactory;
import com.wiscom.is.IdentityManager;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author hrfan
 */
public class IceManager {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private IdentityManager identityManager;

	/**
	 * 返回个IdentityManager对象
	 */
	public IdentityManager getIdentityManager() {
		String idsClientPropertyPath = this.getClass().getResource("/")
				.getPath()
				+ "client.properties";
		log.info("idsClientPropertyPath=" + idsClientPropertyPath);
		if (identityManager == null) {
			IdentityFactory factory;
			try {
				factory = IdentityFactory.createFactory(idsClientPropertyPath);
				identityManager = factory.getIdentityManager();
			} catch (Exception ex) {
				log.error("", ex);
				identityManager = null;
			}
		}
		return this.identityManager;
	}

	public String getTokenValue(HttpServletRequest request,
			HttpServletResponse response) {
		String tokenValue = null;
		IdentityManager im = getIdentityManager();

		// 如果是POST请求，表示是从登录页登录系统，
		// 否则，单点登录校验
		if ("POST".equals(request.getMethod().toUpperCase())) {
			String username = (String) request.getParameter("j_username");
			String password = (String) request.getParameter("j_password");

			if (username != null && password != null) {
				tokenValue = im.createStoken(username, password)
						.getTokenValue();
			}
			log.info("created tokenValue=" + tokenValue);

			// 写Cookie
			if (tokenValue != null && !"".equals(tokenValue)) {
				String cookieDomainName = CookieUtil
						.getDefaultCookieDomain(request);
				String encodeCookieValue = URLEncoder.encode(tokenValue);
				CookieUtil.setCookie(
						response,
						Constants.IS_COOKIE_NAME,
						encodeCookieValue,
						StringUtils.isEmpty(cookieDomainName) ? CookieUtil
								.getDefaultCookieDomain(request)
								: cookieDomainName, -1, "/");
			}
		} else {
			Cookie allCookies[] = request.getCookies();
			if (allCookies != null) {
				for (int i = 0; i < allCookies.length; i++) {
					Cookie cookie = allCookies[i];
					log.info("cookie=" + cookie.getName());
					if (cookie.getName().equals(Constants.IS_COOKIE_NAME)) {
						tokenValue = URLDecoder.decode(cookie.getValue());
						break;
					}
				}
			}
			log.info("cookie tokenValue=" + tokenValue);
		}

		return tokenValue;
	}

}
