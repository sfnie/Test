package org.ratchetgx.orion.security;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.ratchetgx.orion.common.SsfwException;
import org.ratchetgx.orion.common.cache.CacheConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.AntPathRequestMatcher;

public class SsfwInvocationSecurityMetadataSource implements
		FilterInvocationSecurityMetadataSource {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static String SECURITY_METADATA_SOURCE = CacheConstants.CACHE_ELEMENT_KEY + "." + "security.metadata.source";

	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private Cache securityCache;

	public void setSecurityCache(Cache securityCache) {
		this.securityCache = securityCache;
	}

	// 模块路径与角色列表对应关系
	private Map<String, List<ConfigAttribute>> initResouceDefine() {
		log.debug("initResouceDefine()");
		Map<String, List<ConfigAttribute>> resourceDefine = new HashMap<String, List<ConfigAttribute>>();

		try {
			// 每个功能模块对应的角色列表
			final Map<String, List<String>> rel = new HashMap<String, List<String>>();
			String sql = "SELECT m.wid,r.role FROM ss_module m LEFT JOIN ss_module_rel_role r ON m.wid = r.module_wid";
			jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {

				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					while (rs.next()) {
						String module = rs.getString("wid");
						String role = rs.getString("role");
						if (rel.containsKey(module)) {
							List roles = (List) rel.get(module);
							if (!roles.contains(role)) {
								roles.add(role);
							}
						} else {
							List roles = new ArrayList<String>();
							roles.add(role);
							rel.put(module, roles);
						}
					}

					return null;
				}
			});

			// 根据模块与角色列表对应关系，设置模块路径与角色列表对应关系
			sql = "SELECT path FROM ss_modulepath WHERE module_wid = ?";
			Iterator<Entry<String, List<String>>> relItr = rel.entrySet()
					.iterator();
			while (relItr.hasNext()) {
				Map.Entry<String, List<String>> e = relItr.next();
				final String moduleWid = e.getKey();
				List<String> roles = e.getValue();

				Collection<String> modulepaths = jdbcTemplate.query(sql,
						new PreparedStatementSetter() {

							public void setValues(PreparedStatement pstmt)
									throws SQLException {
								pstmt.setString(1, moduleWid);
							}
						}, new ResultSetExtractor<Collection<String>>() {

							public Collection<String> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								Collection<String> paths = new ArrayList<String>();
								while (rs.next()) {
									paths.add(rs.getString("path"));
								}

								return paths;
							}
						});

				Iterator<String> modulepathItr = modulepaths.iterator();
				while (modulepathItr.hasNext()) {
					String modulepath = (String) modulepathItr.next();
					if (resourceDefine.containsKey(modulepath)) {
						List<ConfigAttribute> attrs = resourceDefine
								.get(modulepath);
						Iterator<String> roleItr = roles.iterator();
						while (roleItr.hasNext()) {
							String role = roleItr.next();

							if (role == null) {// 功能模块没有配置相应的角色
								continue;
							}

							ConfigAttribute ca = new SecurityConfig(role);
							if (!attrs.contains(ca)) {
								attrs.add(ca);
							}
						}
					} else {
						List<ConfigAttribute> attrs = new ArrayList<ConfigAttribute>();
						Iterator<String> roleItr = roles.iterator();
						while (roleItr.hasNext()) {
							String role = roleItr.next();

							if (role == null) {// 功能模块没有配置相应的角色
								continue;
							}

							ConfigAttribute ca = new SecurityConfig(role);
							if (!attrs.contains(ca)) {
								attrs.add(ca);
							}
						}
						resourceDefine.put(modulepath, attrs);
					}
				}
			}

			Iterator rdItr = resourceDefine.keySet().iterator();
			while (rdItr.hasNext()) {
				String key = (String) rdItr.next();
				List<ConfigAttribute> cas = resourceDefine.get(key);
				String vs = "";
				Iterator caItr = cas.iterator();
				while (caItr.hasNext()) {
					vs += caItr.next();
					vs += ",";
				}
			}
		} catch (Exception e) {
			log.error("", e);
			throw new SsfwException(e.getMessage());
		}

		return resourceDefine;
	}

	public Collection<ConfigAttribute> getAllConfigAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ConfigAttribute> getAttributes(Object object)
			throws IllegalArgumentException {
		HttpServletRequest request = ((FilterInvocation) object)
				.getHttpRequest();

		Element element = securityCache
				.get(SsfwInvocationSecurityMetadataSource.SECURITY_METADATA_SOURCE);
		if (element == null) {
			synchronized (this) {
				element = securityCache
						.get(SsfwInvocationSecurityMetadataSource.SECURITY_METADATA_SOURCE);
				if (element == null) {// 调用实际的方法
					try {
						element = new Element(
								SsfwInvocationSecurityMetadataSource.SECURITY_METADATA_SOURCE,
								initResouceDefine());
						securityCache.put(element);
					} catch (Throwable e) {
						log.error("",e);
					}
				}
			}
		}

		@SuppressWarnings("unchecked")
		Map<String, List<ConfigAttribute>> resourceDefine = (Map<String, List<ConfigAttribute>>) element
				.getObjectValue();

		Iterator<String> itr = resourceDefine.keySet().iterator();
		while (itr.hasNext()) {
			String resURL = itr.next();
			if (resURL == null || "".equals(resURL.trim())) {
				log.warn("资源路径为空。");
				continue;
			}
			if (new AntPathRequestMatcher(resURL).matches(request)) {
				return resourceDefine.get(resURL);
			}
		}
		return null;
	}

	public boolean supports(Class<?> arg0) {
		return true;
	}
}
