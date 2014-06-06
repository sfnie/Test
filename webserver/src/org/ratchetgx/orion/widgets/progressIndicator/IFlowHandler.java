package org.ratchetgx.orion.widgets.progressIndicator;

import org.springframework.jdbc.core.JdbcTemplate;

public interface IFlowHandler {


	/**
	 * 判断是否有权限访问进度导航组件
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	public boolean hasPermission(String flowID, String userID, String loginUserID) throws Exception;
	
	
	/**
	 * 初始化进度导航组件
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return 自定义对象
	 * @throws Exception
	 */
	public Object init(String flowID, String userID, String loginUserID) throws Exception;

	/**
	 * 
	 * @param jdbcTemplate
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
}
