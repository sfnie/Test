package org.ratchetgx.orion.ssm.ugr.service.select;

import java.util.List;

import org.ratchetgx.orion.ssm.ugr.dao.select.SelectRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectRoleService {

    private SelectRoleDao selectRoleDao;

    @Autowired
    public void setSelectRoleDao(SelectRoleDao selectRoleDao) {
        this.selectRoleDao = selectRoleDao;
    }

    public List<String> listSelectedOfGroup(final String groupWid) {
        return selectRoleDao.listSelectedOfGroup(groupWid);
    }

    public List<String> listUnSelectedOfGroup(final String groupWid) {
        return selectRoleDao.listUnSelectedOfGroup(groupWid);
    }

    public List<String> listSelectedOfUser(final String userWid) {
        return selectRoleDao.listSelectedOfUser(userWid);

    }

    public List<String> listUnSelectedOfUser(final String userWid) {
        return selectRoleDao.listUnSelectedOfUser(userWid);
    }

    public void cancelOfGroup(String groupWid, List<String> roles) {
        selectRoleDao.cancelOfGroup(groupWid, roles);

    }

    public void addOfGroup(String groupWid, List<String> roles) {
        selectRoleDao.addOfGroup(groupWid, roles);

    }

    public void cancelOfUser(String userWid, List<String> roles) {
        selectRoleDao.cancelOfUser(userWid, roles);

    }

    public void addOfUser(String userWid, List<String> roles) {
        selectRoleDao.addOfUser(userWid, roles);

    }
}
