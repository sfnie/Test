/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

/**
 *
 * @author hrfan
 */
public class Condition {

    private String columnName;
    private RelOperEnum relOper;
    private Object value;

    public Condition(String columnName, RelOperEnum relOper, Object value) {
        this.columnName = columnName;
        this.relOper = relOper;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public RelOperEnum getRelOper() {
        return relOper;
    }

    public void setRelOper(RelOperEnum relOper) {
        this.relOper = relOper;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
