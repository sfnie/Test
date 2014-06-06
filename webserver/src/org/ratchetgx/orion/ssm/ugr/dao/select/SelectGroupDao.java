package org.ratchetgx.orion.ssm.ugr.dao.select;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class SelectGroupDao {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * role相关的用户组
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listSelectedOfRole(final String role) {
        String sql = "SELECT g.wid,g.name FROM ss_group_rel_role r LEFT JOIN ss_group g ON r.group_wid=g.wid WHERE r.role = ?";
        List<Map<String, String>> groups = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, role);
            }
        }, new ResultSetExtractor() {
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

    /**
     * role不相关的用户组
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listUnSelectedOfRole(final String role) {
        String sql = "SELECT g.wid,g.name FROM ss_group g WHERE not exists(select * from ss_group_rel_role r where g.wid = r.group_wid AND r.role=?)";
        List<Map<String, String>> groups = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, role);
            }
        }, new ResultSetExtractor() {
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

    public List<Map<String, String>> listSelectedOfUser(final String userWid) {
        String sql = "SELECT g.wid,g.name FROM ss_group_rel_user r LEFT JOIN ss_group g ON r.group_wid=g.wid WHERE r.user_wid = ?";
        List<Map<String, String>> groups = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, userWid);
            }
        }, new ResultSetExtractor() {
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

    public List<Map<String, String>> listUnSelectedOfUser(final String userWid) {
        String sql = "SELECT g.wid,g.name FROM ss_group g WHERE not exists(select * from ss_group_rel_user r where g.wid = r.group_wid AND r.user_wid=?)";
        List<Map<String, String>> groups = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, userWid);
            }
        }, new ResultSetExtractor() {
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

    public void cancelOfRole(final String role, final List<String> groupWids) {
        String sql = "DELETE FROM ss_group_rel_role WHERE role=? AND group_wid=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> groupWidItr = groupWids.iterator();
                while (groupWidItr.hasNext()) {
                    String groupWid = groupWidItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfRole(final String role, final List<String> groupWids) {
        String sql = "INSERT INTO ss_group_rel_role(role,group_wid) values(?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> groupWidItr = groupWids.iterator();
                while (groupWidItr.hasNext()) {
                    String groupWid = groupWidItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void cancelOfUser(final String userWid, final List<String> groupWids) {
        String sql = "DELETE FROM ss_group_rel_user WHERE user_wid=? AND group_wid=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> groupWidItr = groupWids.iterator();
                while (groupWidItr.hasNext()) {
                    String groupWid = groupWidItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfUser(final String userWid, final List<String> groupWids) {
        String sql = "INSERT INTO ss_group_rel_user(user_wid,group_wid) VALUES(?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> groupWidItr = groupWids.iterator();
                while (groupWidItr.hasNext()) {
                    String groupWid = groupWidItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }
}
