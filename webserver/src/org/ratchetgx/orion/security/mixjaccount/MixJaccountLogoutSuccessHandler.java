package org.ratchetgx.orion.security.mixjaccount;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ratchetgx.orion.security.SsfwUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import edu.sjtu.jaccount.JAccountManager;

public class MixJaccountLogoutSuccessHandler implements  LogoutSuccessHandler  {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private MixJAccountManagerHelper jamHelper;
	
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) 
			throws IOException, ServletException {
		
		HttpSession session = request.getSession();
		
		if(auth instanceof UsernamePasswordAuthenticationToken){   //账号登录
			
			if (auth instanceof MixJAccountUsernamePasswordAuthenticationToken) {
				MixJAccountUsernamePasswordAuthenticationToken mjAuth = (MixJAccountUsernamePasswordAuthenticationToken)auth;
				
				SsfwUserDetails userDetails = (SsfwUserDetails)auth.getPrincipal();
				String bh = userDetails.getBh();
				
				String mode = mjAuth.getMode();  /** 登录方式: db / jaccount */
				log.debug("mode:" + mode);
				if ("db".equals(mode)) {  /** DB认证情况下 */
					
					String loginPageUrl = (String)session.getAttribute(MixJAccountConstants.JACCOUNT_SESSION_LOGINED_URL);
					log.debug("loginPageUrl:" + loginPageUrl);
					session.invalidate();
					log.info("logedout success:" + bh);
					response.sendRedirect(loginPageUrl);
					return;
					
				} if ("jaccount".equals(mode)) {   /** JACCOUNT认证情况下 */
					
					session.invalidate();
					
					JAccountManager jam = new JAccountManager(jamHelper.getSiteid(), request.getSession().getServletContext().getRealPath("/"));
					boolean loggedout = jam.logout(request, response, "/ssfw/login_jaccount.jsp");
					if (loggedout) {
						log.info("logedout success:" + bh);
					} else {
						log.info("logedout failed:" + bh);
					}
					return;
					
				}
			}
		}
		
	}

	public MixJAccountManagerHelper getJamHelper() {
		return jamHelper;
	}

	public void setJamHelper(MixJAccountManagerHelper jamHelper) {
		this.jamHelper = jamHelper;
	}

	
	
	
}
