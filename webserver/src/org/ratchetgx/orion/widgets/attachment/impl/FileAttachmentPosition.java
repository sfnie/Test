package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.widgets.attachment.AttachmentPosition;

public class FileAttachmentPosition implements AttachmentPosition {
	// 目录路径
	private String dirPath = "";
	// 文件名称
	private String fileName = "";

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
