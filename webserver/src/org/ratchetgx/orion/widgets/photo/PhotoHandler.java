/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.photo;

import java.io.InputStream;
import java.util.Map;

/**
 * 上传下载处理
 *
 * @author hrfan
 */
public interface PhotoHandler {

    /**
     * 上传处理
     *
     * @param is
     * @param params
     * @return 照片实际值
     */
    String upload(InputStream is,Map params) throws Exception;

    /**
     * 文件读取
     *
     * @param params
     * @return
     */
    InputStream download(Map params) throws Exception;
}
