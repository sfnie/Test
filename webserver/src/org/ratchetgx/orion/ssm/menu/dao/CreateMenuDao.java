package org.ratchetgx.orion.ssm.menu.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
public class CreateMenuDao {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> listModules(final String parentModuleWid) {
        String sql = "";
        List<Map<String, String>> modules = null;
        if (parentModuleWid == null) {
            sql = "SELECT wid,name,memo FROM ss_module WHERE parent_wid IS NULL";
            modules = (List<Map<String, String>>) jdbcTemplate.query(sql,
                    new ResultSetExtractor() {
                        public Object extractData(ResultSet rs)
                                throws SQLException, DataAccessException {
                            List<Map<String, String>> modules = new ArrayList<Map<String, String>>();
                            while (rs.next()) {
                                Map<String, String> module = new HashMap<String, String>();
                                module.put("wid", rs.getString("wid"));
                                module.put("name", rs.getString("name"));
                                module.put("memo", rs.getString("memo"));
                                modules.add(module);
                            }
                            return modules;
                        }
                    });
        } else {
            sql = "SELECT wid,name,memo FROM ss_module WHERE parent_wid = ?";
            modules = (List<Map<String, String>>) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setString(1, parentModuleWid);
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    List<Map<String, String>> modules = new ArrayList<Map<String, String>>();
                    while (rs.next()) {
                        Map<String, String> module = new HashMap<String, String>();
                        module.put("wid", rs.getString("wid"));
                        module.put("name", rs.getString("name"));
                        module.put("memo", rs.getString("memo"));
                        modules.add(module);
                    }
                    return modules;
                }
            });
        }

        return modules;
    }

    public Map<String, String> getDefaultPathOfModule(final String moduleWid) {
        String sql = "SELECT wid,path,memo FROM ss_modulepath WHERE module_wid = ?";
        Map<String, String> path = (Map<String, String>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, moduleWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> path = new HashMap<String, String>();
                if (rs.next()) {
                    path.put("wid", rs.getString("wid"));
                    path.put("path", rs.getString("path"));
                    path.put("memo", rs.getString("memo"));
                }
                return path;
            }
        });

        return path;
    }

    public void createMenu(final String wid, final String name,
            final String memo,final String role) {
        String sql = "INSERT INTO ss_menu(wid,name,memo,role) values(?,?,?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, wid);
                pstmt.setString(2, name);
                pstmt.setString(3, memo);
                pstmt.setString(4, role);

                return pstmt.executeUpdate();
            }
        });
    }

    public void createMenuItem(final Map<String, String> menuItem,
            final String parentWid) {
        String sql = "INSERT INTO ss_menuitem(wid,name,path,module_wid,menu_wid,memo,parent_wid) VALUES(?,?,?,?,?,?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, menuItem.get("wid"));
                pstmt.setString(2, menuItem.get("name"));
                pstmt.setString(3, menuItem.get("path"));
                pstmt.setString(4, menuItem.get("moduleWid"));
                pstmt.setString(5, menuItem.get("menuWid"));
                pstmt.setString(6, menuItem.get("memo"));
                pstmt.setString(7, parentWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public String getSysguid() {
        String sql = "SELECT sys_guid() FROM dual";
        return (String) jdbcTemplate.query(sql, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                String wid = "";
                if (rs.next()) {
                    wid = rs.getString(1);
                }
                return wid;
            }
        });
    }
}
