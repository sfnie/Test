/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.selectrange.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.common.util.IResultSetProcessor;
import org.ratchetgx.orion.widgets.selectrange.ComboboxDefine;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hrfan
 */
public class Xslb2 implements ComboboxDefine {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private Object v;
    private String cascade;
    private DbUtil dbUtil;

    
    public void setCascadeValue(Object v) {
        this.v = v;
    }

    
    public void setCascade(String cascade) {
        this.cascade = cascade;
    }

    
    public List<String> getSupportedCascades() {
        List<String> supportedCascades = new ArrayList<String>();
        supportedCascades.add("pyccm");
        return supportedCascades;
    }

    
    public void setDbUtil(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    
    public Map getData() {
        final Map data = new HashMap();
        try {
            //由cascade是否有值判断是否是级联取值
            if (cascade == null) {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT xslbdm,xslbmc FROM ");
                sql.append("t_xb_xslb");
                dbUtil.execute(sql.toString(), new IResultSetProcessor() {
                    
                    public void process(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            data.put(rs.getString(1), rs.getString(2));
                        }
                    }
                });
            } else {
                //支持的级联属性
                if (getSupportedCascades().contains(cascade)) {
                    StringBuilder sql = new StringBuilder();
                    sql.append("SELECT xslbdm,xslbmc FROM ");
                    sql.append("t_xb_xslb");
                    sql.append(" WHERE ");
                    sql.append(cascade);
                    sql.append(" = ?");
                    dbUtil.execute(sql.toString(), new IPreparedResultSetProcessor() {
                        
                        public void processPreparedStatement(PreparedStatement pstmt) throws SQLException {
                            pstmt.setObject(1, v, Types.VARCHAR);
                        }

                        
                        public void processResultSet(ResultSet rs) throws SQLException {
                            while (rs.next()) {
                                data.put(rs.getString(1), rs.getString(2));
                            }
                        }
                    });
                }
            }
        } catch (Exception ex) {
            log.error("", ex);
        }

        return data;
    }

    
    public Map<Object, String> getVLs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
