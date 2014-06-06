package org.ratchetgx.orion.ssm.module.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.module.dao.ModuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleService {

    private ModuleDao moduleDao;

    @Autowired
    public void setModuleDao(ModuleDao moduleDao) {
        this.moduleDao = moduleDao;
    }

    public List<Map<String, String>> list(final String parentModuleWid) {
        return moduleDao.list(parentModuleWid);
    }

    public void delete(final String moduleWid) {
        moduleDao.delete(moduleWid);
    }

    public void add(final String name, final String memo,
            final String parentModuleWid) {
        moduleDao.add(name, memo, parentModuleWid);
    }

    public void edit(final String moduleWid, final String name,
            final String memo) {
        moduleDao.edit(moduleWid, name, memo);
    }

    public Map<String, String> load(final String moduleWid) {
        return moduleDao.load(moduleWid);
    }
}
