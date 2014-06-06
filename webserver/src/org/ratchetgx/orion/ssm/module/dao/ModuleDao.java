package org.ratchetgx.orion.ssm.module.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class ModuleDao {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> list(final String parentModuleWid) {
        String sql = "select WID,NAME,MEMO,PARENT_WID from SS_MODULE where PARENT_WID = ?";

        if ("000000".equals(parentModuleWid) || parentModuleWid == null) {
            sql += " or PARENT_WID is null";
        }

        log.info(sql);

        List<Map<String, String>> modules = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, parentModuleWid);

            }
        }, new ResultSetExtractor() {
            public List<Map<String, String>> extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> modules = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();

                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    map.put("memo", rs.getString("memo"));
                    map.put("parent_wid", rs.getString("parent_wid"));

                    modules.add(map);
                }

                return modules;
            }
        });

        return modules;
    }

    public void delete(final String moduleWid) {
        String sql = "delete from SS_MODULE where WID = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, moduleWid);
                return pstmt.executeUpdate();

            }
        });
    }

    public void add(final String name, final String memo,
            final String parentModuleWid) {
        String sql = "insert into SS_MODULE(WID,NAME,MEMO,PARENT_WID) values(sys_guid(),?,?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, memo);
                pstmt.setString(3, parentModuleWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void edit(final String moduleWid, final String name,
            final String memo) {
        String sql = "update SS_MODULE set NAME=?,MEMO=? where WID = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, memo);
                pstmt.setString(3, moduleWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public Map<String, String> load(final String moduleWid) {
        String sql = "select WID,NAME,MEMO,PARENT_WID from SS_MODULE where WID = ?";

        Map<String, String> module = (Map<String, String>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, moduleWid);
            }
        }, new ResultSetExtractor() {
            public Map<String, String> extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> module = new HashMap<String, String>();

                if (rs.next()) {
                    module.put("wid", rs.getString("wid"));
                    module.put("name", rs.getString("name"));
                    module.put("memo", rs.getString("memo"));
                    module.put("parent_id", rs.getString("parent_wid"));
                }

                return module;
            }
        });

        return module;
    }
}