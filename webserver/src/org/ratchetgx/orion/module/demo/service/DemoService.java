/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.module.demo.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.module.demo.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author hrfan
 */
@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;

    public Map getYjsjbxx() throws SQLException {
        return demoDao.getYjsjbxx();
    }
    
     public Map getDemojbxx() throws SQLException {
        return demoDao.getDemojbxx();
    }

    public Map getJtcyjbxx() throws SQLException {
        return demoDao.getJtcyjbxx();
    }

    public List<Map> getJyjls() throws SQLException {
        return demoDao.getJyjls();
    }
    
    public Map getKyxmxx() throws SQLException{
        return demoDao.getKyxmxx();
    }
    
    public List getXmcy() throws SQLException{
        return demoDao.getXmcy();
    }
    
    

    @Transactional
    public void save(List<Map<String, String>> yjsjbxxList, List<Map<String, String>> jtcyList) throws SQLException {
        for (int i = 0; i < yjsjbxxList.size(); i++) {
            Map<String, String> yjsjbxx = yjsjbxxList.get(i);
            demoDao.saveYjsjbxx(yjsjbxx);
        }

        for (int i = 0; i < jtcyList.size(); i++) {
            Map<String, String> jtcy = jtcyList.get(i);
            demoDao.saveJtcyjbxx(jtcy);
        }
    }
    
      @Transactional
    public void saveDemojbxx(List<Map<String, String>> yjsjbxxList, List<Map<String, String>> jyjlList) throws SQLException {
        for (int i = 0; i < yjsjbxxList.size(); i++) {
            Map<String, String> yjsjbxx = yjsjbxxList.get(i);
            demoDao.saveDemojbxx(yjsjbxx);  // 保存主表基本信息
        }

         demoDao.deleteAllJyjls(); // 删除从表信息

        for (int i = 0; i < jyjlList.size(); i++) {
            Map<String, String> jyjl = jyjlList.get(i);
            jyjl.put("xh", SsfwUtil.getCurrentBh());
            demoDao.saveJyjl(jyjl); // 保存从表信息
        }
    }

    @Transactional
    public void saveOneToMany(List<Map<String, String>> yjsjbxxList, List<Map<String, String>> jyjlList) throws SQLException {

        for (int i = 0; i < yjsjbxxList.size(); i++) {
            Map<String, String> yjsjbxx = yjsjbxxList.get(i);
            demoDao.saveYjsjbxx(yjsjbxx);
        }
//        demoDao.deleteAllJyjls();
        for (int i = 0; i < jyjlList.size(); i++) {
            Map<String, String> jyjl = jyjlList.get(i);
            ///jyjl.put("xh", SsfwUtil.getCurrentBh());
            demoDao.saveJyjl(jyjl);
        }
    }
    @Transactional
    public void savekyxm(List<Map<String, String>> xmxxList, List<Map<String, String>> xmcyList) throws SQLException {
        
          for (int i = 0; i < xmxxList.size(); i++) {
            Map<String, String> xmxx = xmxxList.get(i);
            demoDao.saveXmxx(xmxx);
        }

        for (int i = 0; i < xmcyList.size(); i++) {
            Map<String, String> xmcy = xmcyList.get(i);
            demoDao.saveXmcy(xmcy);
        }
    }

    public List getGridData() throws SQLException {
         return demoDao.getGridData();
    }
    
    @Transactional
    public void test() throws SQLException{
    	demoDao.test();
    }
}
