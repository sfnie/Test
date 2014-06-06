/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.widgets.selectrange;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.common.util.DbUtil;

/**
 * 自定义下拉选择范围
 *
 * @author hrfan
 */
public interface ComboboxDefine {

    /**
     * 设置级联时的值
     *
     * @param cascadeValue
     */
    void setCascadeValue(Object cascadeValue);

    /**
     * 设置级联名称
     *
     * @param cascade
     */
    void setCascade(String cascade);

    /**
     * 获取支持的级联名称
     *
     * @return
     */
    List<String> getSupportedCascades();

    /**
     * 设置数据操作工具
     *
     * @param dbUtil
     */
    void setDbUtil(DbUtil dbUtil);

    /**
     * 获取数据
     *
     * @return
     */
    Map getData();

    /**
     * 获取选择范围所有<值,显示文本>
     *
     * @return
     */
    Map<Object, String> getVLs();
}
