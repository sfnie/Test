/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.jaccount;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author hrfan
 */
public class JaccountFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private FilterConfig filterConfig = null;
    private WebApplicationContext appCtx;
    private String jaccountLoginUrl = "login_jaccount";
    private String jaccountLogoutUrl = "logout_jaccount";

    public JaccountFilter() {
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            appCtx = (WebApplicationContext) filterConfig
                    .getServletContext()
                    .getAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            String uri = httpRequest.getRequestURI();

            log.info("uri=" + uri + ":" + isExcluded(uri) + ":" + httpRequest.getSession().getAttribute(JAccountManagerHelper.JACCOUNT_SESSION_LOGINED));

            //登录页面直接通过
            if (httpRequest.getRequestURI().endsWith(jaccountLoginUrl)) {
                log.info("-----------------:" + jaccountLoginUrl);
                RequestDispatcher rd = request.getRequestDispatcher("/jaccount/login.jsp");
                rd.forward(request, response);
                return;
            }
            //登出页面直接通过
            if (httpRequest.getRequestURI().endsWith(jaccountLogoutUrl)) {
                log.info("-----------------:" + jaccountLogoutUrl);
                RequestDispatcher rd = request.getRequestDispatcher("/jaccount/logout.jsp");
                rd.forward(request, response);
                return;
            }

            //判断是否师生服务系统登录过
            if (!isExcluded(uri)
                    && httpRequest.getSession().getAttribute(JAccountManagerHelper.JACCOUNT_SESSION_LOGINED) == null) {
                RequestDispatcher rd = request.getRequestDispatcher("/jaccount/logout.jsp");
                rd.forward(request, response);
                return;
            }

            log.info("========");

            chain.doFilter(request, response);
        } catch (Throwable t) {
            log.error("", t);
        }

    }
    
    private boolean isExcluded(String uri) {
        if (uri.endsWith("j_spring_jaccount_security_check")
                || uri.endsWith("j_spring_security_logout")) {
            return true;
        }

        return false;
    }

    public void destroy() {
    }
}
