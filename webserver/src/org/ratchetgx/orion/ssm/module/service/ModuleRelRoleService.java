package org.ratchetgx.orion.ssm.module.service;

import java.util.List;

import org.ratchetgx.orion.ssm.module.dao.ModuleRelRoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleRelRoleService {

    private ModuleRelRoleDao moduleRelRoleDao;

    @Autowired
    public void setModuleRelRoleDao(ModuleRelRoleDao moduleRelRoleDao) {
        this.moduleRelRoleDao = moduleRelRoleDao;
    }

    public void add(final String module_id, final String role) {
        moduleRelRoleDao.add(module_id, role);
    }

    public void delete(final String module_id, final List<String> roles) {
        moduleRelRoleDao.delete(module_id, roles);
    }

    public List<String> listSelected(final String module_id) {
        return moduleRelRoleDao.listSelected(module_id);
    }

    public List<String> listUnSelected(final String module_id) {
        return moduleRelRoleDao.listUnSelected(module_id);
    }
}
