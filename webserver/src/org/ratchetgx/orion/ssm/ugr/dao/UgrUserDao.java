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
public class UgrUserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> listUsers() {
        String sql = "SELECT wid,name FROM ss_user";
        List<Map<String, String>> users = (List<Map<String, String>>) jdbcTemplate
                .query(sql, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<Map<String, String>> users = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("wid", rs.getString("wid"));
                    map.put("name", rs.getString("name"));
                    users.add(map);
                }
                return users;
            }
        });
        return users;
    }

    public void addUser(final String userName) {
        String sql = "INSERT INTO ss_user(wid,name,password,bh) values(sys_guid(),?,?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, userName);
                pstmt.setString(2, "");
                pstmt.setString(3, userName);

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteUser(final String userWid) {
        String sql = "DELETE FROM ss_user WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, userWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteUsers(final List<String> userWids) {
        String sql = "DELETE FROM ss_user WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                Iterator<String> userWidItr = userWids.iterator();
                while (userWidItr.hasNext()) {
                    String userWid = userWidItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        });
    }
}
