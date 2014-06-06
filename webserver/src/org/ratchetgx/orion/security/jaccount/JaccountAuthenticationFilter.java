/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.jaccount;

import edu.sjtu.jaccount.JAccountManager;
import java.io.IOException;
import java.util.Hashtable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 *
 * @author hrfan
 */
public class JaccountAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public JaccountAuthenticationFilter() {
        super("/j_spring_jaccount_security_check");
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        JAccountManager jam = new JAccountManager("jagraduate20130107", getServletContext().getRealPath("/"));
        Hashtable ht = jam.checkLogin(request, response, request.getSession(), request.getRequestURI());
        log.debug("ht=" + ht);
        log.debug("jam.hasTicketInURL=" + jam.hasTicketInURL);
        String bh = (String) ht.get("id");
        log.info("bh=" + bh);

        final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(bh, "");
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return this.getAuthenticationManager().authenticate(authRequest);
    }
    private JAccountManagerHelper jamHelper;

    public void setJamHelper(JAccountManagerHelper jamHelper) {
        this.jamHelper = jamHelper;
    }
    private Logger log = LoggerFactory.getLogger(this.getClass());
}
