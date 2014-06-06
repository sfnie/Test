/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.mixjaccount;

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
public class MixJaccountAuthenticationProvider implements
		AuthenticationProvider, InitializingBean, MessageSourceAware {

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (!(authentication instanceof MixJAccountUsernamePasswordAuthenticationToken)) {
			return null;
		}

		MixJAccountUsernamePasswordAuthenticationToken authRequest = (MixJAccountUsernamePasswordAuthenticationToken) authentication;
		String bh = (String) authRequest.getPrincipal();

		UserDetails loadedUser;
		try {
			if (bh != null && !"".equals(bh)) {
				loadedUser = retrieveUser(bh);
				UsernamePasswordAuthenticationToken result = new MixJAccountUsernamePasswordAuthenticationToken(
						loadedUser, authentication.getCredentials(),
						loadedUser.getAuthorities(), authRequest.getMode());
				result.setDetails(authentication.getDetails());

				return result;
			}
		} catch (Exception ex) {
			throw new MixJaccountAuthenticationException(ex.getMessage());
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
			throw new MixJaccountAuthenticationException("未找到相应的用户信息");
		}

		return loadedUser;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private MessageSource messageSource;
	private UserDetailsService userDetailsService;

	private MixJAccountManagerHelper jamHelper;

	public void setJamHelper(MixJAccountManagerHelper jamHelper) {
		this.jamHelper = jamHelper;
	}

}
