/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.photo.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.ratchetgx.orion.common.SsfwUtil;
import org.ratchetgx.orion.widgets.photo.PhotoHandler;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author hrfan
 */
@Component(value = "PhotoHandlerImpl")
public class PhotoHandlerImpl implements PhotoHandler {
	

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    public String upload(InputStream is,Map params) throws Exception {
    	Object value = params.get("value");
        String bh = (String) params.get("bh");
        String suffix = (String) params.get("suffix");
    	
        //权限验证
        if(!SsfwUtil.getCurrentBh().equals(bh)){
            throw new Exception("无权限上传该照片。");
        }
        
        File dir = new File(bh);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String retrunValue = String.valueOf(new Date().getTime());
        File f = new File(bh + File.separatorChar + retrunValue + "." + suffix);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = is.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            is.close();
            fos.close();
        } catch (IOException ex) {
            log.error("", ex);
            retrunValue = null;
        }
        log.debug(retrunValue + "." + suffix);
        return retrunValue + "." + suffix;
    }

    public InputStream download(Map params) throws Exception {
    	Object value = params.get("value");
        String bh = (String) params.get("bh");
    	
        //权限验证
        if(!SsfwUtil.getCurrentBh().equals(bh)){
            throw new Exception("无权限浏览该照片。");
        }
        
        File f = new File(bh + File.separatorChar + value);
        if (!f.exists()) {
            f = new File((String)SsfwUtil.getValue(SsfwUtil.WEBAPP_ABSOLUTE_PATH) + File.separatorChar  + "resources" + File.separatorChar + "image" + File.separatorChar + "noPhoto.gif");
        }
        
        if (!f.exists()) {
            throw new Exception(f.getAbsolutePath() + "不存在");
        }
        
        String fileName = f.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        params.put("suffix", suffix);
        
        try {
            FileInputStream fis = new FileInputStream(f);
            return fis;
        } catch (IOException ex) {
            throw ex;
        }
    }

}
