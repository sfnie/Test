package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.widgets.attachment.AttachmentPostUploadHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("defaultAttachmentPostUploadHandler")
public class DefaultAttachmentPostUploadHandler implements
		AttachmentPostUploadHandler {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	public String handle(String attachWid, String attachinfo) {
		// TODO Auto-generated method stub
		log.debug("附件上传后被调用处理");
		return null;
	}

}
