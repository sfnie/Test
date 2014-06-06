/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.security.ids;

import com.wiscom.is.IdentityManager;
import com.wiscom.is.SSOToken;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.security.SsfwUserDetails;
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
public class IdsAuthenticationProvider implements AuthenticationProvider,
		InitializingBean, MessageSourceAware {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private MessageSource messageSource;

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (!((authentication instanceof UsernamePasswordAuthenticationToken) && (authentication
				.getPrincipal().equals(IdsAuthenticationFilter.IDS_IDENTIFIER) || authentication
				.getPrincipal().equals(IdsAuthenticationFilter.DB_IDENTIFIER)))) {
			return null;
		}

		String principal = (String) authentication.getPrincipal();

		if (IdsAuthenticationFilter.IDS_IDENTIFIER.equals(principal)) {

			UsernamePasswordAuthenticationToken authRequest = (UsernamePasswordAuthenticationToken) authentication;
			String tokenValue = (String) authRequest.getCredentials();
			if (tokenValue == null || "".equals(tokenValue.trim())) {
				throw new IdsAuthenticationException("tokenValue为空");
			}

			UserDetails loadedUser;
			try {
				IdentityManager IdentityManager = iceMgr.getIdentityManager();
				SSOToken ssoToken = IdentityManager.validateToken(tokenValue);
				if (ssoToken != null) {
					String userDN = ssoToken.getUserId();
					log.info("userDN=" + userDN);
					if (userDN != null && !userDN.trim().equals("")) {
						String userId = LdapUtil.parserUserDN(userDN);
						String userName = IdentityManager
								.getUserNameByID(userId);
						log.info("userId=" + userId + ";userName=" + userName);
//						// 南大数据中心，ID转工号。
//						String staffId = null;
//						String[] arrStaffIds = IdentityManager
//								.getUserAttribute(userId, "eduPersonStaffID");
//						if (arrStaffIds != null && arrStaffIds.length > 0)
//							staffId = arrStaffIds[0].toString(); // 工号
//						if (StringUtils.isBlank(staffId)) {
//							if (log.isDebugEnabled()) {
//								log.debug("不能获取工号，用户ID:" + staffId);
//							}
//						} else {
//							if (log.isDebugEnabled()) {
//								log.debug("用户ID:" + userId + "，工号为:" + staffId);
//							}
//						}
//						loadedUser = retrieveUser(staffId,
//								IdsAuthenticationFilter.IDS_IDENTIFIER);
						loadedUser = retrieveUser(userId,
							IdsAuthenticationFilter.IDS_IDENTIFIER);
						UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
								loadedUser, authentication.getCredentials(),
								loadedUser.getAuthorities());
						result.setDetails(authentication.getDetails());

						return result;
					}
				}
			} catch (Exception ex) {
				throw new IdsAuthenticationException(ex.getMessage());
			}
		} else {
			UsernamePasswordAuthenticationToken authRequest = (UsernamePasswordAuthenticationToken) authentication;
			String[] credentials = (String[]) authRequest.getCredentials();

			try {
				UserDetails loadedUser = retrieveUser(credentials[0],
						IdsAuthenticationFilter.DB_IDENTIFIER);

				UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
						loadedUser, credentials[1], loadedUser.getAuthorities());

				result.setDetails(authentication.getDetails());

				return result;
			} catch (Exception ex) {
				throw new IdsAuthenticationException(ex.getMessage());
			}
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

	private UserDetails retrieveUser(String username, String userLoginType)
			throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = userDetailsService.loadUserByUsername(username);
			if (loadedUser instanceof SsfwUserDetails) {
				((SsfwUserDetails) loadedUser).setUserLoginType(userLoginType);
			}
		} catch (UsernameNotFoundException notFound) {
			log.error("", notFound);
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new AuthenticationServiceException(
					repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new IdsAuthenticationException("未找到相应的用户信息");
		}

		return loadedUser;
	}

	public void setIceMgr(IceManager iceMgr) {
		this.iceMgr = iceMgr;
	}

	private IceManager iceMgr;

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	private UserDetailsService userDetailsService;
}
