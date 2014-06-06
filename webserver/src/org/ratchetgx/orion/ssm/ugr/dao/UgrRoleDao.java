package org.ratchetgx.orion.ssm.ugr.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class UgrRoleDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> listRoles() {
        String sql = "SELECT role FROM ss_role";
        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new ResultSetExtractor() {
                    public Object extractData(ResultSet rs)
                            throws SQLException, DataAccessException {
                        List<String> roles = new ArrayList<String>();
                        while (rs.next()) {
                            roles.add(rs.getString("role"));
                        }
                        return roles;
                    }
                });
        return roles;
    }

    public void addRole(final String role) {
        String sql = "INSERT INTO ss_role(role,memo) values(?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, role);
                pstmt.setString(2, "");

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteRole(final String role) {
        String sql = "DELETE FROM ss_role WHERE role = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                pstmt.setString(1, role);

                return pstmt.executeUpdate();
            }
        });
    }

    public void deleteRoles(final List<String> roles) {
        String sql = "DELETE FROM ss_role WHERE role = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {
                Iterator<String> roleItr = roles.iterator();
                while (roleItr.hasNext()) {
                    String role = roleItr.next();
                    pstmt.setString(1, role);
                    pstmt.addBatch();
                }
                return pstmt.executeBatch();
            }
        });
    }
}
