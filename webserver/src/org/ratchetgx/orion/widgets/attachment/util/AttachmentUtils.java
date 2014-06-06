package org.ratchetgx.orion.widgets.attachment.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AttachmentUtils {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 将附件文件复制到指定的路径下
	 * @throws IOException 
	 */
	public boolean copyAttachmentFiles(String srcServiceId, String dstPath) throws IOException {
		
		/** 校验目标路径的合规性 */
		File destDir = new File(dstPath);
		if (destDir.exists() && !destDir.isDirectory()) {  //指定路径为一个文件
			log.error("destPath不是一个目录，而是一个存在的文件:" + dstPath);
			return false;
		}
		if (!destDir.exists()) {  //指定路径不存在
			destDir.mkdirs();
		} 
		
		/** 查询需要复制的附件信息 */
		String str = "SELECT attfile.file_path AS filepath FROM ss_attachment att "
			       + " LEFT JOIN ss_attachment_file attfile ON att.wid=attfile.attachment_wid "
			       + " WHERE att.serviceid=?";
		List attachmentList = jdbcTemplate.queryForList(str, srcServiceId);
		if (attachmentList == null) {
			attachmentList = new ArrayList();
		}
		
		/** 将所有的附件文件复制到目标目录 */
		for (Iterator iter = attachmentList.iterator(); iter.hasNext();) {
			Map attMap = (Map)iter.next();
			String srcFilePath = (String)attMap.get("filepath");
			
			File srcFile = new File(srcFilePath);
			File dstFile = new File(dstPath + "/" + srcFile.getName());
			
			if (!dstFile.exists()) {
				copyFile(srcFile, dstFile);
			}
		}
		
		return true;
	}
	
	/**
	 * 将附件文件连通附件信息复制到指定的路径下
	 * @throws IOException 
	 */
	@Transactional
	public String copyAttachmentFilesWithInfo(final String srcServiceId, String dstPath) throws IOException {
		
		/** 获取newServiceId */
		String newServiceId = UUID.randomUUID().toString();
		
		/** 复制附件文件 */
		boolean bSucc = copyAttachmentFiles(srcServiceId, dstPath);
		
		/** 复制附件信息 */
		if (bSucc) {  
			
			String sql = "SELECT wid,origin_name,filetype,store_mode,serviceid FROM ss_attachment "
		        + " WHERE serviceid=?";
			final List attachmentList = new ArrayList();
			jdbcTemplate.query(sql,
					new PreparedStatementSetter() {
						public void setValues(PreparedStatement pstmt) throws SQLException {	
							pstmt.setString(1, srcServiceId);
						}
					}, 
					new ResultSetExtractor<Boolean>() {
						public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {						 
							
							while (rs.next()) {	
								Map dataMap = new HashMap();
								dataMap.put("wid", rs.getString("wid"));
								dataMap.put("origin_name", rs.getString("origin_name"));
								dataMap.put("filetype", rs.getString("filetype"));
								dataMap.put("store_mode", rs.getString("store_mode"));
								dataMap.put("serviceid", rs.getString("serviceid"));
								attachmentList.add(dataMap);
							}
							return true;
						}
			});
			
			for (Iterator iter = attachmentList.iterator(); iter.hasNext(); ) {
				Map attachmentMap = (Map)iter.next();
				String attachmentWid = (String)attachmentMap.get("wid");
				String newAttachmentWid = UUID.randomUUID().toString();
				
				sql = "INSERT INTO ss_attachment (wid,origin_name,filetype,store_mode,serviceid) "
			        + " SELECT ?,origin_name,filetype,store_mode,? FROM ss_attachment "
			        + " WHERE wid=?";
				jdbcTemplate.update(sql, newAttachmentWid, newServiceId, attachmentWid);
				
				sql = "INSERT INTO ss_attachment_file (wid,file_path,attachment_wid) "
			        + " SELECT sys_guid(),file_path,? FROM ss_attachment_file "
			        + " WHERE attachment_wid=?";
				jdbcTemplate.update(sql, newAttachmentWid, attachmentWid);
				
			}
			
		}
		
		return newServiceId;
	}
	
	private static void copyFile(File sourceFile, File destFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(destFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

}
