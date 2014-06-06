package org.ratchetgx.orion.widgets.progressIndicator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.ratchetgx.orion.common.logger.LogUtil;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.ProcedureParameterDataType;
import org.ratchetgx.orion.common.util.ProcedureParameterDirection;
import org.ratchetgx.orion.common.util.ProcedureParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 进度导航引擎类
 *
 */
@Component
public class Engine {
	
	/**
	 * 日志
	 */
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static Engine instance = null;
	
	private HttpServletRequest httpRequest = null;
	
	private HttpSession session = null;
	
	/**
	 * dbUtil
	 */
	@Autowired
	private DbUtil dbUtil;
	
	/**
	 * jdbcTemplate
	 */
	@Autowired
	private JdbcTemplate jdbcTemplate;
	/**
	 * logUtil
	 */
	@Autowired
	private LogUtil logUtil;
	private Engine() {
		
	}
	
	
	public static Engine getInstance() throws Exception {
		if (instance == null) {
			instance = new Engine();
		}
		return instance;
	}
	
	/**
	 * @param request
	 * @throws Exception
	 */
	public void setCacheContext(HttpServletRequest request) throws Exception {
		
		request.getSession().getServletContext();
		session = request.getSession();
		httpRequest = request;
		
	}
	
	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	public String initFlow(String flowID, final String userID, String loginUserID) throws Exception {
		
		//第一步，判断是否有权限
		boolean hasPermission = hasFlowPermission(flowID, userID, loginUserID);
		if(!hasPermission){
			log.debug("没有权限");
			return "{}";
		}
		//取出流程id
		StringBuffer sb = new StringBuffer();
		String flowIDSessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
		"_FLOWID").toString();
		flowID = (String)session.getAttribute(flowIDSessionName);
		//第二步，读取流程主信息表中"处理程序"字段值
		String tmpsql = "select a.clcx from t_gggl_jddh_lczxx a   left join t_gggl_jddh_xslbdylc b on a.lcid = b.lcid "
				+ " left join t_xjgl_xjxx_yjsjbxx c on b.xslbdm = c.xslbdm where a.sfqy = '1' and c.xh = ?";
		String tmpStr = null;
		try {
			log.info("jdbcTemplate:"+jdbcTemplate);
			List clcxlist = jdbcTemplate.queryForList(tmpsql,
					new Object[] { userID });
			
			if (clcxlist.size() > 0) {
				Map map = (Map) clcxlist.get(0);
				tmpStr = (String) map.get("clcx");
			}	
			
		} catch (Exception e) {
			log.error("读取流程主信息表相关信息失败");
		}
		log.debug("tmpStr:"+tmpStr);
		
		String objectStr = null;
		if (tmpStr!= null) {
			if (tmpStr.startsWith("java:")) {
				//调用JAVA类方式初始化
				String classStr = tmpStr.substring(5,tmpStr.length());
				log.debug("动态加载流程实现类");
				Class flowClass = Class.forName(classStr);
				IFlowHandler flow = (IFlowHandler)flowClass.newInstance();
				flow.setJdbcTemplate(jdbcTemplate);
				Object o = flow.init(flowID, userID, loginUserID);
				objectStr = o.toString();
		
			} else if (tmpStr.startsWith("procedure")) {
				// 调用存储过程方式初始化
				String  procedureName = tmpStr.substring(10,tmpStr.length());
				
				objectStr = initFlowByProcedure(flowID, userID,loginUserID,"init",procedureName);
			}else{
				
				log.debug("获得处理程序异常。");
			}
		}
		//放到服务器缓存中
		setFlowCustomObjectToCache(flowID, userID, loginUserID, objectStr);
		
		//
		StringBuffer initStr = new StringBuffer();// 返回的初始化字符串
		String lcjdidStr = null;
		String lcjdmcStr = null;
		BigDecimal pxStr = null;
		String flcjdidStr = null;
		// 根据流程ID、userID取得用户的流程信息。
		String sql = "select t.lcjdid,t.lcjdmc,t.px,t.clcx from t_gggl_jddh_lcjd t left join t_gggl_jddh_lczxx d on d.lcid = t.lcid"
				+ " left join t_gggl_jddh_xslbdylc dylc on dylc.lcid = d.lcid  left join t_xjgl_xjxx_yjsjbxx jbxx on jbxx.xslbdm = dylc.xslbdm"
				+ " where t.sfqy = '1' and t.flcjdid='0' and jbxx.xh = ?  order by t.px";
		initStr.append("{'parentNodeList':[");
		// 父节点流程信息
		try {
			List lcjdxxList = jdbcTemplate.queryForList(sql,
					new Object[] { userID });
			if (lcjdxxList.size() > 0) {
				for (int i = 0; i < lcjdxxList.size(); i++) {
					Map map = (Map) lcjdxxList.get(i);
					lcjdidStr = (String) map.get("lcjdid");
					lcjdmcStr = (String) map.get("lcjdmc");
					pxStr = (BigDecimal) map.get("px");
					initStr.append("{'lcjdid':'").append(lcjdidStr).append("',");
					initStr.append("'lcjdmc':'").append(lcjdmcStr).append("',");
					initStr.append("'px':'").append(pxStr).append("'}");
					if (lcjdxxList.size() != 1 && (i != lcjdxxList.size() - 1)) {
						initStr.append(",");
					}
				}
			}
		} catch (Exception e) {
			log.error("查询父节点信息失败！"+ e.getMessage());
		}
		//子节点流程信息
		initStr.append("],'childNodeList':[");
		String sql2 = "select t.lcjdid,t.flcjdid,t.lcjdmc,t.px,t.clcx from t_gggl_jddh_lcjd t left join t_gggl_jddh_lczxx d on d.lcid = t.lcid"
			+ " left join t_gggl_jddh_xslbdylc dylc on dylc.lcid = d.lcid  left join t_xjgl_xjxx_yjsjbxx jbxx on jbxx.xslbdm = dylc.xslbdm"
			+ " where t.sfqy = '1' and t.flcjdid !='0' and jbxx.xh = ? order by t.px ";
		try {
			List lcjdxxList = jdbcTemplate.queryForList(sql2,
					new Object[] { userID });
			if (lcjdxxList.size() > 0) {
				for (int i = 0; i < lcjdxxList.size(); i++) {
					Map map = (Map) lcjdxxList.get(i);
					lcjdidStr = (String) map.get("lcjdid");
					lcjdmcStr = (String) map.get("lcjdmc");
					flcjdidStr = (String)map.get("flcjdid");
					initStr.append("{'lcjdid':'").append(lcjdidStr).append("',");
					initStr.append("'lcjdmc':'").append(lcjdmcStr).append("',");
					initStr.append("'flcjdid':'").append(flcjdidStr).append("'}");
					if (lcjdxxList.size() != 1 && (i != lcjdxxList.size() - 1)) {
						initStr.append(",");
					}
				}
			}
		} catch (Exception e) {
			log.error("查询子节点信息失败！");
		}
		initStr.append("]}");
		//放到缓存中
		//session.setAttribute("NodeList",initStr.toString());
		return initStr.toString();
	}



	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	public boolean hasFlowPermission(String flowID, String userID,
			String loginUserID) throws Exception {
		boolean hasPermission = false;
		// 获得流程主信息表中“处理程序”字段值
		//---------------
		log.debug("userID:"+userID);
		String sql = "select a.clcx ,a.lcid from t_gggl_jddh_lczxx a   left join t_gggl_jddh_xslbdylc b on a.lcid = b.lcid "
				+ " left join t_xjgl_xjxx_yjsjbxx c on b.xslbdm = c.xslbdm where a.sfqy = '1' and c.xh = ?";
		String clcxStr = null;
		String flowIDStr = null;
		try{
			List<Map<String,Object>> clcxlist = jdbcTemplate.queryForList(sql, new Object[] { userID });
			if (clcxlist.size() > 0) {
				Map map = (Map) clcxlist.get(0);
				clcxStr = (String) map.get("clcx");
				flowIDStr = (String) map.get("lcid");
		}
		}catch (Exception e){
			log.error("SQL查询错误:"+e.getMessage());
			e.printStackTrace();
			//
			return false;
		}
		log.debug(clcxStr);
		if (clcxStr != null) {
			if (clcxStr.startsWith("java:")) {
				String classStr = clcxStr.substring(5,clcxStr.length());
				log.debug("动态加载流程实现类");
				Class flowClass = Class.forName(classStr);
				IFlowHandler flow = (IFlowHandler)flowClass.newInstance();
				flow.setJdbcTemplate(jdbcTemplate);
				hasPermission = flow.hasPermission(flowIDStr, userID,
						loginUserID);
			} else if (clcxStr.startsWith("procedure:")) {
				//调用存储过程
				String  procedureName = clcxStr.substring(10,clcxStr.length());
				String permission = initFlowByProcedure(flowIDStr, userID,loginUserID,"hasPermission",procedureName);
				if ("1".equals(permission)) {
					hasPermission = true;
				} 
			}
		}
		//权限信息放入缓存中
		setFlowPermissionToCache(flowIDStr, userID, loginUserID, hasPermission);
		
//		StringBuffer sb = new StringBuffer();
//		String sessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
//				"_FLOWID").toString();
//		session.setAttribute(sessionName, flowIDStr);

		return hasPermission;
	}
	
	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @param hasPermission
	 * @throws Exception
	 */
	private void setFlowPermissionToCache(String flowID, String userID, String loginUserID, Boolean hasPermission) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		String sessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
				"_HASPERMISSION").toString();
		StringBuffer flowIDSb = new StringBuffer();
		String flowIDSessionName = flowIDSb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
		"_FLOWID").toString();
		session.setAttribute(sessionName, hasPermission);
		session.setAttribute(flowIDSessionName, flowID);
		
	}
	
	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	private boolean getFlowPermissionFromCache(String flowID, String userID, String loginUserID) throws Exception {
		StringBuffer sb = new StringBuffer();
		String sessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
				"_HASPERMISSION").toString();
		Boolean hasPermission = (Boolean) session.getAttribute(sessionName);
		return hasPermission;
	}
	
	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @param customObject
	 * @throws Exception
	 */
	private void setFlowCustomObjectToCache(String flowID, String userID,String loginUserID, Object customObject) throws Exception {
		StringBuffer sb = new StringBuffer();
		String sessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
				"_CUSTOMOBJECT").toString();
		session.setAttribute(sessionName,customObject);
	}
	
	/**
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @return
	 * @throws Exception
	 */
	private Object getFlowCustomObjectFromCache(String flowID, String userID, String loginUserID) throws Exception {
		StringBuffer sb = new StringBuffer();
		String sessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
				"_CUSTOMOBJECT").toString();
		return session.getAttribute(sessionName);
	}

	/**
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param customObject
	 * @return
	 * @throws Exception
	 */
	public String getNodeStatus(String flowID, String nodeID, String userID, String loginUserID, Object customObject) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		String flowIDSessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
		"_FLOWID").toString();
		flowID = (String)session.getAttribute(flowIDSessionName);
		//判断权限
		boolean hasPermission = getFlowPermissionFromCache(flowID, userID, loginUserID);
		
		if(!hasPermission){
			log.info("没有权限！");
			return "{}";
		}
		customObject = getFlowCustomObjectFromCache(flowID, userID, loginUserID);
		//读取流程节点表中"处理程序"字段值
		//---------------
		String sql = "select t.clcx from t_gggl_jddh_lcjd t left join t_gggl_jddh_lczxx d on d.lcid = t.lcid "
			+ " where t.sfqy = '1' and t.lcjdid=  ? and d.lcid = ? order by t.px ";
		List clcxlist = jdbcTemplate.queryForList(sql, new Object[] { nodeID,flowID});
		log.info("clcxlist:"+clcxlist.toString());
		String clcxStr = null;
		String nodeInfoStr = null;
		if (clcxlist.size() > 0) {
			Map map = (Map) clcxlist.get(0);
			clcxStr = (String) map.get("clcx");
		}
		
		//
		//nodeInfoStr = achieveNodeMethod("getNodeStatus",clcxStr);
		if (clcxStr!=null) {
			if (clcxStr.startsWith("java:")) {
				//如果是JAVA类实现
				//获得节点的相关信息
				String classStr = clcxStr.substring(5,clcxStr.length());
				log.debug("动态加载节点实现类");
				Class nodeClass = Class.forName(classStr);
				INodeHandler node = (INodeHandler)nodeClass.newInstance();
				node.setJdbcTemplate(jdbcTemplate);
				node.setDbUtil(dbUtil);
				nodeInfoStr = node.getNodeStatus(flowID, nodeID, userID, loginUserID, customObject);
				
			}else if(clcxStr.startsWith("procedure:")){
				
				String  procedureName = clcxStr.substring(10,clcxStr.length());
				
				nodeInfoStr = getNodeByProcedure(flowID, nodeID, userID, loginUserID, "getNodeStatus", customObject, procedureName);
			}
		}
		//如果是存储过程实现
		
		return nodeInfoStr;
	}

	/**
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param customObject
	 * @return
	 * @throws Exception
	 */
	public String doOnClickNode(String flowID, String nodeID, String userID, String loginUserID, Object customObject) throws Exception {
		
		StringBuffer sb = new StringBuffer();
		String flowIDSessionName = sb.append("PROGRESSINDICATOR_FLOW_").append(userID).append(
		"_FLOWID").toString();
		flowID = (String)session.getAttribute(flowIDSessionName);
		//判断节点权限?
		boolean hasPermission = getFlowPermissionFromCache(flowID, userID, loginUserID);
		if(!hasPermission){
			log.info("没有权限！");
			return "{}";
		}
		String nodeInfoStr = "";
		log.info("flowID:"+flowID+",nodeID:"+nodeID);
		//如果没有二级导航
		String sql = "select t.clcx from t_gggl_jddh_lcjd t left join t_gggl_jddh_lczxx d on d.lcid = t.lcid "
			+ " where t.sfqy = '1' and t.lcjdid=  ? and d.lcid = ? order by t.px ";
		List clcxlist = jdbcTemplate.queryForList(sql, new Object[] { nodeID,flowID});
		String clcxStr = null;
		if (clcxlist.size() > 0) {
			Map map = (Map) clcxlist.get(0);
			clcxStr = (String) map.get("clcx");
		}
		
		//String clcxStr = (String)session.getAttribute("nodeClcxStr");
		if (clcxStr!= null) {
			if (clcxStr.startsWith("java:")) {
				//如果是JAVA类实现
				//获得节点的相关信息
				String classStr = clcxStr.substring(5,clcxStr.length());
				log.debug("动态加载节点实现类:"+classStr);
				Class nodeClass = Class.forName(classStr);
				INodeHandler node = (INodeHandler)nodeClass.newInstance();
				node.setJdbcTemplate(jdbcTemplate);
				node.setDbUtil(dbUtil);
				nodeInfoStr = node.doOnClick(flowID, nodeID, userID, loginUserID, customObject);
				
			}else if(clcxStr.startsWith("procedure")){
				String	procedureName = clcxStr.substring(10,clcxStr.length());
				nodeInfoStr = getNodeByProcedure(flowID, nodeID, userID, loginUserID, "doOnClick", customObject, procedureName);
			}
		//1:点击流程节点
		recordOperLog(flowID, nodeID, userID, loginUserID, "1",nodeInfoStr);
		}
		return nodeInfoStr;
	}
	/**
	 * 通过存储过程方式初始化流程信息
	 * @param flowID
	 * @param userID
	 * @param loginUserID
	 * @param operType
	 * @param procedureName
	 * @throws Exception
	 */
	private String initFlowByProcedure(String flowID, String userID,
			String loginUserID, String operType,String procedureName) throws Exception {
		String jsonStr = null;
		try {
			ProcedureParameterList ppl = new ProcedureParameterList();
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_flowID", flowID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_userID", userID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_loginUserID",loginUserID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_operType",operType);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_message", null);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_hasPermission",null);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_json", null);
			int callReturn = dbUtil.executeProcedure(procedureName, ppl);
			String outMessage = null;
			if (callReturn > 0) {
				outMessage = (String) ppl.getParamterValue(4);
				if ("1".equals(outMessage)) {
					// 如果是init，则取出自定义对象值
					if ("init".equals(operType)) {
						jsonStr = (String) ppl.getParamterValue(6);
					}
					// 如果是haspermission，则取出是否有权限
					if ("hasPermission".equals(operType)) {
						jsonStr = (String) ppl.getParamterValue(5);
					}
				}
			}

		} catch (Exception e) {
			log.error("调用存储过程异常！");
		}
	
		return jsonStr;
	}
	/**
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param operType
	 * @param customObject
	 * @param procedureName
	 * @return
	 * @throws Exception
	 */
	private String getNodeByProcedure(String flowID, String nodeID, String userID,
			String loginUserID, String operType,Object customObject,String procedureName) throws Exception {
		String jsonStr = null;
		try {
			ProcedureParameterList ppl = new ProcedureParameterList();
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_flowID", flowID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_nodeID", nodeID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_userID", userID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_loginUserID",loginUserID);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_customObject",customObject);
			ppl.add(ProcedureParameterDirection.IN,ProcedureParameterDataType.STRING, "in_operType",operType);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_message", null);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_doOnClick",null);
			ppl.add(ProcedureParameterDirection.OUT,ProcedureParameterDataType.STRING, "out_getNodeStatus", null);
			int callReturn = dbUtil.executeProcedure(procedureName, ppl);
			String outMessage = null;
			if (callReturn > 0) {
				outMessage = (String) ppl.getParamterValue(6);
				if ("1".equals(outMessage)) {
					// 如果是init，则取出自定义对象值
					if ("getNodeStatus’".equals(operType)) {
						jsonStr = (String) ppl.getParamterValue(8);
					}
					// 如果是haspermission，则取出是否有权限
					if ("doOnClick".equals(operType)) {
						jsonStr = (String) ppl.getParamterValue(7);
					}
				}
			}

		} catch (Exception e) {
			log.error("调用存储过程异常！");
		}
	
		return jsonStr;
	}
	
	/**
	 * 记录操作日志
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param loginUserID
	 * @param operType
	 * @param operType
	 * @throws Exception
	 */
	private void recordOperLog(String flowID, String nodeID, String userID,
	String loginUserID, String operType, String nodeInfoStr) throws Exception {
		//记录操作日志
		String czsql = "insert into t_gggl_jddh_dhczrz values (?,?,?,?,?,?,?,sysdate)";
		String wid = dbUtil.getSysguid();
		String ipAddr = logUtil.getIpAddr(httpRequest);
		jdbcTemplate.update(czsql, new Object[] { wid,wid,flowID,nodeID,operType,loginUserID,ipAddr});
	}

}
