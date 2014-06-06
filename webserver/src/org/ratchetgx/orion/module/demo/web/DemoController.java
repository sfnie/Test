/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.module.demo.web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.birt.BirtEngine;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IProcedureParameterProcessor;
import org.ratchetgx.orion.common.util.OracleCallProcedureService;
import org.ratchetgx.orion.common.util.ProcedureParameter;
import org.ratchetgx.orion.common.util.ProcedureParameterDataType;
import org.ratchetgx.orion.common.util.ProcedureParameterDirection;
import org.ratchetgx.orion.common.util.ProcedureParameterList;
import org.ratchetgx.orion.module.common.service.BizobjService;
import org.ratchetgx.orion.module.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author hrfan
 */
@Controller
@RequestMapping(value = "demo")
public class DemoController {
	// 日志
	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DemoService demoService;
	@Autowired
	private BizobjService bizobjService;
	private JSONArray jsondata;
	@Autowired
	private BirtEngine birtEngine;

	@Autowired
	@Qualifier("oracleCallProcedureService")
	private OracleCallProcedureService callProcedureService;

	@RequestMapping(value = "index")
	public String index(final ModelMap model) {// modelmap存放数据，到view中就消失
		try {
			Map yjsjbxx = demoService.getYjsjbxx();// yjsjbxx研究生基本信息
			Object csrq = yjsjbxx.get("csrq");
			log.info(csrq.getClass() + "");
			log.info((csrq instanceof java.sql.Timestamp) + "");

			yjsjbxx.put("csrq", DbUtil.convertDateToString(csrq, "yyyy-MM-dd"));
			model.put("jbxx", yjsjbxx);

			Map jtcyjbxx = demoService.getJtcyjbxx();
			model.put("jtcy", jtcyjbxx);// jtcy家庭成员

		} catch (SQLException ex) {
			log.error("", ex);
		}
		return "demo/index";// 对应
	}

	@RequestMapping(value = "oneModule")
	public String oneModule(final ModelMap model) throws Exception {// modelmap存放数据，到view中就消失
		try {
			Map yjsjbxx = demoService.getYjsjbxx();// yjsjbxx研究生基本信息
			
			model.put("jbxx", yjsjbxx);

		} catch (SQLException ex) {
			log.error("", ex);
		}
		return "demo/oneModule";// 对应
	}

	@RequestMapping(value = "invokeProcedure")
	public String invokeProcedure() throws Exception {
		try {
			String procedureName = "P_TEST";
			List<ProcedureParameter> ppl = new ArrayList<ProcedureParameter>();
			ProcedureParameter pp = new ProcedureParameter(
					ProcedureParameterDirection.IN,
					ProcedureParameterDataType.STRING, "xh",
					SsfwUtil.getCurrentBh());
			ppl.add(pp);
			pp = new ProcedureParameter(ProcedureParameterDirection.IN,
					ProcedureParameterDataType.STRING, "ip",
					"localhost");
			ppl.add(pp);
			pp = new ProcedureParameter(ProcedureParameterDirection.OUT,
					ProcedureParameterDataType.FLOAT, "grade",
					null);
			ppl.add(pp);
			pp = new ProcedureParameter(ProcedureParameterDirection.OUT,
					ProcedureParameterDataType.RESULTSET,
					"students_cur", null);
			ppl.add(pp);
			callProcedureService.execProucedure(procedureName, ppl,
					new IProcedureParameterProcessor() {

						public void process(ProcedureParameterList ppl)
								throws SQLException {
							ProcedureParameter pp = ppl.getParameter(2);
							log.info("=============" + pp.getValue()
									+ "");
							pp = ppl.getParameter(3);
							log.info("=============" + pp.getValue()
									+ "");
							ResultSet rs = (ResultSet) pp.getValue();
							while (rs.next()) {
								log.info("===" + rs.getString("bh"));
							}
						}

						public void process1(ProcedureParameterList ppl) throws SQLException {
							// TODO Auto-generated method stub
							
						}
					});

		} catch (SQLException ex) {
			log.error("", ex);
		}

		return null;// 对应
	}

	/**
	 * @param kyxmform
	 * @return "/demo/kyxmform.do"
	 * @author : zjliu
	 * @date : 2013-02-26
	 */
	@RequestMapping(value = "kyxmform")
	public String kyxmform(final ModelMap model) {// modelmap存放数据，到view中就消失
		try {
			log.info("进入 kyxmxx Map");
			Map kyxmxx = demoService.getKyxmxx();// 获取科研项目基本信息
			log.debug("获取kyxmxx Map===" + kyxmxx.size());
			model.put("xmxx", kyxmxx);
			List xmcyList = demoService.getXmcy();
			model.put("xmcy", xmcyList);
		} catch (SQLException ex) {
			log.error("", ex);
		}
		return "demo/kyxmform";// 对应
	}

	/**
	 * @param kyxmgrid
	 * @return "/demo/kyxmform.do"
	 * @author : zjliu
	 * @date : 2013-02-26
	 */
	@RequestMapping(value = "kyxmgrid")
	public String kyxmgrid(final ModelMap model) {// modelmap存放数据，到view中就消失
		try {
			log.debug("come in kyxmgrid ");
			List kyxmgrid = demoService.getGridData();// 获取科研项目基本信息
			log.debug("kyxmgrid.size()===" + kyxmgrid.size());
			model.put("xmxx", kyxmgrid);
		} catch (SQLException ex) {
			log.error("", ex);
		}
		return "demo/kyxmgrid";// 对应
	}

	/**
	 * @param getMulitData
	 * @return "/demo/kyxmform.do"
	 * @author : zjliu
	 * @date : 2013-02-26
	 */
	@RequestMapping(method = RequestMethod.GET, value = "getMulitData")
	public JSONArray getMulitData(final HttpServletResponse response)
			throws JSONException, SQLException {// modelmap存放数据，到view中就消失
		try {
			log.debug("come in getMulitData ");
			// / List kydata= demoService.getGridData();//获取科研项目基本信息
			String jsonStr = "[{\"totalResultsCount\":100,\"geonames\":[{\"countryName\":\"Iran\",\"adminCode1\":\"23\",\"fclName\":\"mountain,hill,rock,... \",\"countryCode\":\"IR\",\"lng\":49.133333,\"fcodeName\":\"mountain\",\"toponymName\":\"Kūh-e Zardar\",\"fcl\":\"T\",\"name\":\"Kūh-e Zardar\",\"fcode\":\"MT\",\"geonameId\":1,\"lat\":32.983333,\"adminName1\":\"Lorestn\",\"population\":0}]}]";
			JSONArray jsondata = new JSONArray(jsonStr);
			log.debug("jsondata===" + jsondata.toString());
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write(jsondata.toString());

		} catch (Exception ex) {
			log.error("", ex);
		}
		return jsondata;// 对应
	}

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "kyxmsave")
	public String kyxmsave(HttpServletRequest httpRequest) {// modelmap存放数据，到view中就消失
		try {
			Map bizobjs = bizobjService.getBizobjs(httpRequest);
			List<Map<String, String>> xmxxList = bizobjService
					.getDatasOfBizobj(httpRequest, "xmxx");
			List<Map<String, String>> xmcyList = bizobjService
					.getDatasOfBizobj(httpRequest, "xmcy");
			demoService.savekyxm(xmxxList, xmcyList);
		} catch (Exception ex) {
			log.error("", ex);
			SsfwUtil.addMessage(ex.getMessage());
		}

		return "redirect:/demo/kyxmform.do";
	}

	/**
	 * @param oneModule2
	 * @return "demo/oneModule2.do"
	 * @author: zjliu
	 * @date : 2013-02-25
	 */
	@RequestMapping(value = "oneModule2")
	public String oneModule2(final ModelMap model) {// modelmap存放数据，到view中就消失
		try {
			Map yjsjbxx = demoService.getDemojbxx();// yjsjbxx研究生基本信息
			// bizobjService.convertValueToString(yjsjbxx,
			// "yyyy-MM-dd");//把里面所有的时间格式化//biz商业
			model.put("jbxx", yjsjbxx);

			List jyjlList = demoService.getJyjls();
			// bizobjService.convertValueToString(jyjlList, "qsrq",
			// "yyyy-MM-dd");
			// bizobjService.convertValueToString(jyjlList, "jsrq",
			// "yyyy-MM-dd");
			model.put("jyjls", jyjlList);

		} catch (SQLException ex) {
			log.error("", ex);
		}
		return "demo/oneModule2";// 对应
	}

	@RequestMapping(value = "oneModuleSave")
	public String oneModuleSave(HttpServletRequest httpRequest) {

		try {
			List<Map<String, String>> yjsjbxxList = bizobjService
					.getDatasOfBizobj(httpRequest, "jbxx");
			List<Map<String, String>> jtcyList = bizobjService
					.getDatasOfBizobj(httpRequest, "jtcy");

			demoService.save(yjsjbxxList, jtcyList);

			SsfwUtil.addMessage("保存成功");
		} catch (Exception ex) {
			log.error("", ex);
			SsfwUtil.addMessage("保存失败");
		}

		return "redirect:/demo/oneModule.do";
	}

	@RequestMapping(value = "oneModuleSave2")
	public String oneModuleSave2(HttpServletRequest httpRequest) {

		try {
			Map bizobjs = bizobjService.getBizobjs(httpRequest);
			log.info(bizobjs.toString());
			List<Map<String, String>> yjsjbxxList = bizobjService
					.getDatasOfBizobj(httpRequest, "jbxx");
			log.debug("yjsjbxxList=" + yjsjbxxList + ";" + yjsjbxxList.size());
			List<Map<String, String>> jyjlList = bizobjService
					.getDatasOfBizobj(httpRequest, "jyjl");
			log.debug("jyjlList=" + jyjlList + ";" + jyjlList.size());

			demoService.saveDemojbxx(yjsjbxxList, jyjlList);
		} catch (Exception ex) {
			log.error("", ex);
			SsfwUtil.addMessage(ex.getMessage());
		}

		return "redirect:/demo/oneModule2.do";
	}

	@RequestMapping(value = "save")
	public String save(HttpServletRequest httpRequest) {

		try {
			List<Map<String, String>> yjsjbxxList = bizobjService
					.getDatasOfBizobj(httpRequest, "jbxx");
			List<Map<String, String>> jyjlList = bizobjService
					.getDatasOfBizobj(httpRequest, "jtcy");

			demoService.save(yjsjbxxList, jyjlList);

			SsfwUtil.addMessage("保存成功");
		} catch (Exception ex) {
			log.error("", ex);
			SsfwUtil.addMessage("保存失败");
		}

		return "redirect:/demo/index.do";
	}

	@RequestMapping(value = "onetomany")
	public String onetomany(final ModelMap model) {

		try {
			Map yjsjbxx = demoService.getYjsjbxx();
			// bizobjService.convertValueToString(yjsjbxx, "yyyy-MM-dd");
			model.put("jbxx", yjsjbxx);

			List jyjlList = demoService.getJyjls();
			// bizobjService.convertValueToString(jyjlList, "qsrq",
			// "yyyy-MM-dd");
			// bizobjService.convertValueToString(jyjlList, "jsrq",
			// "yyyy-MM-dd");
			model.put("jyjls", jyjlList);

		} catch (SQLException ex) {
			log.error("", ex);
			SsfwUtil.addMessage(ex.getMessage());
		}

		return "demo/OneToMany";
	}

	@RequestMapping(value = "saveOneToMany")
	public String saveOneToMany(HttpServletRequest httpRequest) {

		try {
			Map bizobjs = bizobjService.getBizobjs(httpRequest);
			log.info(bizobjs.toString());
			List<Map<String, String>> yjsjbxxList = bizobjService
					.getDatasOfBizobj(httpRequest, "jbxx");
			log.debug("yjsjbxxList=" + yjsjbxxList + ";" + yjsjbxxList.size());
			List<Map<String, String>> jyjlList = bizobjService
					.getDatasOfBizobj(httpRequest, "jyjl");
			log.debug("jyjlList=" + jyjlList + ";" + jyjlList.size());

			demoService.saveOneToMany(yjsjbxxList, jyjlList);
		} catch (Exception ex) {
			log.error("", ex);
			SsfwUtil.addMessage(ex.getMessage());
		}

		return "redirect:/demo/onetomany.do";
	}

	@RequestMapping(value = "cascade")
	public String cascade() {
		return "demo/cascade";
	}

	@RequestMapping(value = "test")
	public String test() {
		try {
			demoService.test();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "demo/cascade";
	}

	@RequestMapping(value = "report")
	public String report(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {

		// get report name and launch the engine
		// httpResponse.setContentType("text/html;charset=UTF-8");
		httpResponse.setContentType("application/pdf");
		httpResponse.setHeader("Content-Disposition",
				"inline; filename=test.pdf");
		httpResponse.setHeader("success", "true");
		String reportName = httpRequest.getParameter("ReportName");
		ServletContext sc = httpRequest.getSession().getServletContext();
		IReportEngine reportEngine = birtEngine.getBirtEngine();

		IReportRunnable design;
		try {
			// Open report design
			design = reportEngine.openReportDesign(sc.getRealPath("/reports")
					+ "/" + reportName);
			// create task to run and render report
			IRunAndRenderTask task = reportEngine
					.createRunAndRenderTask(design);
			task.setParameterValue("jxbh", "111");
			task.setParameterValue("kcmc", "111");
			task.setParameterValue("jsmc", "111");
			task.setParameterValue("xsrs", new Integer(20));
			task.setParameterValue("dqxq", "111");
			// set output options
			HTMLRenderOption options = new HTMLRenderOption();
			// set the image handler to a HTMLServerImageHandler if you plan on
			// using the base image url.
			options.setImageHandler(new HTMLServerImageHandler());
			// options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
			options.setOutputStream(httpResponse.getOutputStream());
			options.setBaseImageURL(httpRequest.getContextPath() + "/images");
			options.setImageDirectory(sc.getRealPath("/images"));
			task.setRenderOption(options);

			// run report
			task.run();
			task.close();
		} catch (Exception e) {

			log.error("", e);
		}

		return null;
	}
	
	@RequestMapping(value = "anonymous")
	public String anonymous(){
		return "demo/anonymous";
	}

}
