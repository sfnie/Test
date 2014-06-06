/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.web;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.owasp.validator.html.PolicyException;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.util.AntisamyUtil;
import org.ratchetgx.orion.common.util.BizobjUtil;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author hrfan
 */
public class SsfwUtilFilter implements Filter {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static final boolean debug = false;
	// The filter configuration object we are associated with. If
	// this value is null, this filter instance is not currently
	// configured.
	private FilterConfig filterConfig = null;
	private WebApplicationContext appCtx;
	private String webappAbsolutePath = "";

	public SsfwUtilFilter() {
	}

	private void doBeforeProcessing(ServletRequest request,
			ServletResponse response) throws IOException, ServletException {
		if (debug) {
			log("SsfwUtilFilter:DoBeforeProcessing");
		}
	}

	private void doAfterProcessing(ServletRequest request,
			ServletResponse response) throws IOException, ServletException {
		if (debug) {
			log("SsfwUtilFilter:DoAfterProcessing");
		}

		// Write code here to process the request and/or response after
		// the rest of the filter chain is invoked.

		// For example, a logging filter might log the attributes on the
		// request object after the request has been processed.
		/*
		 * for (Enumeration en = request.getAttributeNames();
		 * en.hasMoreElements(); ) { String name = (String)en.nextElement();
		 * Object value = request.getAttribute(name); log("attribute: " + name +
		 * "=" + value.toString());
		 * 
		 * }
		 */

		// For example, a filter might append something to the response.
		/*
		 * PrintWriter respOut = new PrintWriter(response.getWriter());
		 * respOut.println
		 * ("<P><B>This has been appended by an intrusive filter.</B>");
		 */
	}

	/**
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (debug) {
			log("SsfwUtilFilter:doFilter()");
		}

		doBeforeProcessing(request, response);

		Throwable problem = null;
		try {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			ServletContext sc = httpRequest.getSession().getServletContext();
			if (sc.getAttribute("combobox") == null) {
				SelectRangeVisitor srv = (SelectRangeVisitor) appCtx
						.getBean("selectRangeVisitor");
				StringBuilder sb = new StringBuilder();
				Iterator iter = srv.getComboboxNames().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					sb.append(",");
					sb.append("'");
					sb.append(name);
					sb.append("'");
				}
				if (sb.length() > 0) {
					sc.setAttribute("combobox", sb.substring(1));
					log.info("combobox=" + sc.getAttribute("combobox"));
				}
			}
			if (sc.getAttribute("combotable") == null) {
				SelectRangeVisitor srv = (SelectRangeVisitor) appCtx
						.getBean("selectRangeVisitor");
				Map<String, JSONObject> combotableConfigs = srv
						.getCombotableConfigs();
				JSONArray combotables = new JSONArray();
				Iterator itr = combotableConfigs.keySet().iterator();
				while (itr.hasNext()) {
					String name = (String) itr.next();
					JSONObject config = new JSONObject();
					config.put("name", name);
					config.put("config", combotableConfigs.get(name));
					combotables.put(config);
				}
				sc.setAttribute("combotable", combotables);
				log.info("combotable=" + sc.getAttribute("combotable"));
			}
			if (sc.getAttribute("combotree") == null) {
				SelectRangeVisitor srv = (SelectRangeVisitor) appCtx
						.getBean("selectRangeVisitor");

				StringBuilder sb = new StringBuilder();
				Iterator iter = srv.getCombotreeNames().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					sb.append(",");
					sb.append("'");
					sb.append(name);
					sb.append("'");
				}
				if (sb.length() > 0) {
					sc.setAttribute("combotree", sb.substring(1));
					log.info("combotree=" + sc.getAttribute("combotree"));
				}
			}

			DbUtil dbUtil = (DbUtil) appCtx.getBean("dbUtil");
			SsfwUtil.setValue(SsfwUtil.COMPONENT_DBUTIL, dbUtil);

			BizobjUtil bizobjUtil = (BizobjUtil) appCtx.getBean("bizobjUtil");
			SsfwUtil.setValue(SsfwUtil.COMPONENT_BIZOBJ_UTIL, bizobjUtil);

			SsfwUtil.setValue(SsfwUtil.CURRENT_SESSION,
					httpRequest.getSession());

			SsfwUtil.setValue(SsfwUtil.WEBAPP_ABSOLUTE_PATH, webappAbsolutePath);

			SsfwUtil.setValue(SsfwUtil.CURRENT_CTX,
					appCtx);
			
			SsfwUtil.setValue(SsfwUtil.CURRENT_REQUEST,
					request);
			
			chain.doFilter(request, response);
		} catch (Throwable t) {
			// If an exception is thrown somewhere down the filter chain,
			// we still want to execute our after processing, and then
			// rethrow the problem after that.
			problem = t;
			t.printStackTrace();
		}

		doAfterProcessing(request, response);

		// If there was a problem, we want to rethrow it if it is
		// a known type, otherwise log it.
		if (problem != null) {
			if (problem instanceof ServletException) {
				throw (ServletException) problem;
			}
			if (problem instanceof IOException) {
				throw (IOException) problem;
			}
			sendProcessingError(problem, response);
		}
	}

	/**
	 * Return the filter configuration object for this filter.
	 */
	public FilterConfig getFilterConfig() {
		return (this.filterConfig);
	}

	/**
	 * Set the filter configuration object for this filter.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * Destroy method for this filter
	 */
	public void destroy() {
	}

	/**
	 * Init method for this filter
	 */
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		if (filterConfig != null) {
			if (debug) {
				log("SsfwUtilFilter:Initializing filter");
			}
			webappAbsolutePath = filterConfig.getServletContext().getRealPath(
					"/");
			log.info("webappAbsolutePath=" + webappAbsolutePath);
			appCtx = (WebApplicationContext) filterConfig
					.getServletContext()
					.getAttribute(
							WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		}
	}

	/**
	 * Return a String representation of this object.
	 */
	@Override
	public String toString() {
		if (filterConfig == null) {
			return ("SsfwUtilFilter()");
		}
		StringBuffer sb = new StringBuffer("SsfwUtilFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}

	private void sendProcessingError(Throwable t, ServletResponse response) {
		String stackTrace = getStackTrace(t);

		if (stackTrace != null && !stackTrace.equals("")) {
			try {
				response.setContentType("text/html");
				PrintStream ps = new PrintStream(response.getOutputStream());
				PrintWriter pw = new PrintWriter(ps);
				pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); // NOI18N

				// PENDING! Localize this for next official release
				pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
				pw.print(stackTrace);
				pw.print("</pre></body>\n</html>"); // NOI18N
				pw.close();
				ps.close();
				response.getOutputStream().close();
			} catch (Exception ex) {
			}
		} else {
			try {
				PrintStream ps = new PrintStream(response.getOutputStream());
				t.printStackTrace(ps);
				ps.close();
				response.getOutputStream().close();
			} catch (Exception ex) {
			}
		}
	}

	public static String getStackTrace(Throwable t) {
		String stackTrace = null;
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.close();
			sw.close();
			stackTrace = sw.getBuffer().toString();
		} catch (Exception ex) {
		}
		return stackTrace;
	}

	public void log(String msg) {
		filterConfig.getServletContext().log(msg);
	}
}
