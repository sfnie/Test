package org.ratchetgx.orion.widgets.attachment;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentPreUploadValidator {
	public void validator( MultipartFile multfile, String attachinfo) throws Exception;
}
