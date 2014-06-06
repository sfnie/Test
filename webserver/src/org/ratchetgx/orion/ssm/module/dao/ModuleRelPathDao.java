package org.ratchetgx.orion.ssm.module.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class ModuleRelPathDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> list(final String module_id) {
        String sql = "select WID,PATH,MEMO,MODULE_WID,INDEXED from SS_MODULEPATH where MODULE_WID = ? order by INDEXED";
        List<Map<String, String>> objs = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, module_id);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> objs = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> obj = new HashMap<String, String>();
                    obj.put("wid", rs.getString("wid"));
                    obj.put("path", rs.getString("path"));
                    obj.put("memo", rs.getString("memo"));
                    obj.put("module_id", rs.getString("module_Wid"));
                    obj.put("indexed", rs.getString("indexed"));

                    objs.add(obj);
                }
                return objs;
            }
        });

        return objs;
    }

    public void add(final String module_id, final String path,
            final String indexed, final String memo) {
        String sql = "insert into SS_MODULEPATH(WID,MODULE_WID,PATH,INDEXED,MEMO) values(sys_guid(),?,?,?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, module_id);
                pstmt.setString(2, path);
                pstmt.setString(3, indexed);
                pstmt.setString(4, memo);

                return pstmt.executeUpdate();
            }
        });
    }

    public void delete(final List<String> modulePathWids) {
        String sql = "delete from SS_MODULEPATH where WID = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                for (int i = 0; i < modulePathWids.size(); i++) {
                    pstmt.setString(1, modulePathWids.get(i));
                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });
    }

    public Map<String, String> load(final String moduleWid) {
        String sql = "select WID,PATH,MEMO,MODULE_WID,INDEXED from SS_MODULEPATH where WID = ?";

        return (Map<String, String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, moduleWid);

                    }
                }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> map = new HashMap<String, String>();

                if (rs.next()) {
                    map.put("wid", rs.getString("wid"));
                    map.put("path", rs.getString("path"));
                    map.put("memo", rs.getString("memo"));
                    map.put("module_id", rs.getString("module_id"));
                    map.put("indexed", rs.getString("indexed"));
                }

                return map;
            }
        });
    }

    public void setAsDefault(final String moduleWid, final String pathWid) {
        String sql_0 = "UPDATE ss_modulepath SET indexed = 1 WHERE module_wid = ? AND indexed = 0";
        String sql_1 = "UPDATE ss_modulepath SET indexed = 0 WHERE wid = ?";
        jdbcTemplate.execute(sql_0, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, moduleWid);

                return pstmt.executeUpdate();
            }
        });

        jdbcTemplate.execute(sql_1, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, pathWid);

                return pstmt.executeUpdate();
            }
        });
    }
}
