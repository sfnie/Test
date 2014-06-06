package org.ratchetgx.orion.widgets.attachment.impl;

import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.widgets.attachment.AttachmentPosition;
import org.ratchetgx.orion.widgets.attachment.AttachmentPositionCreator;
import org.springframework.stereotype.Component;

@Component("defaultAttachmentPositionCreator")
public class DefaultAttachmentPositionCreator implements
		AttachmentPositionCreator {

	public AttachmentPosition positionCreator(String attachInfo) {
		FileAttachmentPosition positionimpl = new FileAttachmentPosition();
		// 基于文件存储方式,调用文件位置接口,可以通过读取配置文件获取
		String filepath=String.valueOf(SsfwUtil.getValue(SsfwUtil.WEBAPP_ABSOLUTE_PATH));
		positionimpl.setDirPath(filepath);
		positionimpl.setFileName(SsfwUtil.getDbUtil().getSysguid());// 指定服务端文件名称
		return positionimpl;
	}

}
