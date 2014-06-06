package org.ratchetgx.orion.security.ids;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.wiscom.is.IdentityFactory;
import com.wiscom.is.IdentityManager;

public class IdsLogoutSuccessHandler  implements  LogoutSuccessHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) 
				throws IOException, ServletException {

		HttpSession session = request.getSession();
		
			
		/** 构造本应用的基本URL */
		String requestURL = request.getRequestURL().toString();
		String contextPath = request.getContextPath();
	    int firstSplitIndex = requestURL.indexOf('/');
	    int secondSplitIndex = requestURL.indexOf('/', firstSplitIndex + 1);
	    int thirdSplitIndex = requestURL.indexOf('/', secondSplitIndex + 1);
	    String appBaseURL = requestURL.substring(0, thirdSplitIndex) + contextPath + "/";
 	    
 	    /** 定位IDS的Cookie值 */
		String configFilePath = getClass().getResource("/client.properties").getPath();
		IdentityFactory factory = null;
		try {
			factory = IdentityFactory.createFactory(configFilePath);
		} catch (Exception e) {
			log.info("IDS处理异常：", e);
			throw new ServletException("IDS处理异常:" + e.getMessage());
		}
		IdentityManager im = factory.getIdentityManager();
		
	    Cookie allCookies[] = request.getCookies();
	    Cookie myCookie;
	    String decodedCookieValue = null;
	    if (allCookies != null) {
	        for(int i=0; i< allCookies.length; i++) {
	            myCookie = allCookies[i];
	            if( myCookie.getName().equals("iPlanetDirectoryPro") ) {
	                decodedCookieValue = URLDecoder.decode(myCookie.getValue(),"GB2312");
	            }
	        }
	    }
		String currentIdsUser = "";
		if (decodedCookieValue != null ) {
	    	currentIdsUser = im.getCurrentUser( decodedCookieValue );
	    }
		if (currentIdsUser.length() != 0){ /** 已正常登录 */
			String idsLogoutURL = im.getLogoutURL();
			idsLogoutURL += "?goto=" + appBaseURL + "login_en.jsp";
			response.sendRedirect(idsLogoutURL);
			return;
		} else {
			String login_url = appBaseURL + "login_en.jsp";
			response.sendRedirect(login_url);
			return;
		}
		
	}
	
}
