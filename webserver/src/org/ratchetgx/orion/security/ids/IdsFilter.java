/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.ids;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * @author hrfan
 */
public class IdsFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private FilterConfig filterConfig = null;
	private WebApplicationContext appCtx;
	private String[] excludes = StringUtils.EMPTY_STRING_ARRAY;

	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		if (filterConfig != null) {
			/*
			appCtx = (WebApplicationContext) filterConfig
					.getServletContext()
					.getAttribute(
							WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);*/
			appCtx = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
			String excludesParams = filterConfig.getInitParameter("excludes");
			if (!StringUtils.isEmpty(excludesParams)) {
				excludes = excludesParams.split(",");
			}
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		try {
			String uri = httpRequest.getRequestURI();

			// log.info("uri=" + uri + ":" + isExcluded(uri));

			// 判断是否师生服务系统登录过
			if (httpRequest.getSession().getAttribute(
					Constants.IDS_SESSION_LOGINED) == null
					&& !isExcluded(uri)) {
				RequestDispatcher rd = request
						.getRequestDispatcher("/login_en.jsp");
				rd.forward(request, response);
				return;
			}

			chain.doFilter(request, response);
		} catch (Throwable t) {
			log.error("", t);
		}

	}

	private boolean isExcluded(String uri) {
		for (int i = 0; i < excludes.length; i++) {
			String exclude = excludes[i];
			if (!StringUtils.isEmpty(exclude)) {
				AntPathMatcher antPathMatcher = new AntPathMatcher();
				if (antPathMatcher.match(exclude, uri)) {
					return true;
				}
			}
		}
		return false;
	}

	public void destroy() {

	}

}
