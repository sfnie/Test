package org.ratchetgx.orion.ssm.ugr.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.ugr.dao.UgrGroupDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UgrGroupService {

    private UgrGroupDao ugrGroupDao;

    @Autowired
    public void setUgrGroupDao(UgrGroupDao ugrGroupDao) {
        this.ugrGroupDao = ugrGroupDao;
    }

    public void addGroup(String groupName) {
        ugrGroupDao.addGroup(groupName);
    }

    public void deleteGroup(String group) {
        ugrGroupDao.deleteGroup(group);
    }

    public void deleteGroups(List<String> groupWids) {
        ugrGroupDao.deleteGroups(groupWids);
    }

    public List<Map<String, String>> listGroups() {
        List<Map<String, String>> groups = ugrGroupDao.listGroups();
        return groups;
    }
}
