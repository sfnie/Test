/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author hrfan
 */
public class ConditionGroup {

    private AndOrEnum andOr = AndOrEnum.AND;
    private List<Condition> conditions = new ArrayList<Condition>();
    private List<ConditionGroup> conditionGroups = new ArrayList<ConditionGroup>();
    
    public ConditionGroup() {
    }

    public ConditionGroup(AndOrEnum andOr) {
        this.andOr = andOr;
    }

    public AndOrEnum getAndOr() {
        return andOr;
    }

    public void setAndOr(AndOrEnum andOr) {
        this.andOr = andOr;
    }

    public List<Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    public List<ConditionGroup> getConditionGroups() {
        return Collections.unmodifiableList(conditionGroups);
    }

    public void addConditionGroups(ConditionGroup conditionGroup) {
        this.conditionGroups.add(conditionGroup);
    }
}
