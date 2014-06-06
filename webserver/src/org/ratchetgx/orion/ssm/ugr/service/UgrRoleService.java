package org.ratchetgx.orion.ssm.ugr.service;

import java.util.List;

import org.ratchetgx.orion.ssm.ugr.dao.UgrRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UgrRoleService {

    private UgrRoleDao ugrRoleDao;

    @Autowired
    public void setUgrRoleDao(UgrRoleDao ugrRoleDao) {
        this.ugrRoleDao = ugrRoleDao;
    }

    public void addRole(String role) {
        ugrRoleDao.addRole(role);
    }

    public void deleteRole(String role) {
        ugrRoleDao.deleteRole(role);
    }

    public void deleteRoles(List<String> roles) {
        ugrRoleDao.deleteRoles(roles);
    }

    public List<String> listRoles() {
        List<String> roles = ugrRoleDao.listRoles();



        return roles;
    }
}
