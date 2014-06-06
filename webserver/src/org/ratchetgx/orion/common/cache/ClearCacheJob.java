package org.ratchetgx.orion.common.cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedStatementProcessor;
import org.ratchetgx.orion.common.util.IResultSetProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存相关任务
 * 
 * @author hrfan
 * 
 */
public class ClearCacheJob {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DbUtil dbUtil;

	private Cache selectRangeCache;

	public void setSelectRangeCache(Cache selectRangeCache) {
		this.selectRangeCache = selectRangeCache;
	}

	@Transactional
	public void work() throws SQLException {
		final List<String> srs = new ArrayList<String>();
		String sql = "SELECT * FROM T_SYS_CACHE_SELECTRANGE_TIMER";
		dbUtil.execute(sql, new IResultSetProcessor() {

			public void process(ResultSet rs) throws SQLException {
				while (rs.next()) {
					String cacheName = rs.getString("SELECTRANGE");
					srs.add(cacheName);
				}
			}
		});

		Iterator keyItr;
		List<String> toDeleteKeys = new ArrayList<String>();

		Iterator itr = srs.iterator();
		while (itr.hasNext()) {
			String sr = (String) itr.next();
			keyItr = selectRangeCache.getKeys().iterator();
			while (keyItr.hasNext()) {
				String key = keyItr.next().toString();
				if (key.endsWith(sr)) {
					toDeleteKeys.add(key);
					break;
				}
			}
		}

		keyItr = toDeleteKeys.iterator();
		while (keyItr.hasNext()) {
			String key = (String) keyItr.next();
			selectRangeCache.remove(key);
		}

		sql = "DELETE FROM  T_SYS_CACHE_SELECTRANGE_TIMER";
		dbUtil.execute(sql, new IPreparedStatementProcessor() {

			public void process(PreparedStatement pstmt) throws SQLException {
				pstmt.executeUpdate();

			}
		});
	}
}
