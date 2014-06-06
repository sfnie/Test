package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.widgets.attachment.AttachmentPosition;

public class DbAttachmentPosition implements AttachmentPosition {

	private String tableName = "";
	private String fieldName = "";
	private String recordId = "";

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTabelName() {
		return tableName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getRecordId() {
		return recordId;
	}

}
