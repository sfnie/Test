package org.ratchetgx.orion.widgets.attachment;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang.ArrayUtils;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.message.SendMessageJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentTempFileClearup {

	private Logger log = LoggerFactory.getLogger(AttachmentTempFileClearup.class);

	public void work() {
		String tempFileFolder = SsfwUtil.contextPath + "temp" + File.separator;
		File file = new File(tempFileFolder);
		if (file.exists()) {
			String[] fileNames = file.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".zip")) {
						return true;
					}
					return false;
				}
			});
			if (!ArrayUtils.isEmpty(fileNames)) {
				log.debug("正在删除一键下载临时文件，共" + fileNames.length + "个。");
				File tempFile = null;
				for (int i = 0; i < fileNames.length; i++) {
					tempFile = new File(tempFileFolder + fileNames[i]);
					if (tempFile.exists()) {
						tempFile.delete();
					}
				}
			}
		}
	}
}
