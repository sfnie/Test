/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.jaccount;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 *
 * @author hrfan
 */
public class JaccountAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public JaccountAuthenticationSuccessHandler() {
    }

    /**
     * Constructor which sets the <tt>defaultTargetUrl</tt> property of the base
     * class.
     *
     * @param defaultTargetUrl the URL to which the user should be redirected on
     * successful authentication.
     */
    public JaccountAuthenticationSuccessHandler(String defaultTargetUrl) {
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
        request.getSession().setAttribute(JAccountManagerHelper.JACCOUNT_SESSION_LOGINED, true);
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }
}
