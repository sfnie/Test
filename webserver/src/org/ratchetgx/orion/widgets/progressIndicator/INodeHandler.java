package org.ratchetgx.orion.widgets.progressIndicator;

import org.ratchetgx.orion.common.util.DbUtil;
import org.springframework.jdbc.core.JdbcTemplate;

public interface INodeHandler {
	
	
	/**
	 * 获取流程节点在UI界面显示时所需要的数据
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param customObject
	 * @return 节点状态信息(JSON格式)
	 * @throws Exception
	 */
	public String getNodeStatus(String flowID, String nodeID, String userID, String loginUserID, Object customObject) throws Exception;
	
	
	/**
	 * 用户点击流程节点时的处理
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param customObject 自定义对象
	 * @return 提示信息(JSON格式)
	 * @throws Exception
	 */
	public String doOnClick (String flowID, String nodeID, String userID, String loginUserID, Object customObject) throws Exception;
	
	/**
	 * 
	 * @param jdbcTemplate
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	
	/**
	 * @param dbUtil
	 */
	public void setDbUtil(DbUtil dbUtil);
}
