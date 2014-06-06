package org.ratchetgx.orion.common.util;

import java.io.File;

import javax.servlet.ServletContext;

import org.owasp.validator.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class AntisamyUtil implements ServletContextAware {

	private static final Logger log = LoggerFactory
			.getLogger(AntisamyUtil.class);

	private String configFile;

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	private Boolean open = true;

	public void setOpen(Boolean open) {
		this.open = open;
	}

	private Policy policy;

	public void init() {
		try {
			log.debug("AntisamyUtil -- initialize");
			policy = Policy.getInstance(sc.getRealPath("/")
					+ File.separatorChar + configFile);
		} catch (PolicyException e) {
			log.error("", e);
		}
	}

	/**
	 * 根据相应规则过滤字符串
	 * 
	 * @param input
	 *            输入字符串
	 * @param policyUrl
	 *            规则文件地址
	 * @return
	 */
	public String filtXss(String input) {
		if (!open) {
			return input;
		}
		
		if (input == null) {
			return null;
		}
		String result = "";
		try {
			AntiSamy as = new AntiSamy();
			CleanResults cr = as.scan(input, policy);
			result = cr.getCleanHTML();
		} catch (Exception e) {
			log.error("clean xss str failed. cause: " + e.getMessage());
		}
		return result;
	}

	private ServletContext sc;

	public void setServletContext(ServletContext sc) {
		log.info("setServletContext");
		this.sc = sc;
	}
}
