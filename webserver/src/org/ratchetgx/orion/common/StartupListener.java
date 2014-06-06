package org.ratchetgx.orion.common;

import java.io.File;
import javax.servlet.ServletContextEvent;
import org.springframework.web.context.ContextLoaderListener;

/**
 * @author
 */
public class StartupListener extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		String realPath = event.getServletContext().getRealPath("/");
		SsfwUtil.contextPath = realPath;
		File logsFolder = new File(realPath + File.separator + "logs");
		if (!logsFolder.exists()) {
			logsFolder.mkdirs();
		}
		System.setProperty("LOGSDIR", logsFolder.getPath());
		super.contextInitialized(event);
	}
}
