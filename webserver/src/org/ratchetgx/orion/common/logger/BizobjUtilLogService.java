package org.ratchetgx.orion.common.logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.util.BizobjUtil;
import org.ratchetgx.orion.common.util.ConditionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class BizobjUtilLogService {

	@Autowired
	private BizobjUtil bizobjUtil;
	@Autowired
	private LogUtil logUtil;
	@Autowired
	private AsyncAnalysisData aad;
	@Autowired
	private SyncAnalysisData sad;

	private Logger log = LoggerFactory.getLogger(BizobjUtilLogService.class);

	private final String[] type = { "java.lang.String", "java.util.Date",
			"int", "java.lang.Integer", "boolean", "java.lang.Boolean", "byte",
			"java.lang.Byte", "float", "java.lang.Float", "long",
			"java.lang.Long" };

	private String getMethodNameByFieldName(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
	}

	private void afterSave(HashMap<String, String> dataMap, String tableName)
			throws Exception {
		HttpServletRequest request = (HttpServletRequest) SsfwUtil.getValue(SsfwUtil.CURRENT_REQUEST);
		HashMap<String, String> requestMap = new HashMap<String, String>();
		String optType = dataMap.get("wid") == null ? LogConstants.OPT_NEW
				: LogConstants.OPT_UPDATE;
		Map<String, String> logCfg = (HashMap<String, String>) logUtil
				.getLogCfg(tableName);
		if (logCfg != null && logCfg.size() > 0) {
			String currentBh = SsfwUtil.getCurrentBh();
			requestMap.put("user", request.getRemoteUser());
			requestMap.put("addr", request.getRemoteAddr());
			if ("1".equals(logCfg.get("onoff"))
					&& "2".equals(logCfg.get("threadmodel"))) { // 已打开开关并且要求异步模式记录日志
				aad.logAnalyData(optType, tableName, requestMap, dataMap,
						currentBh);
			}
			if ("1".equals(logCfg.get("onoff"))
					&& "1".equals(logCfg.get("" + "threadmodel"))) { // 已打开开关并且要求同步模式记录日志
				sad.logAnalyData(optType, tableName, dataMap);
			}
		}
	}

	@Around("execution(* org.ratchetgx.orion.common.util.BizobjUtil.persist*(..)) or "
			+ " execution(* org.ratchetgx.orion.common.util.BizobjUtil.remove*(..)) ")
	public void invokePersist(ProceedingJoinPoint jp) throws Throwable {
		jp.proceed();
		Object domain = jp.getArgs()[0];
		Class<?> cls = domain.getClass();
		Field[] fields = cls.getDeclaredFields();
		if (fields == null) {
			return;
		}
		HashMap<String, String> dataMap = new HashMap<String, String>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldTypeName = field.getType().getName();
			if (!ArrayUtils.contains(type, fieldTypeName)) {
				continue;
			}
			String fieldName = field.getName();
			try {
				Method method = cls
						.getMethod(getMethodNameByFieldName(fieldName));
				method.setAccessible(true);
				Object object = method.invoke(domain);
				if (object != null) {
					dataMap.put(fieldName, object.toString());
				}
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
				continue;
			}
		}
		Annotation[] annotations = cls.getAnnotations();
		String tableName = cls.getName();
		// 获取业务对象 @Table中的表名
		if (!ArrayUtils.isEmpty(annotations)) {
			for (Annotation annotation : annotations) {
				String name = annotation.annotationType().getName();
				if ("javax.persistence.Table".equals(name)) {
					try {
						Method method = annotation.getClass().getMethod("name");
						tableName = (String) method.invoke(annotation);
						break;
					} catch (Exception e) {
						if (log.isErrorEnabled()) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		this.afterSave(dataMap, tableName);
	}

	// BizobjUtil方法执行监控
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Around("execution(* org.ratchetgx.orion.common.util.BizobjUtil.save*(..))")
	public void invokeSave(ProceedingJoinPoint jp) throws Throwable {
		HttpServletRequest request = (HttpServletRequest) SsfwUtil.getValue(SsfwUtil.CURRENT_REQUEST);
		HashMap<String, String> requestMap = new HashMap<String, String>();

		String bizobj = (String) jp.getArgs()[0];
		HashMap<String, String> dataMap = (HashMap) jp.getArgs()[1];
		String optType = ((HashMap) jp.getArgs()[1]).get("wid") == null ? LogConstants.OPT_NEW
				: LogConstants.OPT_UPDATE;

		Object obj = (Object) jp.proceed(); // 调用目标方法执行

		// 目标方法执行之后,获取日志配置信息
		Map<String, String> logCfg = (HashMap<String, String>) logUtil
				.getLogCfg(bizobj);

		if (logCfg != null && logCfg.size() > 0) {

			String currentBh = SsfwUtil.getCurrentBh();
			requestMap.put("user", request.getRemoteUser());
			requestMap.put("addr", request.getRemoteAddr());
			if ("1".equals(logCfg.get("onoff"))
					&& "2".equals(logCfg.get("threadmodel"))) { // 已打开开关并且要求异步模式记录日志
				aad.logAnalyData(optType, bizobj, requestMap, dataMap,
						currentBh);
			}
			if ("1".equals(logCfg.get("onoff"))
					&& "1".equals(logCfg.get("threadmodel"))) { // 已打开开关并且要求同步模式记录日志
				sad.logAnalyData(optType, bizobj, dataMap);
			}
		}

	}

	@Around("execution(* org.ratchetgx.orion.common.util.BizobjUtil.delete*(..))")
	public void invokeDelete(ProceedingJoinPoint jp) throws Throwable {
		HttpServletRequest request = (HttpServletRequest) SsfwUtil.getValue(SsfwUtil.CURRENT_REQUEST);
		String optType = LogConstants.OPT_DELETE;
		// 当前编号
		String currentBh = SsfwUtil.getCurrentBh();

		HashMap<String, String> requestInfoMap = new HashMap<String, String>();
		requestInfoMap.put("user", request.getRemoteUser());
		requestInfoMap.put("addr", request.getRemoteAddr());

		// 业务对象
		String bizobj = (String) jp.getArgs()[0];

		// 执行SQL
		StringBuffer sb = new StringBuffer();
		// 删除的数据列表
		List<Map> dataList = null;

		Object arg1 = jp.getArgs()[0];
		if (arg1 instanceof String) {
			String wid = (String) arg1;
			sb.append("DELETE FROM ");
			sb.append(bizobj);
			sb.append(" WHERE WID='");
			sb.append(wid);
			sb.append("'");

			dataList = new ArrayList();
			dataList.add(bizobjUtil.query(bizobj, wid));
		}

		if (arg1 instanceof ConditionGroup) {
			ConditionGroup conditionGroup = (ConditionGroup) arg1;
			if (conditionGroup != null) {
				sb.append("DELETE FROM ");
				sb.append(bizobj);
				sb.append(" WHERE ");
				sb.append(bizobjUtil.encodeCondtionGroup(conditionGroup)
						.getSql());
			}

			dataList = (List<Map>) bizobjUtil.query(bizobj, conditionGroup,
					null);
		}

		Object obj = (Object) jp.proceed();

		// 目标方法执行之后,获取日志配置信息
		Map<String, String> logCfg = (HashMap<String, String>) logUtil
				.getLogCfg(bizobj);
		// 调用删除方法之后
		if ("1".equals(logCfg.get("onoff"))
				&& "2".equals(logCfg.get("threadmodel"))) {
			Iterator itr = dataList.iterator();
			while (itr.hasNext()) {
				Map data = (Map) itr.next();
				aad.logDelinfo(sb.toString(), optType, bizobj, requestInfoMap,
						data, currentBh);
			}
		}

		if ("1".equals(logCfg.get("onoff"))
				&& "1".equals(logCfg.get("threadmodel"))) {
			Iterator itr = dataList.iterator();
			while (itr.hasNext()) {
				Map data = (Map) itr.next();
				sad.logDelinfo(sb.toString(), optType, bizobj, data);
			}
		}

	}

}
