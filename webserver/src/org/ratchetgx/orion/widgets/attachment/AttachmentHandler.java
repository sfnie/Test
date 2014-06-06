package org.ratchetgx.orion.widgets.attachment;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentHandler {

	public boolean getMultiEnabled();

	public AttachmentPositionCreator getAttachmentPositionCreator();

	public AttachmentPostUploadHandler getAttachmentPostUploadHandler();

	public AttachmentPreDownloadHandler getAttachmentPreDownloadHandler();

	public AttachmentPreUploadValidator getAttachmentPreUploadValidator();

	public String uploadAttachment(MultipartFile multfile, String attachInfo,
			AttachmentPosition position,String serviceId) throws Exception;
}
