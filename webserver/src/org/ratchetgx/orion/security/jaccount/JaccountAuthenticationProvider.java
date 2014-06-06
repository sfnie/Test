/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.jaccount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @author hrfan
 */
public class JaccountAuthenticationProvider implements AuthenticationProvider,
		InitializingBean, MessageSourceAware {

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
			return null;
		}

		UsernamePasswordAuthenticationToken authRequest = (UsernamePasswordAuthenticationToken) authentication;
		String bh = (String) authRequest.getPrincipal();

		UserDetails loadedUser;
		try {

			if (bh != null && !"".equals(bh)) {
				loadedUser = retrieveUser(bh);
				log.info("loadeduser=" + loadedUser);

				UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
						loadedUser, authentication.getCredentials(),
						loadedUser.getAuthorities());
				result.setDetails(authentication.getDetails());

				return result;
			}
		} catch (Exception ex) {
			throw new JaccountAuthenticationException(ex.getMessage());
		}

		return null;
	}

	public boolean supports(Class<?> authentication) {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private UserDetails retrieveUser(String username)
			throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = userDetailsService.loadUserByUsername(username);
		} catch (UsernameNotFoundException notFound) {
			log.error("", notFound);
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new JaccountAuthenticationException("未找到相应的用户信息");
		}

		return loadedUser;
	}

	

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private MessageSource messageSource;
	private UserDetailsService userDetailsService;
	
	private JAccountManagerHelper jamHelper;

	public void setJamHelper(JAccountManagerHelper jamHelper) {
		this.jamHelper = jamHelper;
	}
	

}
