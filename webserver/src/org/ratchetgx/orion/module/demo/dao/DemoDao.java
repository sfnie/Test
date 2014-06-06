/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.module.demo.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.util.AndOrEnum;
import org.ratchetgx.orion.common.util.BizobjUtil;
import org.ratchetgx.orion.common.util.Condition;
import org.ratchetgx.orion.common.util.ConditionGroup;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedStatementProcessor;
import org.ratchetgx.orion.common.util.RelOperEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author hrfan
 */
@Repository
public class DemoDao {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BizobjUtil bizobjUtil;
	@Autowired
	private DbUtil dbUtil;

	public Map getYjsjbxx() throws SQLException {
		
//		bizobjUtil.getColumnTypes("T_XJGL_XJXX_YJSJBXX");

		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);// 只是为了取个and值
		// “xh”列名，RelOperEnum.EQUAL比较运算符，SsfwUtil.getCurrentBh获取编号信息
		cg.addCondition(new Condition("xh", RelOperEnum.EQUAL, SsfwUtil
				.getCurrentBh()));
		Map yjsjbxx = null;
		List<Map> yjsjbxxList = bizobjUtil.query("T_XJGL_XJXX_YJSJBXX", cg,
				null);
		if (yjsjbxxList.size() > 0) {
			yjsjbxx = yjsjbxxList.get(0);
		} else {
			yjsjbxx = new HashMap();
		}
//		return bizobjUtil.convertTimeToString(yjsjbxx, "yyyy-MM-dd");
		return yjsjbxx;
	}

	public Map getJtcyjbxx() throws SQLException {
		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);
		cg.addCondition(new Condition("xh", RelOperEnum.EQUAL, SsfwUtil
				.getCurrentBh()));
		List<Map> jtcyList = bizobjUtil.query("T_XJGL_XJXX_JTCYJBXX", cg, null);
		if (jtcyList.size() > 0) {
			return jtcyList.get(0);
		} else {
			return new HashMap();
		}
	}

	public Map getDemojbxx() throws SQLException {

		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);// 只是为了取个and值
		// “xh”列名，RelOperEnum.EQUAL比较运算符，SsfwUtil.getCurrentBh获取编号信息
		cg.addCondition(new Condition("xh", RelOperEnum.EQUAL, SsfwUtil
				.getCurrentBh()));
		Map yjsjbxx = null;
		List<Map> yjsjbxxList;
		yjsjbxxList = bizobjUtil.query("T_DEMO_RYJBXX", cg, null);
		if (yjsjbxxList.size() > 0) {
			yjsjbxx = yjsjbxxList.get(0);
		} else {
			yjsjbxx = new HashMap();
		}
		return yjsjbxx;
//		return bizobjUtil.convertTimeToString(yjsjbxx, "yyyy-MM-dd");
	}

	public void saveDemojbxx(Map data) throws SQLException {
		bizobjUtil.save("T_DEMO_RYJBXX", data);
	}

	public void saveYjsjbxx(Map data) throws SQLException {
		Map pattern = new HashMap();
		bizobjUtil.save("T_XJGL_XJXX_YJSJBXX", data,pattern);
	}

	public void saveJtcyjbxx(Map data) throws SQLException {
		bizobjUtil.save("T_XJGL_XJXX_JTCYJBXX", data);
	}

	public void saveJyjl(Map data) throws SQLException {
		bizobjUtil.save("T_XJGL_XJXX_JYJL", data);
	}

	public void deleteAllJyjls() throws SQLException {
		ConditionGroup cg = new ConditionGroup();
		cg.addCondition(new Condition("xh", RelOperEnum.EQUAL, SsfwUtil
				.getCurrentBh()));
		bizobjUtil.delete("T_XJGL_XJXX_JYJL", cg);
	}

	public List<Map> getJyjls() throws SQLException {
		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);
		cg.addCondition(new Condition("xh", RelOperEnum.EQUAL, SsfwUtil
				.getCurrentBh()));
		List<Map> jyjlList = bizobjUtil.query("T_XJGL_XJXX_JYJL", cg, null);
		return jyjlList;
	}

	public Map getKyxmxx() throws SQLException {
		test() ;
		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);// 只是为了取个and值
		cg.addCondition(new Condition("xmxh", RelOperEnum.EQUAL, "24234234"));
		Map xmxx = null;
		List<Map> xmxxList;
		xmxxList = bizobjUtil.query("T_XM_JBXX", cg, null);
		if (xmxxList.size() > 0) {
			xmxx = xmxxList.get(0);
		} else {
			xmxx = new HashMap();
		}
		///deleteXmcy();
		
		return xmxx;
//		return bizobjUtil.convertTimeToString(xmxx, "yyyy-MM-dd");

	}

	public List getXmcy() throws SQLException {
		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);
		cg.addCondition(new Condition("xmxh", RelOperEnum.EQUAL, "24234234"));
		List jyjlList = bizobjUtil.query("T_XM_XMCY", cg, null);
		
		return jyjlList;
	}

	public void deleteXmcy() throws SQLException {
		ConditionGroup cg = new ConditionGroup();
		cg.addCondition(new Condition("xmxh", RelOperEnum.EQUAL, "24234234"));
		bizobjUtil.delete("T_XM_XMCY", cg);
	}

	public void saveXmxx(Map<String, String> xmxx) throws SQLException {
		bizobjUtil.save("T_XM_JBXX", xmxx);
	}

	public void saveXmcy(Map<String, String> xmcy) throws SQLException {
		bizobjUtil.save("T_XM_XMCY", xmcy);
	}

	public List getGridData() throws SQLException {
		ConditionGroup cg = new ConditionGroup(AndOrEnum.AND);
		List xmxxList = bizobjUtil.query("T_XM_JBXX", null, null);
		return xmxxList;
	}

	public void test() throws SQLException {
		String bizobj = "t_xjgl_xjxx_yjsjbxx";
		// String conditionString = "xh=:xh";
		// Map paramMap = new HashMap();
		// paramMap.put("xh", "1130372160");
		// bizobjUtil.delete(bizobj, conditionString, paramMap);

//		ConditionGroup conditionGroup = new ConditionGroup();
//		conditionGroup.addCondition(new Condition("xh", RelOperEnum.EQUAL,
//				"1130372160"));
//		bizobjUtil.delete(bizobj, conditionGroup);
		
		dbUtil.execute("delete from t_xjgl_xjxx_yjsjbxx where xh = ?", new IPreparedStatementProcessor() {
			
			public void process(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				pstmt.setString(1, "1130372160");
				pstmt.executeUpdate();
			}
		});

	}
}
