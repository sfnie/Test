package org.ratchetgx.orion.widgets.photo.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.ratchetgx.orion.widgets.photo.PhotoHandler;
import org.ratchetgx.orion.widgets.photo.PhotoHandlerVisitor;
import org.ratchetgx.orion.widgets.photo.PhotoUploadValidator;
import org.ratchetgx.orion.widgets.photo.PhotoUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 
 * @author hrfan
 * 
 */
@Controller
@RequestMapping(value = "/photo")
public class PhotoHandlerController {

	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PhotoHandlerVisitor phv;

	@RequestMapping(method = RequestMethod.POST)
	public String uploadPhoto(@RequestParam("bh") String bh,
			@RequestParam("value") String value,
			@RequestParam("handler") String handler,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
		MultipartFile multFile = multiRequest.getFile("imgFile");

		// log.info("contentType=" + multFile.getContentType().toLowerCase());

		JSONObject rev = new JSONObject();
		try {
			rev.put("success", false);

			String suffix = "";
			String errorMessage = "";

			// 取得上传的文件名
			String fileName = multFile.getOriginalFilename();
			if (fileName != null && !"".equals(fileName.trim())) {
				
				if (fileName.lastIndexOf(".") >= 0) {
					suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
				}
				
				Map params = new HashMap();
				params.put("bh", bh);
				params.put("suffix", suffix);
				params.put("value", value);
				
				// 上传照片校验
				PhotoUploadValidator puv = phv.getPhotoUploadValidator(handler);
				if (puv != null) {
					try {
						puv.validate(multFile,params);
					} catch (Exception ex) {
						log.error("", ex);
						errorMessage = ex.getMessage();
					}
				}

				if (errorMessage.length() == 0) {
					PhotoHandler ph = phv.getPhotoHander(handler);
					try {
						String retrunValue = ph.upload(multFile
								.getInputStream(),params);
						rev.put("value", retrunValue);
						rev.put("success", true);
					} catch (Exception ex) {
						log.error("", ex);
						errorMessage = ex.getMessage();
					}
				}
				if (errorMessage.length() > 0) {
					rev.put("errorMessage", errorMessage);
				}
			}
		} catch (Exception ex) {
			log.error("", ex);
		}

		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(rev.toString());

		return null;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String viewPhoto(@RequestParam("bh") String bh,
			@RequestParam("value") String value,
			@RequestParam("handler") String handler,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		PhotoHandler ph = phv.getPhotoHander(handler);
		
		Map params = new HashMap();
		params.put("bh", bh);
		params.put("value", value);

		InputStream is;
		try {
			is = ph.download(params);
		} catch (Exception ex) {
			log.error("", ex);
			throw ex;
		}
		
		String suffix = (String) params.get("suffix");
		String contentType = PhotoUtil.getSuffixAndContentTypeMap().get(suffix);
        if(contentType == null){
        	contentType = PhotoUtil.defaultContentType;
        }
        response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		
		OutputStream os = response.getOutputStream();
		byte[] buf = new byte[1024];
		int len = -1;
		while ((len = is.read(buf)) > 0) {
			os.write(buf, 0, len);
		}
		is.close();
		os.close();

		return null;
	}
}
