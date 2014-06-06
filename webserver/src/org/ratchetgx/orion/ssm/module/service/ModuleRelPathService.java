package org.ratchetgx.orion.ssm.module.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.module.dao.ModuleRelPathDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleRelPathService {

    private ModuleRelPathDao moduleRelPathDao;

    @Autowired
    public void setModuleRelPathDao(ModuleRelPathDao moduleRelPathDao) {
        this.moduleRelPathDao = moduleRelPathDao;
    }

    public List<Map<String, String>> list(final String module_id) {
        return moduleRelPathDao.list(module_id);
    }

    public void add(final String module_id, final String path,
            final String indexed, final String memo) {
        moduleRelPathDao.add(module_id, path, indexed, memo);
    }

    public void delete(final List<String> modulePathWids) {
        moduleRelPathDao.delete(modulePathWids);
    }

    public Map<String, String> load(final String moduleWid) {
        return moduleRelPathDao.load(moduleWid);
    }

    public void setAsDefault(final String moduleWid, final String pathWid) {
        moduleRelPathDao.setAsDefault(moduleWid, pathWid);
    }
}
