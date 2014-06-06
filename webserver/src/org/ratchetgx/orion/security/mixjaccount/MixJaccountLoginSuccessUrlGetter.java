package org.ratchetgx.orion.security.mixjaccount;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ratchetgx.orion.security.DefaultTargetUrlGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

public class MixJaccountLoginSuccessUrlGetter implements DefaultTargetUrlGetter {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	public String getDefaultTargetUrl(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws Exception {
		
		String defultURI = "/index.do";
		
		String retUrl = request.getParameter("retUrl");
		log.debug("retUrl(encode):" + retUrl);
		if (retUrl != null && !"".equals(retUrl)) {
			log.debug("retUrl(decode):" + URLDecoder.decode(retUrl, "utf-8"));
			defultURI = retUrl;
		}
		log.debug("defultURI:" + defultURI);
		return defultURI;
	}
	
	
}
