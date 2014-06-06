package org.ratchetgx.orion.ssm.menu.dao;

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
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class MenuDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> list() {
        String sql = "SELECT wid,name,memo,role FROM SS_MENU";

        return (List<Map<String, String>>) jdbcTemplate.query(sql,
                new ResultSetExtractor() {
                    public Object extractData(ResultSet rs)
                            throws SQLException, DataAccessException {
                        List<Map<String, String>> menus = new ArrayList<Map<String, String>>();

                        while (rs.next()) {
                            Map<String, String> menu = new HashMap<String, String>();
                            menu.put("wid", rs.getString("wid"));
                            menu.put("name", rs.getString("name"));
                            menu.put("memo", rs.getString("memo"));
                            menu.put("role", rs.getString("role"));
                            menus.add(menu);
                        }

                        return menus;
                    }
                });
    }

    public Map<String, String> load(final String menuWid) {
        String sql = "SELECT wid,name,memo,role FROM ss_menu WHERE wid=?";

        return (Map<String, String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, menuWid);

                    }
                }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> menu = new HashMap<String, String>();

                if (rs.next()) {
                    menu.put("wid", rs.getString("wid"));
                    menu.put("name", rs.getString("name"));
                    menu.put("memo", rs.getString("memo"));
                    menu.put("role", rs.getString("role"));
                }

                return menu;
            }
        });
    }

    public void add(final String name, final String memo) {
        String sql = "INSERT INTO ss_menu(wid,name,memo) values(sys_guid(),?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, memo);

                return pstmt.executeUpdate();
            }
        });
    }

    public void edit(final String wid, final String name, final String memo, final String role) {
        String sql = "UPDATE ss_menu SET name=?,memo=?,role=? WHERE wid=?";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, memo);
                pstmt.setString(3, role);
                pstmt.setString(4, wid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void delete(final String menuWid) {
        String sql_1 = "DELETE FROM ss_menuitem WHERE menu_wid=?";
        jdbcTemplate.execute(sql_1, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, menuWid);

                return pstmt.executeUpdate();
            }
        });

        String sql_0 = "DELETE FROM ss_menu WHERE wid=?";
        jdbcTemplate.execute(sql_0, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, menuWid);

                return pstmt.executeUpdate();
            }
        });

    }

    public List<String> listUnSelectedRoles() {
        String sql = "select ROLE from SS_ROLE r where NOT EXISTS (select ROLE from SS_MENU m WHERE r.role = m.role)";

        List<String> roles = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
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
