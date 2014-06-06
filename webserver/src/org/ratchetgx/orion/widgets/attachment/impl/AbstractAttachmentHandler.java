package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.widgets.attachment.AttachmentHandler;
import org.ratchetgx.orion.widgets.attachment.AttachmentPositionCreator;
import org.ratchetgx.orion.widgets.attachment.AttachmentPostUploadHandler;
import org.ratchetgx.orion.widgets.attachment.AttachmentPreDownloadHandler;
import org.ratchetgx.orion.widgets.attachment.AttachmentPreUploadValidator;

public abstract class AbstractAttachmentHandler implements AttachmentHandler {

	private boolean multiEnabled;
	private AttachmentPositionCreator attachmentPositionCreator;
	private AttachmentPostUploadHandler attachmentPostUploadHandler;
	private AttachmentPreDownloadHandler attachmentPreDownloadHandler;
	private AttachmentPreUploadValidator attachmentPreUploadValidator;

	public void setMultiEnabled(boolean multiEnabled) {
		this.multiEnabled = multiEnabled;
	}

	public void setAttachmentPositionCreator(
			AttachmentPositionCreator attachmentPositionCreator) {
		this.attachmentPositionCreator = attachmentPositionCreator;
	}

	public void setAttachmentPostUploadHandler(
			AttachmentPostUploadHandler attachmentPostUploadHandler) {
		this.attachmentPostUploadHandler = attachmentPostUploadHandler;
	}

	public void setAttachmentPreDownloadHandler(
			AttachmentPreDownloadHandler attachmentPreDownloadHandler) {
		this.attachmentPreDownloadHandler = attachmentPreDownloadHandler;
	}

	public void setAttachmentPreUploadValidator(
			AttachmentPreUploadValidator attachmentPreUploadValidator) {
		this.attachmentPreUploadValidator = attachmentPreUploadValidator;
	}

	public boolean getMultiEnabled() {
		return this.multiEnabled;
	}

	public AttachmentPositionCreator getAttachmentPositionCreator() {
		return this.attachmentPositionCreator;
	}

	public AttachmentPostUploadHandler getAttachmentPostUploadHandler() {
		return this.attachmentPostUploadHandler;
	}

	public AttachmentPreDownloadHandler getAttachmentPreDownloadHandler() {
		return this.attachmentPreDownloadHandler;
	}

	public AttachmentPreUploadValidator getAttachmentPreUploadValidator() {
		return this.attachmentPreUploadValidator;
	}

}
