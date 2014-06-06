/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.selectrange.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IResultSetProcessor;
import org.ratchetgx.orion.widgets.selectrange.ComboboxDefine;
import org.slf4j.LoggerFactory;

/**
 * 异动大类
 *
 * @author hrfan
 */
public class Yddl implements ComboboxDefine {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private Object cascadeValue;
    private String cascade;
    private DbUtil dbUtil;

    
    public void setCascadeValue(Object v) {
        this.cascadeValue = v;
    }

    
    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    
    public List<String> getSupportedCascades() {
        List<String> supportedCascades = new ArrayList<String>();
        return supportedCascades;
    }

    
    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    
    public Map getData() {
        final Map data = new HashMap();
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT yddldm, yddlmc" + " FROM t_xjgl_xjyd_yddl yddl")
                    .append(" WHERE EXISTS (select 1")
                    .append(" from (SELECT yddldm, ydlbdm, ydlbmc")
                    .append(" FROM t_xjgl_xjyd_ydlb")
                    .append(" WHERE substr(nvl(czlc, '000'), 1, 1) = '1') temp")
                    .append(" where yddl.yddldm = temp.yddldm)").append("order by yddldm");
            dbUtil.execute(sql.toString(), new IResultSetProcessor() {
                
                public void process(ResultSet rs) throws SQLException {
                    while (rs.next()) {
                        data.put(rs.getString(1), rs.getString(2));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("", ex);
        }

        return data;
    }

    
    public Map<Object, String> getVLs() {
        final Map<Object, String> vls = new HashMap<Object, String>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT yddldm, yddlmc" + " FROM t_xjgl_xjyd_yddl yddl")
                .append(" WHERE EXISTS (select 1")
                .append(" from (SELECT yddldm, ydlbdm, ydlbmc")
                .append(" FROM t_xjgl_xjyd_ydlb")
                .append(" WHERE substr(nvl(czlc, '000'), 1, 1) = '1') temp")
                .append(" where yddl.yddldm = temp.yddldm)").append("order by yddldm");
        try {
            dbUtil.execute(sql.toString(), new IResultSetProcessor() {
                
                public void process(ResultSet rs) throws SQLException {
                    while (rs.next()) {
                        vls.put(rs.getObject(1), rs.getString(2));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("", ex);
        }
        return vls;
    }
}
