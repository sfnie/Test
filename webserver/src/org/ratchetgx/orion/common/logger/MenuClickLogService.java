package org.ratchetgx.orion.common.logger;

import java.util.HashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class MenuClickLogService {

	@Autowired
	private AsyncAnalysisData aad;
	@Autowired
	private SyncAnalysisData sad;

	// 监控菜单点击请求
	@After("execution(* org.ratchetgx.orion.module.common.web.IndexController.navMenu*(..))")
	public void invokeNavMenu(JoinPoint jp) throws Throwable {
		HashMap<String, String> dataMap = new HashMap<String, String>();
		if (jp.getArgs()[0] != null) {
			dataMap.put("menuId", jp.getArgs()[0].toString());
			sad.logAnalyData(LogConstants.OPT_CLICK, "", dataMap);
		}
	}

}
