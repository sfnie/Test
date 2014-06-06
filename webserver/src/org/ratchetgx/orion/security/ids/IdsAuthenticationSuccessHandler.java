/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.ids;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.security.DefaultTargetUrlGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 *
 * @author hrfan
 */
public class IdsAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private DefaultTargetUrlGetter defaultTargetUrlGetter;
	
	public void setDefaultTargetUrlGetter(
			DefaultTargetUrlGetter defaultTargetUrlGetter) {
		this.defaultTargetUrlGetter = defaultTargetUrlGetter;
	}
	
    public IdsAuthenticationSuccessHandler() {
    }

    /**
     * Constructor which sets the <tt>defaultTargetUrl</tt> property of the base
     * class.
     *
     * @param defaultTargetUrl the URL to which the user should be redirected on
     * successful authentication.
     */
    public IdsAuthenticationSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    /**
     * Calls the parent class {@code handle()} method to forward or redirect to
     * the target URL, and then calls {@code clearAuthenticationAttributes()} to
     * remove any leftover session data.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    	
    	request.getSession().setAttribute(Constants.IDS_SESSION_LOGINED, true);
        
    	if (request.getParameter("redirectUrl") != null) {
			String redirectUrl = request.getParameter("redirectUrl");
			// response.sendRedirect(redirectUrl);
			this.getRedirectStrategy().sendRedirect(request, response,
					redirectUrl);
		} else {

			String targetUrl = null;
			if (defaultTargetUrlGetter != null) {
				try {
					targetUrl = defaultTargetUrlGetter.getDefaultTargetUrl(
							request, response, authentication);
				} catch (Exception e) {
					log.error("", e);
				}
			}

			if (!StringUtils.isEmpty(targetUrl)) {
				if (response.isCommitted()) {
					logger.debug("Response has already been committed. Unable to redirect to "
							+ targetUrl);
					return;
				}
				this.getRedirectStrategy().sendRedirect(request, response,
						targetUrl);
			} else {
				handle(request, response, authentication);
			}

			clearAuthenticationAttributes(request);
		}
    }
}
