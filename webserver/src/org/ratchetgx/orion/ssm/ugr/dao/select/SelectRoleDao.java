package org.ratchetgx.orion.ssm.ugr.dao.select;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

@Repository
public class SelectRoleDao {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * groupWid相关的角色
     *
     * @param groupWid
     * @return
     */
    public List<String> listSelectedOfGroup(final String groupWid) {
        String sql = "SELECT role FROM ss_group_rel_role r  WHERE r.group_wid = ?";
        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, groupWid);
                    }
                }, new ResultSetExtractor() {
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

    /**
     * groupWid不相关的角色
     *
     * @param groupWid
     * @return
     */
    public List<String> listUnSelectedOfGroup(final String groupWid) {
        String sql = "SELECT t.role FROM ss_role t WHERE not exists(select * from ss_group_rel_role r where t.role = r.role AND r.group_wid=?)";
        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, groupWid);
                    }
                }, new ResultSetExtractor() {
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

    public List<String> listSelectedOfUser(final String userWid) {
        String sql = "SELECT role FROM ss_user_rel_role r  WHERE r.user_wid = ?";
        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, userWid);
                    }
                }, new ResultSetExtractor() {
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

    public List<String> listUnSelectedOfUser(final String userWid) {
        String sql = "SELECT t.role FROM ss_role t WHERE not exists(select * from ss_user_rel_role r where t.role = r.role AND r.user_wid=?)";
        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, userWid);
                    }
                }, new ResultSetExtractor() {
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

    public void cancelOfGroup(final String groupWid, final List<String> roles) {
        String sql = "DELETE FROM ss_group_rel_role WHERE role=? AND group_wid=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> roleItr = roles.iterator();
                while (roleItr.hasNext()) {
                    String role = roleItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfGroup(final String groupWid, final List<String> roles) {
        String sql = "INSERT INTO ss_group_rel_role(role,group_wid) values(?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> roleItr = roles.iterator();
                while (roleItr.hasNext()) {
                    String role = roleItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, groupWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void cancelOfUser(final String userWid, final List<String> roles) {
        String sql = "DELETE FROM ss_user_rel_role WHERE user_wid=? AND role=?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> roleItr = roles.iterator();
                while (roleItr.hasNext()) {
                    String role = roleItr.next();
                    pstmt.setString(1, userWid);
                    pstmt.setString(2, role);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }

    public void addOfUser(final String userWid, final List<String> roles) {
        String sql = "INSERT INTO ss_user_rel_role(role,user_wid) values(?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                Iterator<String> roleItr = roles.iterator();
                while (roleItr.hasNext()) {
                    String role = roleItr.next();
                    pstmt.setString(1, role);
                    pstmt.setString(2, userWid);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });

    }
}
