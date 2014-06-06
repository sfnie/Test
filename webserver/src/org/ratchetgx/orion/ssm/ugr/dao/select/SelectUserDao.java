package org.ratchetgx.orion.ssm.ugr.dao.select;

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
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class SelectUserDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * groupWid相关的用户
     *
     * @param user
     * @return
     */
    public List<Map<String, String>> listSelectedOfGroup(final String groupWid) {
        String sql = "SELECT u.wid,u.name,u.email FROM ss_user u LEFT JOIN ss_group_rel_user r ON u.wid=r.user_wid WHERE r.group_wid = ?";
        List<Map<String, String>> users = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, groupWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> users = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    map.put("email", rs.getString("email"));
                    users.add(map);
                }
                return users;
            }
        });
        return users;
    }

    /**
     * groupWid不相关的用户
     *
     * @param user
     * @return
     */
    public List<Map<String, String>> listUnSelectedOfGroup(final String groupWid) {
        String sql = "SELECT u.wid,u.name,u.email FROM ss_user u WHERE not exists(select * from ss_group_rel_user r where u.wid = r.user_wid AND r.group_wid=?)";
        List<Map<String, String>> users = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, groupWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> users = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    map.put("email", rs.getString("email"));
                    users.add(map);
                }
                return users;
            }
        });
        return users;
    }

    /**
     * role相关的用户
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listSelectedOfRole(final String role) {
        String sql = "SELECT u.wid,u.name,u.email FROM ss_user u LEFT JOIN ss_user_rel_role r ON r.user_wid=u.wid WHERE r.role = ?";
        List<Map<String, String>> users = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, role);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> users = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    map.put("email", rs.getString("email"));
                    users.add(map);
                }
                return users;
            }
        });
        return users;

    }

    /**
     * role不相关的用户
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listUnSelectedOfRole(final String role) {
        String sql = "SELECT u.wid,u.name,u.email FROM ss_user u WHERE not exists(SELECT * FROM ss_user_rel_role r WHERE u.wid = r.user_wid AND r.role=?)";
        List<Map<String, String>> users = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, role);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> users = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    map.put("email", rs.getString("email"));
                    users.add(map);
                }
                return users;
            }
        });
        return users;
    }

    public void cancelOfRole(final String role, final List<String> userWids) {
        String sql = "DELETE FROM ss_user_rel_role WHERE role=? AND user_wid=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> userWidItr = userWids.iterator();
                while (userWidItr.hasNext()) {
                    String userWid = userWidItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, userWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfRole(final String role, final List<String> userWids) {
        String sql = "INSERT INTO ss_user_rel_role(role,user_wid) values(?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> userWidItr = userWids.iterator();
                while (userWidItr.hasNext()) {
                    String userWid = userWidItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, userWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void cancelOfGroup(final String groupWid, final List<String> userWids) {
        String sql = "DELETE FROM ss_group_rel_user WHERE user_wid=? AND group_wid=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> userWidItr = userWids.iterator();
                while (userWidItr.hasNext()) {
                    String userWid = userWidItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfGroup(final String groupWid, final List<String> userWids) {
        String sql = "INSERT INTO ss_group_rel_user(user_wid,group_wid) VALUES(?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> userWidItr = userWids.iterator();
                while (userWidItr.hasNext()) {
                    String userWid = userWidItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }
}
