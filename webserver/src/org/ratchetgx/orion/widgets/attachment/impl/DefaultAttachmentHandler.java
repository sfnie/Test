package org.ratchetgx.orion.widgets.attachment.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.widgets.attachment.AttachmentPosition;
import org.ratchetgx.orion.widgets.attachment.AttachmentSaveDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class DefaultAttachmentHandler extends AbstractAttachmentHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DbUtil dbUtil;
	@Autowired
	private AttachmentSaveDbService ass;

	/**
	 * 附件上传
	 */
	public String uploadAttachment(MultipartFile multpart, String attachInfo,
			AttachmentPosition position,String serviceId) throws Exception {

		try {

			String wid = "";
			// 本地文件附件上传
			if (position instanceof FileAttachmentPosition) {
				wid = uploadFileAttachment(multpart, attachInfo,
						(FileAttachmentPosition) position, serviceId);
			}

			// 保存至数据库附件上传
			if (position instanceof DbAttachmentPosition) {
				wid = uploadDbAttachment(multpart, attachInfo,
						(DbAttachmentPosition) position, serviceId);
			}

			return wid;

		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}

	}

	/**
	 * 上传保存为文件的附件
	 * 
	 * @param multpart
	 * @param attachInfo
	 * @param position
	 * @return
	 * @throws Exception
	 */
	private String uploadFileAttachment(MultipartFile multpart,
			String attachInfo, FileAttachmentPosition position, String serviceId)
			throws Exception {

		String suffix = multpart.getOriginalFilename().substring(
				multpart.getOriginalFilename().indexOf(".") + 1);

		String wid = dbUtil.getSysguid();

		File dir = new File(position.getDirPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(position.getDirPath() + File.separator + position.getFileName() + "."
				+ suffix);

		InputStream is = multpart.getInputStream();
		FileOutputStream fos = new FileOutputStream(f);
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) > 0) {
			fos.write(buf, 0, len);
		}
		is.close();
		fos.close();

		HashMap<String, Object> attachmentData = new HashMap<String, Object>();
		HashMap<String, Object> attachmentFileData = new HashMap<String, Object>();

		// 文件上传成功后将附件信息保存至数据库,根据storeMode参数组装HashMap;
		// 组装ss_attachment
		attachmentData.put("wid", wid);
		attachmentData.put("origin_name", multpart.getOriginalFilename());
		attachmentData.put("filetype", suffix);
		attachmentData.put("store_mode", "file");
		attachmentData.put("serviceid", serviceId);
		// 组装ss_attachment_file
		attachmentFileData.put("wid", dbUtil.getSysguid());
		attachmentFileData.put("file_path",
				position.getDirPath() + File.separator + position.getFileName() + "." + suffix);
		attachmentFileData.put("attachment_wid", attachmentData.get("wid"));

		// 调用saveService.save保存数据库
		ass.saveFileAttachmentData(attachmentData, attachmentFileData);

		return wid;
	}

	/**
	 * 上传保存至数据库的附件
	 * 
	 * @param multpart
	 * @param attachInfo
	 * @param position
	 * @return
	 * @throws Exception
	 */
	private String uploadDbAttachment(MultipartFile multpart,
			String attachInfo, DbAttachmentPosition position,String serviceId) throws Exception {
		return null;
	}

}
