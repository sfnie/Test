package org.ratchetgx.orion.security.yjs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;

/**
 * 检验研究生是否新生组
 * @author sfnie
 *
 */

public class CheckStusent {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private JdbcTemplate jdbcTemplate;
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {	 
		this.jdbcTemplate = jdbcTemplate;
	}

	 
	public Collection<String> eval(String username,Collection<String> roles) {
		//遍历用户角色信息		
		Iterator<String> roleItr = roles.iterator();
		 
		Boolean isChecked = Boolean.FALSE;
		while (roleItr.hasNext()) {
			String role = roleItr.next();
			if(("硕士生".equals(role)) || ("博士生".equals(role))){		
				//校验学生是否新生
				 isChecked =checkStudents(username);							 
			}
		}		
		
		if(isChecked.booleanValue()){
			roles.removeAll(roles);
			roles.add("新生");			
		}	
	
		return roles;
	}
    
	
	public Boolean checkStudents(final String userId) {
	 
		/*String sql = "SELECT xh,nj,rxjjdm FROM t_xjgl_xjxx_yjsjbxx yjs,t_xb_xq xq "
                    +" WHERE yjs.nj=xq.nd AND yjs.rxjjdm = xq.xqjj "
                    +" AND sfdqxq='1' AND xh= ? ";*/
		
		 String sql = "SELECT xh FROM t_XJGL_XJXX_QRSZ_XSFW fw "
			        +" where  fw.qrszwid in (select sz.wid from T_XJGL_XJXX_XXQRSZ  sz where qrlx = '2')"
		            +" AND xh= ? ";
		Boolean isNewStudent = jdbcTemplate.query(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, userId.toLowerCase());
					}
				}, new ResultSetExtractor<Boolean>() {
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {						 
						Boolean bo=Boolean.FALSE;
						if(rs.next()){							
							bo =Boolean.TRUE;
						}
						
						return bo;
					}
				});
		
		if(isNewStudent==null){
			return Boolean.FALSE;			
		}		
		
		log.debug("--------------:"+isNewStudent.booleanValue());
		if(!isNewStudent.booleanValue()){
			return Boolean.FALSE;			
	    }
		
		sql="SELECT  * FROM T_XJGL_XJXX_XXQRSZ  WHERE qrlx='2' AND trunc(SYSDATE) between trunc(qrkssj) and  trunc(qrjssj)";
    	
		Boolean isIntimes = jdbcTemplate.query(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement pstmt)
							throws SQLException {					 
					}
				}, new ResultSetExtractor<Boolean>() {
					public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {						 
						Boolean bo=Boolean.FALSE;
						if(rs.next()){							
							bo =Boolean.TRUE;
						}
						
						return bo;
					}
				});
		
	
		if(isIntimes==null){
			return Boolean.FALSE;			
		}
		 	
	    if(isIntimes.booleanValue()){
	    	return Boolean.TRUE;	    	
	    }
	    
	    return Boolean.FALSE;			
	}
}
