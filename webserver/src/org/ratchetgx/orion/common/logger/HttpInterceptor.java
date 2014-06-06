package org.ratchetgx.orion.common.logger;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class HttpInterceptor implements HandlerInterceptor {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private LogUtil logUtil;
	@Autowired
	private DbUtil dbUtil;

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception exp)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj, ModelAndView modview)
			throws Exception {

		HashMap<String, Object> csmap = new HashMap<String, Object>();
		HashMap<String, String> interdatamap = new HashMap<String, String>();
		log.debug("ACTIP : " + logUtil.getIpAddr(request));
		// 记录系统登录
		csmap.put("wid", dbUtil.getSysguid());
		csmap.put("userid", SsfwUtil.getCurrentBh());
		String remoteUser = request.getRemoteUser();
		if (StringUtils.isBlank(remoteUser)) {
			remoteUser = "游客";
		}
		csmap.put("username", remoteUser);
		csmap.put("actip", logUtil.getIpAddr(request));
		csmap.put("acttime", new Date());

		/**
		 * 登录类型：WID,ACTID:NULL,ACTTABLE:NULL,USERID,USERNAME,ACTTIME,ACTTYPE
		 * :1,ACTIP,ACTURL
		 */
		csmap.put("acturl", "登录系统：" + request.getRequestURL().toString());
		csmap.put("acttype", "1"); // 系统登录类型
		csmap.put("actid", "");
		csmap.put("acttable", "");
		logUtil.addLog(csmap, interdatamap);

	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

}
