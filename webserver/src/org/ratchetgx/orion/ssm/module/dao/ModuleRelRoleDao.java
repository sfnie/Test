package org.ratchetgx.orion.ssm.module.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
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
public class ModuleRelRoleDao {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void add(final String module_id, final String role) {
        String sql = "insert into SS_MODULE_REL_ROLE(MODULE_WID,ROLE) values(?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, module_id);
                pstmt.setString(2, role);

                return pstmt.executeUpdate();

            }
        });
    }

    public void delete(final String module_id, final List<String> roles) {
        String sql = "delete from SS_MODULE_REL_ROLE where MODULE_WID = ? and ROLE = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                for (int i = 0; i < roles.size(); i++) {
                    String role = roles.get(i);

                    log.info(role + ":" + module_id);
                    pstmt.setString(1, module_id);
                    pstmt.setString(2, role);

                    pstmt.addBatch();
                }

                return pstmt.executeBatch();
            }
        });
    }

    public List<String> listSelected(final String module_id) {
        String sql = "select ROLE from SS_MODULE_REL_ROLE where MODULE_WID = ?";

        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, module_id);

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

    public List<String> listUnSelected(final String module_id) {
        String sql = "select ROLE from SS_ROLE where ROLE not in (select ROLE from SS_MODULE_REL_ROLE where MODULE_WID = ?)";

        JSONObject rev = new JSONObject();

        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, module_id);

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
}
