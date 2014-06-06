package org.ratchetgx.orion.common.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class CacheAspect {

	private static Logger log = LoggerFactory.getLogger(CacheAspect.class);

	private BizobjCacheService bizobjCacheService;
	private SelectRangeCacheService selectRangeCacheService;

	public void setBizobjCacheService(BizobjCacheService bizobjCacheService) {
		this.bizobjCacheService = bizobjCacheService;
	}

	public void setSelectRangeCacheService(
			SelectRangeCacheService selectRangeCacheService) {
		this.selectRangeCacheService = selectRangeCacheService;
	}

	@Pointcut("execution(* org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor.cache*(..))")
	public void selectRangeVisitorMethods() {
	}

	//@Pointcut("execution(* org.ratchetgx.orion.common.util.BizobjUtilHelper.getColumnTypes(..))")
	@Pointcut("execution(* org.ratchetgx.orion.common.util.BizobjUtilHelper.getColumn*(..))")
    public void bizobjUtilMethods() {
	}

	@Around("selectRangeVisitorMethods()")
	public Object selectRangeVisitorAdvice(ProceedingJoinPoint joinPoint)
			throws Throwable {
		return selectRangeCacheService.invokeSelectRangeVisitor(joinPoint);
	}

	@Around("bizobjUtilMethods()")
	public Object bizobjUtilAdvice(ProceedingJoinPoint joinPoint)
			throws Throwable {
		return bizobjCacheService.invokeBizobjUtil(joinPoint);
	}
}
