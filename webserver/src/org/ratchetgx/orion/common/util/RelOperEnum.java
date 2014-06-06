/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

/**
 * 关系运算符列表，用以拼接sql语句
 *
 * @author hrfan
 */
public enum RelOperEnum {

    EQUAL("="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),
    LIKE("like");
    private String desc;

    RelOperEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
