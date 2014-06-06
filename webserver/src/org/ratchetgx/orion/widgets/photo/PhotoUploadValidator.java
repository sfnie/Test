package org.ratchetgx.orion.widgets.photo;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * 照片上传校验器
 * 
 * @author hrfan
 * 
 */
public interface PhotoUploadValidator {
	public void validate(MultipartFile multFile,Map params)
			throws Exception;
}
