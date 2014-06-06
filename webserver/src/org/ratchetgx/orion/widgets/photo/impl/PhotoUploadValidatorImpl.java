package org.ratchetgx.orion.widgets.photo.impl;

import java.util.Map;

import org.ratchetgx.orion.widgets.photo.PhotoUploadValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component(value = "PhotoUploadValidatorImpl")
public class PhotoUploadValidatorImpl implements PhotoUploadValidator {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void validate(MultipartFile multFile,Map params)
			throws Exception {
		String bh = (String) params.get("bh");
		String value = (String) params.get("value");
		// 上传文件的大小
		// long size = multFile.getSize();
		// if (size > (1024 * 1024 * 10)) {
		// errorMessage = "只允许上传10M之内的图片";
		// }
		
		// if (!PhotoUtil.supportType(suffix.toLowerCase())) {
		// errorMessage = "只允许上传jpg,jpeg,gif,png格式的图片";
		//throw new Exception(errorMessage);
		// }

		
		log.info("validate=====");
	}

}
