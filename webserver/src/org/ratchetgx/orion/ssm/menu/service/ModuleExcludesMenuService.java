/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ratchetgx.orion.ssm.menu.service;

import java.util.List;
import java.util.Map;

import org.ratchetgx.orion.ssm.menu.dao.ModuleExcludesMenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hrfan
 */
@Service
public class ModuleExcludesMenuService {
    
    @Autowired
    private ModuleExcludesMenuDao dao;

    public List<Map<String, String>> list(String parent_id,String menuId) {
        return dao.list(parent_id,menuId);
    }
    
    public List<Map<String, String>> searchlist(String name) {
        return dao.searchlist(name);
    }
    
}
