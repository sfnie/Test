/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.photo;

/**
 *
 * @author hrfan
 */
public interface PhotoFileHandler extends PhotoHandler {

    /**
     * 获取文件名称（带后缀名）
     *
     * @return
     */
    String getFileName();

    /**
     * 获取文件所在目录路径
     *
     * @return
     */
    String getDirectory();
}
