/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.selectrange.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.common.util.IResultSetProcessor;
import org.ratchetgx.orion.widgets.selectrange.CombotreeDefine;
import org.slf4j.LoggerFactory;

/**
 * 省市学校信息
 *
 * @author hrfan
 */
public class Ssxxxx implements CombotreeDefine {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private Object parentValue;
    private String parentDefine;
    private Object cascadeValue;
    private String cascade;
    private DbUtil dbUtil;

    public void setParentValue(Object v) {
        this.parentValue = v;
    }

    public void setParentDefine(String parentDefine) {
        this.parentDefine = parentDefine;
    }

    public List<String> getSupportedParentDefines() {
        List<String> supportedParentDefines = new ArrayList<String>();
        supportedParentDefines.add("szss");
        return supportedParentDefines;
    }

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

    /**
     * 此选择范围不支持级联情况
     *
     * @return
     */
    public List<Map<String, Object>> getData() {
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        try {
            //parentValue为空，则查询院系信息，否则查询导师信息
            if (parentValue == null || "".equals(parentValue.toString().trim())) {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT distinct(szssm),szss FROM t_byxw_xwsb_gdyxdw dw ");
                dbUtil.execute(sql.toString(), new IResultSetProcessor() {
                    public void process(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            Map m = new HashMap();
                            m.put("value", rs.getObject(1));
                            m.put("label", rs.getString(2));
                            m.put("fullLabel", rs.getString(2));
                            m.put("left", false);
                            data.add(m);
                        }
                    }
                });

                Map m = new HashMap();
                m.put("value", "00000");
                m.put("label", "其他");
                m.put("fullLabel", "其他");
                m.put("left", true);
                data.add(m);
            } else {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT dwdm,dwmc FROM t_byxw_xwsb_gdyxdw WHERE szssm = ?");
                dbUtil.execute(sql.toString(), new IPreparedResultSetProcessor() {
                    public void processPreparedStatement(PreparedStatement pstmt) throws SQLException {
                        pstmt.setObject(1, parentValue);
                    }

                    public void processResultSet(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            Map m = new HashMap();
                            m.put("value", rs.getObject(1));
                            m.put("label", rs.getString(2));
                            m.put("fullLabel", rs.getString(2));
                            m.put("left", true);
                            data.add(m);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        return data;
    }

    public Map<Object, String> getVFLs() {
        final Map<Object, String> vfls = new HashMap<Object, String>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT dwdm,dwmc FROM t_byxw_xwsb_gdyxdw");
        try {
            dbUtil.execute(sql.toString(), new IResultSetProcessor() {
                public void process(ResultSet rs) throws SQLException {
                    while (rs.next()) {
                        vfls.put(rs.getObject(1), rs.getString(2));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("", ex);
        }
        return vfls;
    }
}
