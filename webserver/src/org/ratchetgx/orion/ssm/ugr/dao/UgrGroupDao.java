package org.ratchetgx.orion.ssm.ugr.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class UgrGroupDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> listGroups() {
        String sql = "SELECT wid,name FROM ss_group";
        List<Map<String, String>> groups = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    groups.add(map);
                }
                return groups;
            }
        });
        return groups;
    }

    public void addGroup(final String groupName) {
        String sql = "INSERT INTO ss_group(wid,name,memo) values(sys_guid(),?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, groupName);
                pstmt.setString(2, "");

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteGroup(final String groupWid) {
        String sql = "DELETE FROM ss_group WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, groupWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteGroups(final List<String> groupWids) {
        String sql = "DELETE FROM ss_group WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                Iterator<String> groupWidItr = groupWids.iterator();
                while (groupWidItr.hasNext()) {
                    String groupWid = groupWidItr.next();
                    pstmt.setString(1, groupWid);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        });
    }
}
