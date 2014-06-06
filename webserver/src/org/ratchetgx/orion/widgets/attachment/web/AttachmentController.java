package org.ratchetgx.orion.widgets.attachment.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.common.util.BizobjUtil;
import org.ratchetgx.orion.common.util.DbUtil;
import org.ratchetgx.orion.common.util.IPreparedResultSetProcessor;
import org.ratchetgx.orion.widgets.attachment.AttachmentHandler;
import org.ratchetgx.orion.widgets.attachment.AttachmentPosition;
import org.ratchetgx.orion.widgets.attachment.AttachmentPositionCreator;
import org.ratchetgx.orion.widgets.attachment.AttachmentPostUploadHandler;
import org.ratchetgx.orion.widgets.attachment.AttachmentPreUploadValidator;
import org.ratchetgx.orion.widgets.attachment.AttachmentSaveDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping(value = "/attach")
public class AttachmentController implements ApplicationContextAware {
	// 日志
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DbUtil dbUtil;

	@Autowired
	private BizobjUtil bizobjUtil;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	AttachmentSaveDbService ass;

	@RequestMapping(value = "attachment")
	public String attachment() throws SQLException {
		return "attach/attachment";
	}

	/**
	 * 
	 * @param handler
	 * @param attachInfo
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "uploadAttachment")
	public String uploadAttachment(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String handler = request.getParameter("handler");
		String serviceId = request.getParameter("serviceId"); // 对应业务模块ID
		
		//serviceId为空，自动生成serviceId
		if (serviceId == null || "".equals(serviceId)) {
			serviceId = dbUtil.getSysguid();
		}
		
		if (handler == null || "".equals(handler)) {
			handler = "attachmentHandler";
		}

		AttachmentHandler attachmentHandler = (AttachmentHandler) appCtx
				.getBean(handler);

		String attachinfo = request.getParameter("attachinfo") != null ? request
				.getParameter("attachinfo") : "";
		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		MultipartFile multFile = multiRequest.getFile("attachment");
		String suffix = multFile.getOriginalFilename().substring(
				multFile.getOriginalFilename().lastIndexOf(".") + 1);
		String filesize = String.valueOf(multFile.getSize());

		JSONObject rev = new JSONObject();

		try {

			log.debug(" --- uploadAttachment ---");
			rev.put("success", false);
			// 取得上传的文件名
			String originalfilename = multFile.getOriginalFilename();
			// 上传之前校验
			AttachmentPreUploadValidator attachvalidator = attachmentHandler
					.getAttachmentPreUploadValidator();
			log.debug("----------getAttachmentPreUploadValidator--------");
			if (attachvalidator != null) {
				attachvalidator.validator(multFile, attachinfo);
			}

			AttachmentPositionCreator positioncreator = attachmentHandler
					.getAttachmentPositionCreator();
			AttachmentPosition position = (AttachmentPosition) positioncreator
					.positionCreator(attachinfo);
			// 进行上传
			String attachmentId = attachmentHandler.uploadAttachment(multFile,
					attachinfo, position, serviceId);

			rev.put("attachmentId", attachmentId);
			rev.put("serviceId", serviceId);
			rev.put("fileType", suffix);
			rev.put("fileSize", filesize);
			rev.put("originName", originalfilename);

			// 上传成功完成后调用postUpload处理过程
			AttachmentPostUploadHandler postupload = attachmentHandler
					.getAttachmentPostUploadHandler();

			postupload.handle(attachmentId, attachinfo);

			rev.put("success", true);

		} catch (Exception ex) {
			log.error("", ex);
		}

		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(rev.toString());

		return null;
	}

	/**
	 * 一键下载
	 * 
	 * @param serviceId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = "oneKeyDownload")
	public String oneKeyDownloadAttachment(
			@RequestParam final String serviceId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// 临时zip文件
		String tempFileName = UUID.randomUUID().toString() + ".zip";
		String tempFileFolder = SsfwUtil.contextPath + "temp" + File.separator;
		File tempFile = new File(tempFileFolder);
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
		tempFile = new File(tempFileFolder + tempFileName);
		final org.apache.tools.zip.ZipOutputStream zipOutputStream = new org.apache.tools.zip.ZipOutputStream(
				new FileOutputStream(tempFile));
		//zipOutputStream.setEncoding("utf-8");
		try {

			String allSerivceAttachmentSql = "select t1.wid,t1.origin_name,t1.filetype,t1.store_mode,t2.file_path,t3.table_name,t3.field_name,t3.record_wid from ss_attachment t1 left join ss_attachment_file t2 on (t1.wid = t2.attachment_wid) left join ss_attachment_db t3 "
					+ " on (t1.wid = t3.attachment_wid) where t1.serviceid = ?";

			List<Map<String, Object>> allServiceAttachment = jdbcTemplate
					.queryForList(allSerivceAttachmentSql,
							new Object[] { serviceId });

			if (allServiceAttachment.size() == 0) {
				log.debug("serviceId:" + serviceId + "，没有附件。");
			}
			// 根据存储方式分类保存到不同集合中
			List<Map<String, Object>> fileModeAttachment = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> dbModeAttachment = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < allServiceAttachment.size(); i++) {
				Map<String, Object> map = allServiceAttachment.get(i);
				String storeMode = (String) map.get("store_mode");
				if ("db".equals(storeMode)) {
					dbModeAttachment.add(map);
				}
				if ("file".equals(storeMode)) {
					fileModeAttachment.add(map);
				}
			}
			// 文件存储方式,压缩到临时zip文件中。
			for (int i = 0; i < fileModeAttachment.size(); i++) {
				Map<String, Object> info = fileModeAttachment.get(i);
				log.debug("正在读取附件文件ID: " + info.get("wid") + ",路径:"
						+ info.get("file_path"));
				File file = new File((String) info.get("file_path"));
				if (file.exists()) {
					FileInputStream is = null;
					byte[] buffer = new byte[1024];
					int length = -1;
					try {
						org.apache.tools.zip.ZipEntry zipEntry = new org.apache.tools.zip.ZipEntry(
								(String) info.get("origin_name"));
						zipOutputStream.putNextEntry(zipEntry);
						is = new FileInputStream(file);
						while ((length = is.read(buffer)) != -1) {
							zipOutputStream.write(buffer, 0, length);
						}
						zipOutputStream.closeEntry();
					} catch (FileNotFoundException e) {
						log.error(e.getMessage(), e);
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					} finally {
						try {
							is.close();
						} catch (IOException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
			// DB存储方式,压缩到临时zip文件中。
			for (int i = 0; i < dbModeAttachment.size(); i++) {
				final Map<String, Object> info = dbModeAttachment.get(i);
				String sql = "select " + info.get("field_name") + " from "
						+ info.get("table_name") + " where wid = ?";
				jdbcTemplate.query(sql,
						new Object[] { info.get("record_wid") },
						new ResultSetExtractor() {
							public Object extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								while (rs.next()) {
									Blob blob = rs.getBlob(1);
									InputStream is = blob.getBinaryStream();
									org.apache.tools.zip.ZipEntry zipEntry = new org.apache.tools.zip.ZipEntry(
											(String) info.get("origin_name"));
									try {
										zipOutputStream.putNextEntry(zipEntry);

										byte[] buf = new byte[1024];
										int len = -1;
										while ((len = is.read(buf)) > 0) {
											zipOutputStream.write(buf, 0, len);
										}

									} catch (IOException e) {
										log.error("", e);
									} finally {
										try {
											is.close();
										} catch (IOException e) {
											log.error("", e);
										}
									}
								}
								return null;
							}
						});
			}
		} finally {
			try {
				zipOutputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		response.setContentType("application/x-msdownload;charset=UTF-8");
		response.setHeader("Content-Disposition",
				"attachment;filename=files.zip");

		FileInputStream is = null;
		OutputStream os = null;

		try {
			is = new FileInputStream(tempFile);
			os = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;

			while ((length = is.read(buffer)) != -1) {
				os.write(buffer, 0, length);
			}

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
					tempFile.delete();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	/**
	 * 下载单个附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("downloadAttachment")
	public String downloadAttachment(HttpServletRequest request,
			final HttpServletResponse response) {

		final String attachmentId = request.getParameter("attachmentId");
		// 获取附件存储模式
		final String[] store_mode = { "" };
		final String[] origin_name = { "" };
		final String[] file_type = { "" };
		try {

			String sql_1 = "SELECT filetype,origin_name,store_mode FROM ss_attachment WHERE wid = ?";

			dbUtil.execute(sql_1, new IPreparedResultSetProcessor() {

				public void processResultSet(ResultSet rs) throws SQLException {
					if (rs.next()) {
						store_mode[0] = rs.getString("store_mode");
						origin_name[0] = rs.getString("origin_name");
						file_type[0] = rs.getString("filetype");
					}

				}

				public void processPreparedStatement(PreparedStatement pstmt)
						throws SQLException {
					pstmt.setString(1, attachmentId);
				}
			});
			log.debug("origin_name[0] : " + origin_name[0]);
			origin_name[0] = URLEncoder.encode(origin_name[0], "UTF-8");
			response.setContentType("application/x-msdownload;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ origin_name[0]);

			// 数据库存储模式
			if ("db".equals(store_mode[0])) {

				final String[] tableName = { "" };
				final String[] fieldName = { "" };
				final String[] recordWid = { "" };
				String sql_2 = "SELECT table_name,field_name,record_wid FROM ss_attachment_db WHERE attachment_wid = ?";
				dbUtil.execute(sql_2, new IPreparedResultSetProcessor() {

					public void processResultSet(ResultSet rs)
							throws SQLException {
						if (rs.next()) {
							tableName[0] = rs.getString("table_name");
							fieldName[0] = rs.getString("field_name");
							recordWid[0] = rs.getString("record_wid");
						}

					}

					public void processPreparedStatement(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, attachmentId);
					}
				});

				// 获取附件流
				if (tableName[0].length() > 0 && fieldName[0].length() > 0
						&& recordWid[0].length() > 0) {
					String sql_3 = "SELECT " + fieldName[0] + " FROM "
							+ tableName[0] + " WHERE wid=?";
					dbUtil.execute(sql_3, new IPreparedResultSetProcessor() {

						public void processResultSet(ResultSet rs)
								throws SQLException {
							if (rs.next()) {
								Blob blob = rs.getBlob(1);
								InputStream is = blob.getBinaryStream();
								try {
									OutputStream os = response
											.getOutputStream();
									byte[] buf = new byte[1024];
									int len = -1;
									while ((len = is.read(buf)) > 0) {
										os.write(buf, 0, len);
									}
									is.close();
									os.close();
								} catch (IOException e) {
									log.error("", e);
								}
							}
						}

						public void processPreparedStatement(
								PreparedStatement pstmt) throws SQLException {
							pstmt.setString(1, recordWid[0]);
						}
					});
				}
			}

			// 文件存储模式
			if ("file".equals(store_mode[0])) {
				final String[] filePath = { "" };
				String sql_4 = "SELECT file_path FROM ss_attachment_file WHERE attachment_wid = ?";
				dbUtil.execute(sql_4, new IPreparedResultSetProcessor() {

					public void processResultSet(ResultSet rs)
							throws SQLException {
						if (rs.next()) {
							filePath[0] = rs.getString("file_path");
						}
					}

					public void processPreparedStatement(PreparedStatement pstmt)
							throws SQLException {
						pstmt.setString(1, attachmentId);
					}
				});

				if (filePath[0].length() > 0) {
					File f = new File(filePath[0]);
					if (f.exists()) {
						FileInputStream fin = new FileInputStream(f);
						OutputStream os = response.getOutputStream();
						byte[] buf = new byte[1024];
						int len = -1;
						while ((len = fin.read(buf)) > 0) {
							os.write(buf, 0, len);
						}
						fin.close();
						os.close();
					}
				}
			}

		} catch (SQLException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		}

		return null;
	}

	@RequestMapping(value = "deleteAttachment")
	public String deleteAttachment(
			@RequestParam("attachmentId") String attachmentId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, JSONException {
		JSONObject jsonObj = new JSONObject();

		// 依据attachmentId获取存储路径
		String retMessage = "fail";
		try {
			Map resultMap = (Map) bizobjUtil.query("V_SS_ATTACHMENT_ALL",
					attachmentId);
			// 默认处理文件存储情况的文件删除
			if ((resultMap.get("store_mode")).equals("file")) {
				String filepath = resultMap.get("file_path").toString();
				File f = new File(filepath);
				if (f.isFile()) {
					if (f.delete()) {
						// 文件删除成功后删除数据库数据
						ass.deleteFileAttachmentData(attachmentId);
					}
				}else{
					//文件不存在，直接删除表中数据
					ass.deleteFileAttachmentData(attachmentId);
				}
			} else if (resultMap.get("store_mode").equals("db")) {
				// 删除数据库附件记录
				ass.deleteDbAttachmentData(attachmentId);
			}
			jsonObj.put("success", true);
		} catch (Exception ex) {
			log.error("", ex);
			retMessage = "fail";
			jsonObj.put("success", false);
		}
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(jsonObj.toString());

		return null;
	}

	// 列出某模块所有附件依据业务ID(ServiceID)
	@RequestMapping(value = "listAttachment")
	public String listAttachment(@RequestParam("handler") String handler,
			@RequestParam("serviceId") final String serviceId,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, SQLException, JSONException {
		final JSONObject rev = new JSONObject();
		final JSONArray attachments = new JSONArray();
		try {

			String sql = "SELECT WID,ORIGIN_NAME,FILETYPE,STORE_MODE,SERVICEID FROM SS_ATTACHMENT WHERE SERVICEID=?";
			dbUtil.execute(sql, new IPreparedResultSetProcessor() {

				public void processResultSet(ResultSet rs) throws SQLException {

					while (rs.next()) {
						JSONObject attachment = new JSONObject();
						try {
							attachment.put("attachmentId", rs.getString("WID"));
							attachment.put("originName",
									rs.getString("ORIGIN_NAME"));
							attachment.put("fileType", rs.getString("FILETYPE"));
							attachment.put("store_mode",
									rs.getString("STORE_MODE"));
							attachment.put("serviceId",
									rs.getString("SERVICEID"));
						} catch (JSONException e) {
							log.error("", e);
						}
						attachments.put(attachment);
					}
					log.debug("attachments.length() : " + attachments.length());
				}

				public void processPreparedStatement(PreparedStatement pstmt)
						throws SQLException {
					pstmt.setString(1, serviceId);

				}
			});
		} catch (Exception e) {
			log.error("", e);
		}
		rev.put("attachments", attachments);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(rev.toString());

		return null;
	}

	private ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext appCtx)
			throws BeansException {
		this.appCtx = appCtx;
	}

}
