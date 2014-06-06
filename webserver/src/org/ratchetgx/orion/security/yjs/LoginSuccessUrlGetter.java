package org.ratchetgx.orion.security.yjs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.ratchetgx.orion.security.ids.IdsLoginSuccessUrlGetter;

public class LoginSuccessUrlGetter extends IdsLoginSuccessUrlGetter {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public String getDefaultTargetUrl(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws Exception {
		
		String retUrl = super.getDefaultTargetUrl(request, response, authentication);
		log.debug("retUrl:" + retUrl);
		
		return retUrl;
	}

}
