package org.ratchetgx.orion.common.logger;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ratchetgx.orion.common.util.DbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

public class AsyncAnalysisData {
	@Autowired
	HttpServletRequest request;
	@Autowired
	private LogUtil logUtil;
	@Autowired
	private DbUtil dbUtil;
	
	@Async
	public void logAnalyData(final String optType,final String bizobj,HashMap<String,String>requestMap,HashMap<String,String> datamap,String currentbh) throws SQLException, InterruptedException{
		//首先记录T_SYS_LOG日志主表
		final HashMap<String,Object> asymap=new HashMap<String,Object>();
		asymap.put("wid", dbUtil.getSysguid());
		asymap.put("userid", currentbh);
		asymap.put("username", requestMap.get("user"));
		asymap.put("actip", requestMap.get("addr"));
		asymap.put("acttime",new Date());
		if(optType!=null && (optType==LogConstants.OPT_NEW||optType==LogConstants.OPT_UPDATE)){
			asymap.put("acttype",optType==LogConstants.OPT_NEW?"3":"4"); //系统更新类型3-添加  4-更新 
			asymap.put("acturl", optType==LogConstants.OPT_NEW?"新增数据":"修改数据");
		}else if(optType!=null && optType==LogConstants.OPT_DELETE){
			asymap.put("acttype","5"); //5-删除
			asymap.put("acturl", "删除数据");
		}
		asymap.put("actid",datamap.get("wid")!=null ? datamap.get("wid").toString():"");
		asymap.put("acttable",bizobj);
		logUtil.addLog(asymap,datamap);
		
	}
	
	@Async
	public void logDelinfo(final String actSql,final String optType,final String bizobj,Map<String,String>requestMap,Map<String,String> datamap,String currentbh) throws SQLException{
		//首先记录T_SYS_LOG日志主表
		final HashMap<String,Object> asymap=new HashMap<String,Object>();
		asymap.put("wid", dbUtil.getSysguid());
		asymap.put("userid", currentbh);
		asymap.put("username", requestMap.get("user"));
		asymap.put("actip", requestMap.get("addr"));
		asymap.put("acttime",new Date());
		if(optType!=null && (optType==LogConstants.OPT_NEW||optType==LogConstants.OPT_UPDATE)){
			asymap.put("acttype",optType==LogConstants.OPT_NEW?"3":"4"); //系统更新类型3-添加  4-更新 
			asymap.put("acturl", optType==LogConstants.OPT_NEW?"新增数据":"修改数据");
		}else if(optType!=null && optType==LogConstants.OPT_DELETE){
			asymap.put("acttype","5"); //5-删除
			asymap.put("acturl", "删除数据 : "+actSql);
		}
		asymap.put("actid",datamap.get("wid")!=null ? datamap.get("wid").toString():"");
		asymap.put("acttable",bizobj);
		logUtil.addLog(asymap,datamap);
		
		
		
	}
	
	@Async
	public void logOptSqlInfo(final String actSql,HashMap<String,String>requestMap,String currentbh) throws SQLException{
		//首先记录T_SYS_LOG日志主表
		final HashMap<String,Object> asymap=new HashMap<String,Object>();
		asymap.put("wid", dbUtil.getSysguid());
		asymap.put("userid", currentbh);
		asymap.put("username", requestMap.get("user"));
		asymap.put("actip", requestMap.get("addr"));
		asymap.put("acttime",new Date());
		asymap.put("acturl", "直接调用DbUtil.execute() SQL : "+actSql);

		asymap.put("actid","");
		asymap.put("acttable","");
		logUtil.addSqlLog(asymap);
		
		
		
	}
	
}
