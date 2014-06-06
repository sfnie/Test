package org.ratchetgx.orion.widgets.progressIndicator.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.widgets.progressIndicator.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 进度导航控制类
 *
 */
@Controller
@RequestMapping(value = "/progerssIndicator")
public class ProgressIndicatorController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public Engine engine;

	/**
	 * 初始化流程
	 * @param flowID
	 * @param userID
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "init")
	public void initFlow(@RequestParam("flowID") String flowID,
				         @RequestParam("userID") String userID,
				         HttpServletRequest request,
				         HttpServletResponse response) throws Exception {
		
		
		log.debug("----------------engine init start---------------------");
		String loginuserid = SsfwUtil.getCurrentBh();
		//Engine engine = Engine.getInstance();
		engine.setCacheContext(request);
		String jsonStr = engine.initFlow(flowID,userID,loginuserid);
		log.debug("----------------engine init :" + jsonStr);
		JSONObject rev = new JSONObject();
		rev.put("jsonStr",jsonStr);
		response.setContentType("application/json;charset=UTF-8");
	    response.getWriter().print(rev.toString());

	}
	
	/**
	 * 获取流程节点在UI界面显示时所需要的数据
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "getNodeStatus")
	public void getNodeStatus(@RequestParam("flowID") String flowID,
            				  @RequestParam("nodeID") String nodeID,
            				  @RequestParam("userID") String userID,
            				  HttpServletRequest request,
            				  HttpServletResponse response) throws Exception {
		log.debug("----------------engine getNodeStatus start---------------------");
		//Engine engine = Engine.getInstance();
		engine.setCacheContext(request);
		//String loginUserID = request.getSession().getAttribute("loginUserID").toString();
		String loginUserID = SsfwUtil.getCurrentBh();
		Object customObject = new Object();
		String jsonStr = engine.getNodeStatus(flowID, nodeID, userID, loginUserID, customObject);
		log.debug("----------------engine getNodeStatus :" + jsonStr);
		JSONObject rev = new JSONObject();
		rev.put("jsonStr",jsonStr);
		response.setContentType("application/json;charset=UTF-8");
	    response.getWriter().print(rev.toString());
	}


	/**
	 * 用户点击流程节点时的处理
	 * @param flowID
	 * @param nodeID
	 * @param userID
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "doOnClickNode")
	public void doOnClickNode(@RequestParam("flowID") String flowID,
            				  @RequestParam("nodeID") String nodeID,
            				  @RequestParam("userID") String userID,
            				  HttpServletRequest request,
            				  HttpServletResponse response) throws Exception {
		log.debug("----------------engine doOnClickNode start---------------------");
		//
		//Engine engine = Engine.getInstance();
		engine.setCacheContext(request);
		//String loginUserID = request.getSession().getAttribute("loginUserID").toString();
		String loginUserID = SsfwUtil.getCurrentBh();
		Object customObject = new Object();
		String jsonStr = engine.doOnClickNode(flowID, nodeID, userID, loginUserID, customObject);
		log.debug("----------------engine doOnClickNode :" + jsonStr);
		JSONObject rev = new JSONObject();
		rev.put("jsonStr",jsonStr);
		response.setContentType("application/json;charset=UTF-8");
	    response.getWriter().print(rev.toString());
	}

}
