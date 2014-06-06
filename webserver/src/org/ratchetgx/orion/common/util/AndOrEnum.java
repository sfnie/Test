/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

/**
 * and or 用以拼接sql字符串
 *
 * @author hrfan
 */
public enum AndOrEnum {

    AND("and"),
    OR("or");
    private String desc;

    AndOrEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
