/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.cache;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.ratchetgx.orion.widgets.selectrange.ComboboxSelectRange;
import org.ratchetgx.orion.widgets.selectrange.CombotableSelectRange;
import org.ratchetgx.orion.widgets.selectrange.CombotreeSelectRange;
import org.ratchetgx.orion.widgets.selectrange.SelectRange;
import org.ratchetgx.orion.widgets.selectrange.SelectRangeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选择范围缓存服务
 * 
 * @author hrfan
 */
public class SelectRangeCacheService {

	// 选择范围数据
	public void setSelectRangeCache(Cache selectRangeCache) {
		this.selectRangeCache = selectRangeCache;
	}

	// 选择范围定义缓存
	public void setSelectRangeDefineCache(Cache selectRangeDefineCache) {
		this.selectRangeDefineCache = selectRangeDefineCache;
	}

	// 加载选择范围定义
	public void loadCache() throws Exception {
		readComboboxConfig();
		readCombotreeConfig();
		readCombotableConfig();
		log.info("加载选择范围定义完成。");
	}

	// 截面调用
	public Object invokeSelectRangeVisitor(ProceedingJoinPoint joinPoint) {
		String targetName = joinPoint.getThis().getClass().getName();
		String methodName = joinPoint.getSignature().getName();
		Object[] arguments = joinPoint.getArgs();
		String selectRangeName = (String) arguments[0];
		SelectRange selectRange = getSelectRangeByName(selectRangeName);
		if (selectRange == null) {
			log.error("没有相应的选择范围\"" + selectRangeName + "\"");
			return null;
		}

		Object result = null;
		if (!selectRange.isCached()) {// 未配置缓存数据
			log.debug("\"" + selectRangeName + "\"未配置缓存。");
			try {
				result = joinPoint.proceed();
			} catch (Throwable e) {
				log.error("", e);
			}
			return result;
		}

		String cacheKey = getCacheKey(targetName, methodName, arguments);
		net.sf.ehcache.Element element = selectRangeCache.get(cacheKey);
		if (element == null) {
			synchronized (this) {
				log.debug(":" + cacheKey);
				element = selectRangeCache.get(cacheKey);
				if (element == null) {// 调用实际的方法
					try {
						result = joinPoint.proceed();
					} catch (Throwable e) {
						log.error("", e);
					}
					element = new net.sf.ehcache.Element(cacheKey,
							(Serializable) result);
					selectRangeCache.put(element);
				}
			}
		}

		return element.getObjectValue();
	}

	private String getCacheKey(String targetName, String methodName,
			Object[] arguments) {
		StringBuilder sb = new StringBuilder();
		sb.append(targetName).append(".").append(methodName)
				.append(CacheConstants.CACHE_ELEMENT_KEY);
		if ((arguments != null) && (arguments.length != 0)) {
			for (int i = 0; i < arguments.length; i++) {
				sb.append(".").append(arguments[i]);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private SelectRange getSelectRangeByName(String selectName) {
		Map<String, ComboboxSelectRange> comboboxRanges = (Map<String, ComboboxSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX)
				.getObjectValue();

		Map<String, CombotableSelectRange> combotableRanges = (Map<String, CombotableSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE)
				.getObjectValue();

		Map<String, CombotreeSelectRange> combotreeRanges = (Map<String, CombotreeSelectRange>) selectRangeDefineCache
				.get(SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE)
				.getObjectValue();

		SelectRange selectRange = comboboxRanges.get(selectName);
		if (selectRange == null) {
			selectRange = combotableRanges.get(selectName);
		}
		if (selectRange == null) {
			selectRange = combotreeRanges.get(selectName);
		}

		return selectRange;
	}

	private void readComboboxConfig() throws Exception {
		try {
			Map<String, ComboboxSelectRange> selectRanges = new HashMap<String, ComboboxSelectRange>();
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("combobox.xml");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element srs = document.getRootElement();
			for (Iterator srItr = srs.elementIterator(); srItr.hasNext();) {
				Element sr = (Element) srItr.next();
				String name = sr.elementText("name");
				String description = sr.elementText("description");
				String classDefine = sr.elementText("class-define");
				String table = sr.elementText("table");
				String valueColumn = sr.elementText("value-column");
				String labelColumn = sr.elementText("label-column");
				String filter = sr.elementText("filter");
				List<String> cascades = null;

				Element cascadeElements = sr.element("cascades");
				if (cascadeElements != null) {
					cascades = new ArrayList<String>();
					for (Iterator cascadeIter = cascadeElements
							.elementIterator("cascade"); cascadeIter.hasNext();) {
						Element e = (Element) cascadeIter.next();
						cascades.add(e.getTextTrim());
					}
				}

				Map<String, Object> pairs = null;
				Element pairsElement = sr.element("pairs");
				if (pairsElement != null) {
					pairs = new HashMap<String, Object>();
					Iterator pairItr = pairsElement.elements().iterator();
					while (pairItr.hasNext()) {
						Element pairElement = (Element) pairItr.next();
						pairs.put(pairElement.attributeValue("key"),
								pairElement.attributeValue("value"));
					}
				}

				String cachedValue = sr.attributeValue("cached");
				Boolean cached = true;
				if (cachedValue != null
						&& "false".equals(cachedValue.trim().toLowerCase())) {
					cached = false;
				}

				String order = sr.elementText("order");

				String epstarTableName = sr.elementText("epstar-tableName");
				String epstarColumnName = sr.elementText("epstar-columnName");

				ComboboxSelectRange selectRange = new ComboboxSelectRange(name,
						description, classDefine, table, valueColumn,
						labelColumn, filter, cascades, pairs, cached, order,
						epstarTableName, epstarColumnName);
				selectRanges.put(name, selectRange);
			}

			net.sf.ehcache.Element element = new net.sf.ehcache.Element(
					SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOBOX,
					selectRanges);
			selectRangeDefineCache.put(element);

		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}
	}

	private void readCombotableConfig() throws Exception {
		try {
			Map<String, CombotableSelectRange> selectRanges = new HashMap<String, CombotableSelectRange>();

			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("combotable.xml");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element srs = document.getRootElement();
			for (Iterator srItr = srs.elementIterator(); srItr.hasNext();) {
				Element sr = (Element) srItr.next();
				String name = sr.elementText("name");
				String description = sr.elementText("description");
				String table = sr.elementText("table");
				String filter = sr.elementText("filter");
				String order = sr.elementText("order");

				List<String> cascades = null;
				Element cascadeElements = sr.element("cascades");
				if (cascadeElements != null) {
					cascades = new ArrayList<String>();
					for (Iterator cascadeIter = cascadeElements
							.elementIterator("cascade"); cascadeIter.hasNext();) {
						Element e = (Element) cascadeIter.next();
						cascades.add(e.getTextTrim());
					}
				}

				Map display = new HashMap();
				Element displayElement = sr.element("display");
				if (displayElement != null) {
					display.put("title", displayElement.elementText("title"));
					List<Map> columns = new ArrayList<Map>();
					for (Iterator columnIter = displayElement
							.element("columns").elementIterator("column"); columnIter
							.hasNext();) {
						Element e = (Element) columnIter.next();

						Map columnMap = new HashMap();
						columnMap.put("header", e.elementText("header"));
						columnMap.put("name", e.elementText("name"));
						columnMap.put("width", e.elementText("width"));

						String flag = e.attributeValue("flag");
						if (flag != null && !"".equals(flag)) {
							columnMap.put("flag", flag);
						}
						columns.add(columnMap);
					}
					display.put("columns", columns);
				}

				CombotableSelectRange selectRange = new CombotableSelectRange(
						name, description, table, filter, cascades, display,
						order);
				selectRanges.put(name, selectRange);
			}

			net.sf.ehcache.Element element = new net.sf.ehcache.Element(
					SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTABLE,
					selectRanges);
			selectRangeDefineCache.put(element);

		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}
	}

	private void readCombotreeConfig() throws Exception {
		try {
			Map<String, CombotreeSelectRange> selectRanges = new HashMap<String, CombotreeSelectRange>();

			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("combotree.xml");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element srs = document.getRootElement();
			for (Iterator srItr = srs.elementIterator(); srItr.hasNext();) {
				Element sr = (Element) srItr.next();
				String name = sr.elementText("name");
				String description = sr.elementText("description");
				String classDefine = sr.elementText("class-define");
				String table = sr.elementText("table");
				String valueColumn = sr.elementText("value-column");
				String labelColumn = sr.elementText("label-column");
				String labelFullColumn = sr.elementText("label-full-column");
				String order = sr.elementText("order");
				List<String> cascades = null;

				Element cascadeElements = sr.element("cascades");
				if (cascadeElements != null) {
					cascades = new ArrayList<String>();
					for (Iterator cascadeIter = cascadeElements
							.elementIterator("cascade"); cascadeIter.hasNext();) {
						Element e = (Element) cascadeIter.next();
						cascades.add(e.getTextTrim());
					}
				}

				List<String> parentDefines = null;
				Element parentDefineElements = sr.element("parent-defines");
				if (parentDefineElements != null) {
					parentDefines = new ArrayList<String>();
					for (Iterator parentDefineIter = parentDefineElements
							.elementIterator("parent-define"); parentDefineIter
							.hasNext();) {
						Element e = (Element) parentDefineIter.next();
						parentDefines.add(e.getTextTrim());
					}
				}

				String cachedValue = sr.attributeValue("cached");
				Boolean cached = true;
				if (cachedValue != null
						&& "false".equals(cachedValue.trim().toLowerCase())) {
					cached = false;
				}

				CombotreeSelectRange selectRange = new CombotreeSelectRange(
						name, description, classDefine, table, valueColumn,
						labelColumn, labelFullColumn, cascades, parentDefines,
						cached, order);
				selectRanges.put(name, selectRange);
			}

			net.sf.ehcache.Element element = new net.sf.ehcache.Element(
					SelectRangeVisitor.SELECTRANGE_DEFINE_COMBOTREE,
					selectRanges);
			selectRangeDefineCache.put(element);

		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}
	}

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Cache selectRangeCache;
	private Cache selectRangeDefineCache;
}
