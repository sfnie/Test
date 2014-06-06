/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.module.common.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ratchetgx.orion.common.util.AntisamyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author hrfan
 */
@Service
public class BizobjService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AntisamyUtil antisamyUtil;

	public Map<String, String> getBizobjs(HttpServletRequest httpRequest) {
		Map<String, String> bizobjs = new HashMap<String, String>();
		String[] bizobjConfigs = httpRequest.getParameterValues("bizobjs");
		if (bizobjConfigs != null) {
			for (int i = 0; i < bizobjConfigs.length; i++) {
				String bizobjConfig = bizobjConfigs[i];
				String[] bizobj = bizobjConfig.split(":");
				bizobjs.put(bizobj[0], bizobj[1]);
			}
		}
		return bizobjs;
	}

	/**
	 * 获取业务对象的数据（可以有多条记录的数据）
	 * 
	 * @param httpRequest
	 * @param bizobj
	 * @return
	 */
	public List<Map<String, String>> getDatasOfBizobj(
			HttpServletRequest httpRequest, String bizobj) {
		long start = System.currentTimeMillis();
		Map<Integer, Map<String, String>> datas = new HashMap<Integer, Map<String, String>>();

		List<String> fitParameterNames = new ArrayList<String>();

		Enumeration parameterNames = httpRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			if (parameterName.indexOf(bizobj + ".") >= 0) {
				fitParameterNames.add(parameterName);
			}
		}
		for (int i = 0; i < fitParameterNames.size(); i++) {
			String parameterName = fitParameterNames.get(i);
			String columnName = parameterName.substring(parameterName
					.lastIndexOf(".") + 1);
			String[] parameterValues = httpRequest
					.getParameterValues(parameterName);
			for (int j = 0; j < parameterValues.length; j++) {
				Map<String, String> data = datas.get(j);
				if (data == null) {
					data = new HashMap<String, String>();
					datas.put(j, data);
				}
				
				/** 去除XML不支持的字符 */
				parameterValues[j] = stripNonValidXMLCharacters(parameterValues[j]);
				                
				//data.put(columnName, antisamyUtil.filtXss(parameterValues[j]));
				data.put(columnName, parameterValues[j]);
			}
		}

		List<Map<String, String>> sortedDatas = new ArrayList<Map<String, String>>();
		for (int i = 0; i < datas.entrySet().size(); i++) {
			sortedDatas.add(datas.get(i));
		}

		long end = System.currentTimeMillis();

		log.debug("共耗时：" + (end - start));

		return sortedDatas;
	}

	/**
	 * 获取没有业务对象的参数值
	 * 
	 * @param httpRequest
	 * @return
	 */
	public Map<String, String[]> getDatasWithoutBizobj(
			HttpServletRequest httpRequest) {
		Map<String, String[]> datas = new HashMap<String, String[]>();

		Enumeration parameterNames = httpRequest.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			if (!bizobjContained(httpRequest, parameterName)) {
				datas.put(parameterName,
						httpRequest.getParameterValues(parameterName));
			}
		}

		return datas;
	}

	private boolean bizobjContained(HttpServletRequest httpRequest,
			String parameterName) {
		if (parameterName == null) {
			return false;
		}

		Map<String, String> bizobjs = getBizobjs(httpRequest);

		Iterator itr = bizobjs.keySet().iterator();
		while (itr.hasNext()) {
			String bo = (String) itr.next();
			if (parameterName.startsWith(bo + ".")) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 去除给定的字符串中不被XML支持的字符
	 * This method ensures that the output String has only valid XML unicode
	 * characters as specified by the XML 1.0 standard. For reference, please
	 * see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the
	 * standard</a>. This method will return an empty String if the input is
	 * null or empty.
	 * 
	 * @param in
	 *            The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in) {
	    StringBuffer out = new StringBuffer(); // Used to hold the output.
	    char current; // Used to reference the current character.

	    if (in == null || ("".equals(in)))
	        return ""; // vacancy test.
	    for (int i = 0; i < in.length(); i++) {
	        current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
	                                // here; it should not happen.
	        if ((current == 0x9) || (current == 0xA) || (current == 0xD)
	                || ((current >= 0x20) && (current <= 0xD7FF))
	                || ((current >= 0xE000) && (current <= 0xFFFD))
	                || ((current >= 0x10000) && (current <= 0x10FFFF)))
	            out.append(current);
	    }
	    return out.toString();
	}

}
