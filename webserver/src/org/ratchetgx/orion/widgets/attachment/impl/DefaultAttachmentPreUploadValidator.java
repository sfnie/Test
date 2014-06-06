package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.widgets.attachment.AttachmentPreUploadValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("defaultAttachmentPreUploadValidator")
public class DefaultAttachmentPreUploadValidator implements
		AttachmentPreUploadValidator {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public void validator(MultipartFile multfile, String attachinfo) {
		// 支持上传格式、大小校验限制
		log.debug("附件校验成功");
	}

}
