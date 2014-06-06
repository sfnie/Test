package org.ratchetgx.orion.security;

import java.util.Map;

public interface AttachInfoGetter {
	
	/**
	 * 获取编号为bh的人员的附加信息
	 * 
	 * @param bh
	 * @return
	 */
	public Map getAttachInfo(String bh);
}
