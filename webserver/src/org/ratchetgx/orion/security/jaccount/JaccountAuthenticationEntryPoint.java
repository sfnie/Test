/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.jaccount;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 *
 * @author hrfan
 */
public class JaccountAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private String jaccountLogoutUrl = "/ssfw/logout_jaccount";

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("====commence====");
        response.sendRedirect(jaccountLogoutUrl);
    }

    public void afterPropertiesSet() throws Exception {
    }
}
