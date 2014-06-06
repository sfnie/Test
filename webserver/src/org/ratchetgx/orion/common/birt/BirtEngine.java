package org.ratchetgx.orion.common.birt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class BirtEngine implements ServletContextAware,InitializingBean,DisposableBean {

	private ServletContext sc;

	private IReportEngine birtEngine = null;
	private Properties configProps = new Properties();
	private final String configFile = "BirtConfig.properties";

	public void setServletContext(ServletContext sc) {
		this.sc = sc;
	}

	public IReportEngine getBirtEngine() {
		return birtEngine;
	}

	private void loadEngineProps() {
		try {
			// Config File must be in classpath
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream in = null;
			in = cl.getResourceAsStream(configFile);
			configProps.load(in);
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private synchronized void initializeBirtEngine() {
		EngineConfig config = new EngineConfig();
		if (configProps != null) {
			String logLevel = configProps.getProperty("logLevel");
			Level level = Level.OFF;
			if ("SEVERE".equalsIgnoreCase(logLevel)) {
				level = Level.SEVERE;
			} else if ("WARNING".equalsIgnoreCase(logLevel)) {
				level = Level.WARNING;
			} else if ("INFO".equalsIgnoreCase(logLevel)) {
				level = Level.INFO;
			} else if ("CONFIG".equalsIgnoreCase(logLevel)) {
				level = Level.CONFIG;
			} else if ("FINE".equalsIgnoreCase(logLevel)) {
				level = Level.FINE;
			} else if ("FINER".equalsIgnoreCase(logLevel)) {
				level = Level.FINER;
			} else if ("FINEST".equalsIgnoreCase(logLevel)) {
				level = Level.FINEST;
			} else if ("OFF".equalsIgnoreCase(logLevel)) {
				level = Level.OFF;
			}

			config.setLogConfig(configProps.getProperty("logDirectory"), level);
		}

		config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
				Thread.currentThread().getContextClassLoader());
		// if you are using 3.7 POJO Runtime no need to setEngineHome
		// config.setEngineHome("");
		IPlatformContext context = new PlatformServletContext(sc);
		config.setPlatformContext(context);

		try {
			Platform.startup(config);
		} catch (BirtException e) {
			e.printStackTrace();
		}

		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		birtEngine = factory.createReportEngine(config);
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void afterPropertiesSet() throws Exception {
		loadEngineProps();
		initializeBirtEngine();
	}

	public void destroy() throws Exception {
		if (birtEngine == null) {
			return;
		}
		birtEngine.shutdown();
		Platform.shutdown();
		birtEngine = null;
	}
}