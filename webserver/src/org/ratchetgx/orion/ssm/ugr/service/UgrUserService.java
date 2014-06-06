package org.ratchetgx.orion.ssm.ugr.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.ugr.dao.UgrUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UgrUserService {

    private UgrUserDao ugrUserDao;

    @Autowired
    public void setUgrUserDao(UgrUserDao ugrUserDao) {
        this.ugrUserDao = ugrUserDao;
    }

    public void addUser(String user) {
        ugrUserDao.addUser(user);
    }

    public void deleteUser(String user) {
        ugrUserDao.deleteUser(user);
    }

    public void deleteUsers(List<String> users) {
        ugrUserDao.deleteUsers(users);
    }

    public List<Map<String, String>> listUsers() {
        List<Map<String, String>> users = ugrUserDao.listUsers();
        return users;
    }
}
