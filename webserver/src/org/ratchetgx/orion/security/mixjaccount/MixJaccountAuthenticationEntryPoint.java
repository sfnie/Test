/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.mixjaccount;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.context.ServletContextAware;

/**
 *
 * @author hrfan
 */
public class MixJaccountAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean, ServletContextAware {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private ServletContext sc;
    
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("commence");
        
        String currentURL = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !"".equals(queryString)) {
        	currentURL += "?" + queryString;
        }
        
        currentURL = currentURL.substring(currentURL.indexOf(request.getContextPath()) + request.getContextPath().length());
        log.debug("currentURL:" + currentURL);
        
        String authenURL = request.getContextPath() + "/j_spring_jaccount_security_check";
        authenURL += "?retUrl=" + URLEncoder.encode(currentURL, "utf-8");
        
        log.debug("authenURl:" + authenURL);
        
        response.sendRedirect(authenURL);
        
    }

    public void afterPropertiesSet() throws Exception {
    }

	public void setServletContext(ServletContext sc) {
		this.sc = sc;
	}
	
}
