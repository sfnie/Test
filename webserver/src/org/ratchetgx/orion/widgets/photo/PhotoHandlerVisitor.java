/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.photo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * 
 * @author hrfan
 */
@Component(value="photoHandlerVisitor")
public class PhotoHandlerVisitor implements InitializingBean,
		ApplicationContextAware {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private Map<String, PhotoHandler> handlers = new HashMap<String, PhotoHandler>();
	private Map<String, PhotoUploadValidator> validators = new HashMap<String, PhotoUploadValidator>();
	private ApplicationContext applicationContext;

	public void init() throws Exception {
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("photo-handlers.xml");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(is);
			Element srs = document.getRootElement();
			for (Iterator srItr = srs.elementIterator(); srItr.hasNext();) {
				Element sr = (Element) srItr.next();
				String name = sr.elementText("name");
				// 处理器
				String classDefine = sr.elementText("class-define");
				String beanDefine = sr.elementText("bean-define");
				PhotoHandler ph = null;
				if (classDefine != null && !"".equals(classDefine.trim())) {
					String className = classDefine;
					Class cls;
					cls = Class.forName(className);
					ph = (PhotoHandler) cls.newInstance();
					log.info("根据类名\"" + className + "\"初始化实例。");
				} else if (beanDefine != null && !"".equals(beanDefine.trim())) {
					ph = (PhotoHandler) applicationContext.getBean(beanDefine);
					log.info("根据bean标识\"" + beanDefine + "\"从上下文获取实例。");
				}
				if (ph != null) {
					handlers.put(name, ph);
				}
				// 校验器
				String classValidator = sr.elementText("class-validator");
				String beanValidator = sr.elementText("bean-validator");
				PhotoUploadValidator puv = null;
				if (classValidator != null && !"".equals(classValidator.trim())) {
					String className = classValidator;
					Class cls;
					cls = Class.forName(className);
					puv = (PhotoUploadValidator) cls.newInstance();
				} else if (beanValidator != null
						&& !"".equals(beanValidator.trim())) {
					puv = (PhotoUploadValidator) applicationContext
							.getBean(beanValidator);
				}
				if (puv != null) {
					validators.put(name, puv);
				}
			}
		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}
	}

	public PhotoHandler getPhotoHander(String handlerName) {
		return handlers.get(handlerName);
	}

	public PhotoUploadValidator getPhotoUploadValidator(String handlerName) {
		return validators.get(handlerName);
	}

	public void afterPropertiesSet() throws Exception {
		init();
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
