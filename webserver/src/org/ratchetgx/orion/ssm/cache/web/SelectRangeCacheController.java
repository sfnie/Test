package org.ratchetgx.orion.ssm.cache.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;

import org.eclipse.birt.report.model.api.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ratchetgx.orion.common.cache.CacheConstants;
import org.ratchetgx.orion.common.cache.SelectRangeCacheService;
import org.ratchetgx.orion.common.util.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 选择范围缓存处理
 * 
 * @author hrfan
 * 
 */
@Controller
@RequestMapping(value = "cache")
public class SelectRangeCacheController {

	private static Logger log = LoggerFactory
			.getLogger(SelectRangeCacheController.class);

	@Autowired
	private CacheUtil cacheUtil;

	@RequestMapping(method = RequestMethod.GET, value = "index")
	public String index() {
		return "cache/index";
	}

	/**
	 * 查找缓存元素
	 * 
	 * @return
	 * @throws JSONException
	 */
	@RequestMapping(value = "findCacheElementKeys")
	public String findCacheElementKeys(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String cacheName = request.getParameter("cacheName");
		log.debug("cacheName=" + cacheName);
		String filterStr = request.getParameter("filterStr");
		log.debug("filterStr=" + filterStr);

		JSONObject rev = new JSONObject();
		JSONArray elementKeyArray = new JSONArray();

		try {
			rev.put("elementKeys", elementKeyArray);

			if (!StringUtil.isEmpty(cacheName)) {
				List<String> cacheNames = new ArrayList<String>();
				if ("all".equals(cacheName)) {
					cacheNames = cacheUtil.listCacheNames();
				} else {
					cacheNames.add(cacheName);
				}
				for (int i = 0; i < cacheNames.size(); i++) {
					String name = (String) cacheNames.get(i);
					Cache cache = (Cache) cacheUtil.findCacheByName(name);
					if (cache != null) {
						Iterator elementKeyItr = cacheUtil
								.listCacheElementKeys(cache).iterator();
						while (elementKeyItr.hasNext()) {
							Object elementKey = elementKeyItr.next();
							String elementKeyStr = elementKey.toString();
							log.debug("elementKeyStr : " + elementKeyStr);

							if (elementKeyStr
									.indexOf(CacheConstants.CACHE_ELEMENT_KEY) > -1) {
								elementKeyStr = elementKeyStr
										.substring(elementKeyStr
												.indexOf(CacheConstants.CACHE_ELEMENT_KEY)
												+ CacheConstants.CACHE_ELEMENT_KEY
														.length() + 1);
							}

							JSONObject obj = new JSONObject();
							obj.put("elementKey", elementKey);
							obj.put("elementKeyStr", elementKeyStr);
							int comboboxIndex = elementKeyStr
									.indexOf("combobox");
							int combotableIndex = elementKeyStr
									.indexOf("combotable");
							int combotreeIndex = elementKeyStr
									.indexOf("combotree");
							if (comboboxIndex > -1 || combotableIndex > -1
									|| combotreeIndex > -1) {
								continue;
							}
							if (StringUtil.isEmpty(filterStr)
									|| (!StringUtil.isEmpty(filterStr) && elementKeyStr
											.indexOf(filterStr) > -1)) {
								elementKeyArray.put(obj);
							}
						}
					}
				}

			}
		} catch (JSONException e) {
			log.error("", e);
		}

		response.setContentType("text/json;charset=utf-8");
		response.getWriter().println(rev);

		return null;
	}

	/**
	 * 清除缓存
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "clearCacheElementKeys")
	public String clearCacheElementKeys(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String cacheName = request.getParameter("cacheName");
		log.debug("cacheName=" + cacheName);
		String elementKeysStr = request.getParameter("elementKeys");
		log.debug("elementKeysStr=" + elementKeysStr);

		JSONObject rev = new JSONObject();
		try {
			rev.put("success", true);

			if (!StringUtil.isEmpty(cacheName)) {
				List<String> cacheNames = new ArrayList<String>();
				if ("all".equals(cacheName)) {
					cacheNames = cacheUtil.listCacheNames();
				} else {
					cacheNames.add(cacheName);
				}
				for (int i = 0; i < cacheNames.size(); i++) {
					String name = cacheNames.get(i);
					Cache cache = (Cache) cacheUtil.findCacheByName(name);
					JSONArray elementKeys = new JSONArray(elementKeysStr);
					for (int j = 0; j < elementKeys.length(); j++) {
						String elementKey = elementKeys.getString(j);
						boolean removed = cache.remove(elementKey);
						log.debug("delete:" + elementKey + "，removed ："
								+ removed + "。");

					}
				}
			}
		} catch (JSONException e) {
			log.error("", e);
			try {
				rev.put("success", false);
			} catch (JSONException e1) {
				log.error("", e1);
			}
		}

		response.setContentType("text/json;charset=utf-8");
		response.getWriter().println(rev);

		return null;
	}

	@RequestMapping(value = "findCacheNames")
	public String findCacheNames(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		JSONObject rev = new JSONObject();
		JSONArray cacheNames = new JSONArray();

		try {
			// Iterator cacheItr = cacheUtil.listCaches().iterator();
			// while (cacheItr.hasNext()) {
			// Cache cache = (Cache) cacheItr.next();
			// JSONObject o = new JSONObject();
			// o.put("cacheName", cache.getName());
			// cacheNames.put(o);
			// }
			JSONObject o = new JSONObject();
			o.put("cacheName", "selectRangeCache");
			cacheNames.put(o);
			rev.put("cacheNames", cacheNames);
		} catch (JSONException e) {
			log.error("", e);
		}

		response.setContentType("text/json;charset=utf-8");
		response.getWriter().println(rev);

		return null;
	}
}
