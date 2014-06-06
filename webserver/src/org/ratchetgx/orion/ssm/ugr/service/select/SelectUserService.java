package org.ratchetgx.orion.ssm.ugr.service.select;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.ugr.dao.select.SelectUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectUserService {

    private SelectUserDao selectUserDao;

    @Autowired
    public void setSelectUserDao(SelectUserDao selectUserDao) {
        this.selectUserDao = selectUserDao;
    }

    /**
     * groupWid相关的用户
     *
     * @param user
     * @return
     */
    public List<Map<String, String>> listSelectedOfGroup(final String groupWid) {
        return selectUserDao.listSelectedOfGroup(groupWid);
    }

    /**
     * groupWid不相关的用户
     *
     * @param user
     * @return
     */
    public List<Map<String, String>> listUnSelectedOfGroup(final String groupWid) {
        return selectUserDao.listUnSelectedOfGroup(groupWid);
    }

    /**
     * role相关的用户
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listSelectedOfRole(final String role) {
        return selectUserDao.listSelectedOfRole(role);

    }

    /**
     * role不相关的用户
     *
     * @param role
     * @return
     */
    public List<Map<String, String>> listUnSelectedOfRole(final String role) {
        return selectUserDao.listUnSelectedOfRole(role);
    }

    public void cancelOfRole(String role, List<String> userWids) {
        selectUserDao.cancelOfRole(role, userWids);

    }

    public void addOfRole(String role, List<String> userWids) {
        selectUserDao.addOfRole(role, userWids);

    }

    public void cancelOfGroup(String groupWid, List<String> userWids) {
        selectUserDao.cancelOfGroup(groupWid, userWids);

    }

    public void addOfGroup(String groupWid, List<String> userWids) {
        selectUserDao.addOfGroup(groupWid, userWids);

    }
}
