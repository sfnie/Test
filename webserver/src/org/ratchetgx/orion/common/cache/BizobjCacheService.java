/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.cache;

import java.io.Serializable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择范围拦截器，用于缓存选择范围结果
 * 
 * @author hrfan
 */
public class BizobjCacheService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	private Cache bizobjCache;

	public void setBizobjCache(Cache bizobjCache) {
		this.bizobjCache = bizobjCache;
	}
	
	public Object invokeBizobjUtil(ProceedingJoinPoint joinPoint) {
		String targetName = joinPoint.getThis().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		Object result = null;
		String cacheKey = getCacheKey(targetName, methodName, arguments);
		Element element = bizobjCache.get(cacheKey);
		if (element == null) {
			synchronized (this) {
				log.debug(":" + cacheKey);
				element = bizobjCache.get(cacheKey);
				if (element == null) {// 调用实际的方法
					try {
						result = joinPoint.proceed();
					} catch (Throwable e) {
					}
					element = new Element(cacheKey, (Serializable) result);
					bizobjCache.put(element);
				}
			}
		}

		return element.getValue();
	}

	private String getCacheKey(String targetName, String methodName,
			Object[] arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append(targetName).append(".").append(methodName).append(CacheConstants.CACHE_ELEMENT_KEY);
		if ((arguments != null) && (arguments.length != 0)) {
			for (int i = 0; i < arguments.length; i++) {
				sb.append(".").append(arguments[i]);
			}
		}
		return sb.toString();
	}
}
