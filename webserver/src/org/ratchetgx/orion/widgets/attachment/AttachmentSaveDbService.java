package org.ratchetgx.orion.widgets.attachment;

import java.sql.SQLException;
import java.util.HashMap;

import org.ratchetgx.orion.common.util.BizobjUtil;
import org.ratchetgx.orion.common.util.Condition;
import org.ratchetgx.orion.common.util.ConditionGroup;
import org.ratchetgx.orion.common.util.RelOperEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AttachmentSaveDbService {
	@Autowired
	private BizobjUtil bizobjUtil;

	/**
	 * 保存本地文件附件信息
	 * 
	 * @param attachmentData
	 * @param attachmentFileData
	 * @throws SQLException
	 */
	public void saveFileAttachmentData(HashMap<String, Object> attachmentData,
			HashMap<String, Object> attachmentFileData) throws SQLException {
		bizobjUtil.insert("SS_ATTACHMENT", attachmentData);
		bizobjUtil.insert("SS_ATTACHMENT_FILE", attachmentFileData);
	}

	/**
	 * 删除本地文件附件信息
	 * 
	 * @param attachmentId
	 * @throws SQLException
	 */
	public void deleteFileAttachmentData(String attachmentId)
			throws SQLException {
		bizobjUtil.delete("SS_ATTACHMENT", attachmentId);
		ConditionGroup cg = new ConditionGroup();
		cg.addCondition(new Condition("ATTACHMENT_WID", RelOperEnum.EQUAL, attachmentId));
		bizobjUtil.delete("SS_ATTACHMENT_FILE", cg);
	}

	/**
	 * 保存数据库附件信息
	 * 
	 * @param attachmentData
	 * @param attachmentFileData
	 * @throws SQLException
	 */
	public void saveDbAttachmentData(HashMap<String, Object> attachmentData,
			HashMap<String, Object> attachmentFileData) throws SQLException {
	}

	/**
	 * 删除数据库附件信息
	 * 
	 * @param attachmentId
	 * @throws SQLException
	 */
	public void deleteDbAttachmentData(String attachmentId) throws SQLException {
	}
}
