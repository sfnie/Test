package org.ratchetgx.orion.ssm.ugr.service.select;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.ugr.dao.select.SelectGroupDao;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectGroupService {

    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private SelectGroupDao selectGroupDao;

    @Autowired
    public void setSelectGroupDao(SelectGroupDao selectGroupDao) {
        this.selectGroupDao = selectGroupDao;
    }

    public List<Map<String, String>> listSelectedOfRole(String role) {
        return selectGroupDao.listSelectedOfRole(role);
    }

    public List<Map<String, String>> listUnSelectedOfRole(String role) {
        return selectGroupDao.listUnSelectedOfRole(role);
    }

    public List<Map<String, String>> listSelectedOfUser(String userWid) {
        return selectGroupDao.listSelectedOfUser(userWid);
    }

    public List<Map<String, String>> listUnSelectedOfUser(String userWid) {
        return selectGroupDao.listUnSelectedOfUser(userWid);
    }

    public void cancelOfRole(String role, List<String> groupWids) {
        selectGroupDao.cancelOfRole(role, groupWids);

    }

    public void addOfRole(String role, List<String> groupWids) {
        selectGroupDao.addOfRole(role, groupWids);

    }

    public void cancelOfUser(String userWid, List<String> groupWids) {
        selectGroupDao.cancelOfUser(userWid, groupWids);

    }

    public void addOfUser(String userWid, List<String> groupWids) {
        selectGroupDao.addOfUser(userWid, groupWids);

    }
}
