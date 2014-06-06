package org.ratchetgx.orion.security.mixjaccount;

import edu.sjtu.jaccount.JAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

public class MixJAccountManagerHelper implements ApplicationContextAware {

	private ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext appCtx)
			throws BeansException {
		this.appCtx = appCtx;
	}

	private String siteid;

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getSiteid() {
		return siteid;
	}

	private JAccountManager jam;

	public JAccountManager getJam() {
		if (jam == null) {
			WebApplicationContext webAppCtx = (WebApplicationContext) appCtx;
			String ctxPath = webAppCtx.getServletContext().getRealPath("/");
			log.info("ctxPath=" + ctxPath);
			jam = new JAccountManager(siteid, ctxPath);
		}

		return jam;
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());
}
