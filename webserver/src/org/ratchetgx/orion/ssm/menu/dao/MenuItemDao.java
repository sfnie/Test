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
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository 
public class MenuItemDao {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, String>> listItems(final String menuWid,
            final String parentMenuItemWid) {
        if (menuWid == null) {
            throw new RuntimeException(menuWid + " cant't be null.");
        }

        List<Map<String, String>> items = null;

        if (parentMenuItemWid == null || "".equals(parentMenuItemWid.trim())) {
            String sql = "SELECT wid,name,icon_path,path,module_wid,menu_wid,memo,parent_wid FROM ss_menuitem WHERE menu_wid=? AND parent_wid IS NULL ORDER BY indexed";

            items = (List<Map<String, String>>) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setString(1, menuWid);
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    List<Map<String, String>> menuItems = new ArrayList<Map<String, String>>();

                    while (rs.next()) {
                        Map<String, String> menuItem = new HashMap<String, String>();
                        menuItem.put("wid", rs.getString("wid"));
                        menuItem.put("name", rs.getString("name"));
                        menuItem.put("icon_path", rs.getString("icon_path"));
                        menuItem.put("path", rs.getString("path"));
                        menuItem.put("module_wid",
                                rs.getString("module_wid"));
                        menuItem.put("menu_wid",
                                rs.getString("menu_wid"));
                        menuItem.put("memo", rs.getString("memo"));
                        menuItem.put("parent_wid",
                                rs.getString("parent_wid"));

                        menuItems.add(menuItem);
                    }

                    return menuItems;
                }
            });
        } else {
            String sql = "SELECT wid,name,icon_path,path,module_wid,menu_wid,memo,parent_wid FROM ss_menuitem WHERE menu_wid=? AND parent_wid=? ORDER BY indexed";

            items = (List<Map<String, String>>) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setString(1, menuWid);
                            pstmt.setString(2, parentMenuItemWid);
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    List<Map<String, String>> menuItems = new ArrayList<Map<String, String>>();

                    while (rs.next()) {
                        Map<String, String> menuItem = new HashMap<String, String>();
                        menuItem.put("wid", rs.getString("wid"));
                        menuItem.put("name", rs.getString("name"));
                        menuItem.put("icon_path", rs.getString("icon_path"));
                        menuItem.put("path", rs.getString("path"));
                        menuItem.put("module_wid",
                                rs.getString("module_wid"));
                        menuItem.put("menu_wid",
                                rs.getString("menu_wid"));
                        menuItem.put("memo", rs.getString("memo"));
                        menuItem.put("parent_wid",
                                rs.getString("parent_wid"));

                        menuItems.add(menuItem);
                    }

                    return menuItems;
                }
            });
        }

        return items;
    }

    public void delete(final String menuItemWid) {
        String sql = "DELETE FROM ss_menuitem WHERE wid=?";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, menuItemWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void add(final String name, final String path,
            final String module_wid, final String menu_wid, final String memo,
            final String parent_wid, final String icon_path) {
        String sql = "INSERT INTO ss_menuitem(wid,name,path,module_wid,menu_wid,memo,parent_wid,icon_path) values(sys_guid(),?,?,?,?,?,?,?)";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, path);
                pstmt.setString(3, module_wid);
                pstmt.setString(4, menu_wid);
                pstmt.setString(5, memo);
                pstmt.setString(6, parent_wid);
                pstmt.setString(7, icon_path);

                return pstmt.executeUpdate();
            }
        });
    }

    public void edit(final String wid, final String name, final String path,
            final String module_wid, final String menu_wid, final String memo,
            final String parent_wid, final String icon_path) {
        String sql = "UPDATE ss_menuitem SET name=?,path=?,module_wid=?,menu_wid=?,memo=?,parent_wid=?,icon_path=? WHERE wid=?";

        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setString(1, name);
                pstmt.setString(2, path);
                pstmt.setString(3, module_wid);
                pstmt.setString(4, menu_wid);
                pstmt.setString(5, memo);
                pstmt.setString(6, parent_wid);
                pstmt.setString(7, icon_path);

                pstmt.setString(8, wid);

                return pstmt.executeUpdate();
            }
        });
    }

    public Map<String, String> loadItem(final String menuItemWid) {
        String sql = "SELECT wid,name,icon_path,path,module_wid,menu_wid,memo,parent_wid FROM ss_menuitem WHERE wid=?";

        final Map<String, String> menuItem = (Map<String, String>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, menuItemWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> menuItem = new HashMap<String, String>();

                if (rs.next()) {
                    menuItem.put("wid", rs.getString("wid"));
                    menuItem.put("name", rs.getString("name"));
                    menuItem.put("icon_path", rs.getString("icon_path"));
                    menuItem.put("path", rs.getString("path"));
                    menuItem.put("module_wid",
                            rs.getString("module_wid"));
                    menuItem.put("menu_wid", rs.getString("menu_wid"));
                    menuItem.put("memo", rs.getString("memo"));
                    menuItem.put("parent_wid",
                            rs.getString("parent_wid"));
                }

                return menuItem;
            }
        });

        sql = "SELECT name FROM ss_module WHERE wid=?";

        jdbcTemplate.query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt) throws SQLException {
                log.info("module_wid:" + menuItem.get("module_wid"));
                pstmt.setString(1, menuItem.get("module_wid"));
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException {

                if (rs.next()) {
                    menuItem.put("module_name", rs.getString("name"));
                }

                return null;
            }
        });

        return menuItem;
    }

    public void up(final String menuItemWid) {
        // 获取该菜单项的父项和顺序
        String sql = "SELECT parent_wid,indexed,menu_wid FROM ss_menuitem WHERE wid=?";
        final Map<String, String> menuItem = (Map<String, String>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, menuItemWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> menuItem = new HashMap<String, String>();
                if (rs.next()) {
                    menuItem.put("indexed", rs.getString("indexed"));
                    menuItem.put("parent_wid",
                            rs.getString("parent_wid"));
                    menuItem.put("menu_wid", rs.getString("menu_wid"));
                }

                return menuItem;
            }
        });

        // 更新所有indexed为空的兄弟菜单项的indexed为0
        if (menuItem.get("parent_wid") == null) {
            sql = "UPDATE ss_menuitem SET indexed = 0 WHERE indexed IS NULL AND menu_wid = ? AND parent_wid IS NULL";
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setString(1, menuItem.get("menu_wid"));

                    return pstmt.executeUpdate();
                }
            });
        } else {
            sql = "UPDATE ss_menuitem SET indexed = 0 WHERE indexed IS NULL AND menu_wid = ? AND parent_wid = ?";
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setString(1, menuItem.get("menu_wid"));
                    pstmt.setString(2, menuItem.get("parent_wid"));

                    return pstmt.executeUpdate();
                }
            });
        }

        final Integer indexed = Integer
                .parseInt(menuItem.get("indexed") == null ? "0" : menuItem
                .get("indexed"));

        // 已经是第一个了
        if (indexed == 0) {
            return;
        }

        // 获取上移之前menuItemWid之前的菜单项
        String preMenuItemWid = "";
        if (menuItem.get("parent_wid") == null) {
            sql = "SELECT wid FROM ss_menuitem WHERE indexed < ? AND menu_wid = ? AND parent_wid IS NULL ORDER BY indexed DESC ";
            preMenuItemWid = (String) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setInt(1, indexed);
                            pstmt.setString(2, menuItem.get("menu_wid"));
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    String preMenuItemWid = "";

                    if (rs.next()) {
                        preMenuItemWid = rs.getString("wid");
                    }

                    return preMenuItemWid;
                }
            });

        } else {
            sql = "SELECT wid FROM ss_menuitem WHERE indexed < ? AND menu_wid = ? AND parent_wid = ? ORDER BY indexed DESC ";
            preMenuItemWid = (String) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setInt(1, indexed);
                            pstmt.setString(2, menuItem.get("menu_wid"));
                            pstmt.setString(3, menuItem.get("parent_wid"));
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    String preMenuItemWid = "";

                    if (rs.next()) {
                        preMenuItemWid = rs.getString("wid");
                    }

                    return preMenuItemWid;
                }
            });

        }

        // 更新menuItemWid的indexed
        sql = "UPDATE ss_menuitem SET indexed = ? WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setInt(1, indexed - 1);
                pstmt.setString(2, menuItemWid);

                return pstmt.executeUpdate();
            }
        });

        final String finalPreMenuItemWid = preMenuItemWid;

        // 更新preMenuItemWid的indexed
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setInt(1, indexed);
                pstmt.setString(2, finalPreMenuItemWid);

                return pstmt.executeUpdate();
            }
        });
    }

    public void down(final String menuItemWid) {
        // 获取该菜单项的父项和顺序
        String sql = "SELECT parent_wid,indexed,menu_wid FROM ss_menuitem WHERE wid=?";
        final Map<String, String> menuItem = (Map<String, String>) jdbcTemplate
                .query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt)
                    throws SQLException {
                pstmt.setString(1, menuItemWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                Map<String, String> menuItem = new HashMap<String, String>();
                if (rs.next()) {
                    menuItem.put("indexed", rs.getString("indexed"));
                    menuItem.put("parent_wid",
                            rs.getString("parent_wid"));
                    menuItem.put("menu_wid", rs.getString("menu_wid"));
                }

                return menuItem;
            }
        });

        // 更新所有indexed为空的兄弟菜单项的indexed为0
        if (menuItem.get("parent_wid") == null) {
            sql = "UPDATE ss_menuitem SET indexed = 0 WHERE indexed IS NULL AND menu_wid = ? AND parent_wid IS NULL";
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setString(1, menuItem.get("menu_wid"));

                    return pstmt.executeUpdate();
                }
            });
        } else {
            sql = "UPDATE ss_menuitem SET indexed = 0 WHERE indexed IS NULL AND menu_wid = ? AND parent_wid = ?";
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setString(1, menuItem.get("menu_wid"));
                    pstmt.setString(2, menuItem.get("parent_wid"));

                    return pstmt.executeUpdate();
                }
            });
        }

        final Integer indexed = Integer
                .parseInt(menuItem.get("indexed") == null ? "0" : menuItem
                .get("indexed"));

        // 获取下移之前menuItemWid之后的菜单项
        String nextMenuItemWid = "";
        if (menuItem.get("parent_wid") == null) {
            sql = "SELECT wid FROM ss_menuitem WHERE indexed > ? AND menu_wid = ? AND parent_wid IS NULL ORDER BY indexed ASC ";
            nextMenuItemWid = (String) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setInt(1, indexed);
                            pstmt.setString(2, menuItem.get("menu_wid"));
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    String preMenuItemWid = "";

                    if (rs.next()) {
                        preMenuItemWid = rs.getString("wid");
                    }

                    return preMenuItemWid;
                }
            });

        } else {
            sql = "SELECT wid FROM ss_menuitem WHERE indexed > ? AND menu_wid = ? AND parent_wid = ? ORDER BY indexed ASC ";
            nextMenuItemWid = (String) jdbcTemplate.query(sql,
                    new PreparedStatementSetter() {
                        public void setValues(PreparedStatement pstmt)
                                throws SQLException {
                            pstmt.setInt(1, indexed);
                            pstmt.setString(2, menuItem.get("menu_wid"));
                            pstmt.setString(3, menuItem.get("parent_wid"));
                        }
                    }, new ResultSetExtractor() {
                public Object extractData(ResultSet rs)
                        throws SQLException, DataAccessException {
                    String preMenuItemWid = "";

                    if (rs.next()) {
                        preMenuItemWid = rs.getString("wid");
                    }

                    return preMenuItemWid;
                }
            });

        }

        // 更新menuItemWid的indexed
        sql = "UPDATE ss_menuitem SET indexed = ? WHERE wid = ?";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setInt(1, indexed + 1);
                pstmt.setString(2, menuItemWid);

                return pstmt.executeUpdate();
            }
        });

        // 更新nextMenuItemWid的indexed
        final String finalNextMenuItemWid = nextMenuItemWid;
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement pstmt)
                    throws SQLException, DataAccessException {

                pstmt.setInt(1, indexed);
                pstmt.setString(2, finalNextMenuItemWid);

                return pstmt.executeUpdate();
            }
        });

    }

    /**
     * menuItemWid子项排序
     *
     * @param menuItemWid
     */
    public void sort(final String menuItemWid) {
        String sql = "SELECT COUNT(*) FROM ss_menuitem WHERE parent_wid = ?";

        Object c = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt) throws SQLException {
                pstmt.setString(1, menuItemWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                int c = 0;

                if (rs.next()) {
                    c = rs.getInt(1);
                }

                return c;
            }
        });

        sql = "SELECT wid FROM ss_menuitem WHERE parent_wid = ? ORDER BY indexed ASC";
        List<String> wids = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, menuItemWid);
                    }
                }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<String> wids = new ArrayList<String>();

                while (rs.next()) {
                    wids.add(rs.getString("wid"));
                }

                return wids;
            }
        });

        sql = "UPDATE ss_menuitem SET indexed = ? WHERE wid = ?";
        for (int i = 0; i < wids.size(); i++) {
            final int indexed = i;
            final String wid = wids.get(i);
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setInt(1, indexed);
                    pstmt.setString(2, wid);

                    return pstmt.executeUpdate();
                }
            });
            //递归调用
            sort(wid);
        }

    }

    /**
     * menuItemWid子项排序
     *
     * @param menuItemWid
     */
    public void topSort(final String menuWid) {
        if (menuWid == null || "".equals(menuWid.trim())) {
            throw new RuntimeException(menuWid + "不能为空！");
        }

        String sql = "SELECT COUNT(*) FROM ss_menuitem WHERE menu_wid = ? AND parent_wid IS NULL";

        Object c = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement pstmt) throws SQLException {
                pstmt.setString(1, menuWid);
            }
        }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                int c = 0;

                if (rs.next()) {
                    c = rs.getInt(1);
                }

                return c;
            }
        });

        sql = "SELECT wid FROM ss_menuitem WHERE menu_wid = ? AND parent_wid IS NULL ORDER BY indexed ASC";
        List<String> wids = (List<String>) jdbcTemplate.query(sql,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement pstmt)
                            throws SQLException {
                        pstmt.setString(1, menuWid);
                    }
                }, new ResultSetExtractor() {
            public Object extractData(ResultSet rs)
                    throws SQLException, DataAccessException {
                List<String> wids = new ArrayList<String>();

                while (rs.next()) {
                    wids.add(rs.getString("wid"));
                }

                return wids;
            }
        });

        sql = "UPDATE ss_menuitem SET indexed = ? WHERE wid = ?";
        for (int i = 0; i < wids.size(); i++) {
            final int indexed = i;
            final String wid = wids.get(i);
            jdbcTemplate.execute(sql, new PreparedStatementCallback() {
                public Object doInPreparedStatement(PreparedStatement pstmt)
                        throws SQLException, DataAccessException {

                    pstmt.setInt(1, indexed);
                    pstmt.setString(2, wid);

                    return pstmt.executeUpdate();
                }
            });
            //递归调用
            sort(wid);
        }

    }

    /**
     * 把wid=moduleWid的模块以及其后代模块作为子项添加至menuItemWid菜单项
     *
     * @param moduleWid
     * @param menuItemWid
     */
    @Transactional
    public Map dd(final String moduleWid, final String parentMenuItemWid,final String menuWid) {

        //moduleWid添加至菜单项menuItemWid的子菜单项
        String sql = "select sys_guid() from dual";
        final String generatedMenuitemWid = jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        });
        sql = "select name from ss_module where wid = ?";
        final String moduleName = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, moduleWid);
            }
        }, new ResultSetExtractor<String>() {
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getString(1);
                }
                return null;
            }
        });
        sql = "insert into ss_menuitem(wid,name,module_wid,menu_wid,parent_wid) values(?,?,?,?,?)";
        jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ps.setString(1, generatedMenuitemWid);
                ps.setString(2, moduleName);
                ps.setString(3, moduleWid);
                ps.setString(4, menuWid);
                ps.setString(5, parentMenuItemWid);

                return ps.executeUpdate();

            }
        });

        //moduleWid的子模块添加至wid菜单项的子项
        sql = "select wid from ss_module where parent_wid = ?";
        List<String> childModuleWids = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, moduleWid);
            }
        }, new ResultSetExtractor<List<String>>() {
            public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<String> childModuleWids = new ArrayList<String>();
                while (rs.next()) {
                    childModuleWids.add(rs.getString(1));
                }
                return childModuleWids;
            }
        });

        for (int i = 0; i < childModuleWids.size(); i++) {
            String childModuleWid = childModuleWids.get(i);
            dd(childModuleWid, generatedMenuitemWid,menuWid);
        }

        Map map = new HashMap();
        map.put("wid", generatedMenuitemWid);
        map.put("name", moduleName);
        map.put("module_wid", moduleWid);
        map.put("menu_wid", menuWid);
        map.put("parent_wid", parentMenuItemWid);

        return map;

    }
}
