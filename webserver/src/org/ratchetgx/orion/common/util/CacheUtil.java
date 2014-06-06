package org.ratchetgx.orion.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 缓存操作
 * 
 * @author hrfan
 * 
 */
@Component(value = "cacheUtil")
public class CacheUtil {

	@Autowired
	private CacheManager cacheManager;

	public List<Cache> listCaches() {
		List<Cache> caches = new ArrayList<Cache>();
		String[] strArray = cacheManager.getCacheNames();
		for (int i = 0; i < strArray.length; i++) {
			caches.add(cacheManager.getCache(strArray[i]));
		}
		return caches;
	}

	public List<String> listCacheNames() {
		List<String> cacheNames = new ArrayList<String>();
		String[] strArray = cacheManager.getCacheNames();
		for (int i = 0; i < strArray.length; i++) {
			cacheNames.add(strArray[i]);
		}
		return cacheNames;
	}

	public List<Element> listCacheElements(Cache cache) {
		List<Element> elements = new ArrayList<Element>();
		Iterator<Object> keyItr = cache.getKeys().iterator();
		while (keyItr.hasNext()) {
			Object key = keyItr.next();
			Element element = cache.get(key);
			elements.add(element);
		}
		return elements;
	}

	public List<Object> listCacheElementKeys(Cache cache) {
		List<Object> elementKeys = new ArrayList<Object>();
		Iterator<Object> keyItr = cache.getKeys().iterator();
		while (keyItr.hasNext()) {
			Object key = keyItr.next();
			elementKeys.add(key);
		}
		return elementKeys;
	}

	public List<Element> listCacheElements(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		return listCacheElements(cache);
	}

	public List<Object> listCacheElementKeys(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		return listCacheElementKeys(cache);
	}

	public void clearCache(Cache cache) {
		cache.removeAll();
	}

	public void clearCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		cache.removeAll();
	}

	public void clearElement(Cache cache, Object elementKey) {
		cache.remove(elementKey);
	}

	public void clearElement(String cacheName, Object elementKey) {
		Cache cache = cacheManager.getCache(cacheName);
		cache.remove(elementKey);
	}

	public Cache findCacheByName(String cacheName) {
		return cacheManager.getCache(cacheName);
	}

	/**
	 * 设置模块缓存数据
	 * 
	 * @param key
	 * @param data
	 */
	public void setModuleData(String key, Object data) {
		Cache cache = findCacheByName("moduleCache");
		Element element = new Element(key, data);
		cache.put(element);
	}

	/**
	 * 获取模块缓存数据
	 * 
	 * @param key
	 * @return
	 */
	public Object getModuleData(String key) {
		Cache cache = findCacheByName("moduleCache");
		if (!cache.isKeyInCache(key)) {
			return null;
		}
		Element element = cache.get(key);
		return element.getObjectValue();
	}
}
