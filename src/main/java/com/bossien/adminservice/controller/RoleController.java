package com.bossien.adminservice.controller;

import com.bossien.common.model.basics.Role;
import com.bossien.common.util.SearchEntity;
import com.bossien.adminservice.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @RequestMapping("/selectRoleList")
    public String selectRoleList(@RequestBody SearchEntity searchEntity) {
        String result = roleService.selectRoleList(searchEntity);
        return  result;
    }

    @RequestMapping("/selectRoleAllList")
    public String selectRoleAllList() {
        String result = roleService.selectRoleAllList();
        return  result;
    }

    @RequestMapping("/updateRoleExpire")
    public String updateRoleExpire(@RequestParam(value = "roleId") Long roleId) {
        String result = roleService.updateRoleExpire(roleId);
        return  result;
    }

    @RequestMapping("/addUpdateRole")
    public String addUpdateRole(@RequestBody Role role) {
        String result = roleService.addUpdateRole(role);
        return  result;
    }

    @RequestMapping("/deleteRole")
    public String deleteRole(@RequestParam(value = "ids[]") Long[] ids) {
        String result = roleService.delete(ids);
        return  result;
    }

    @RequestMapping("/getRole")
    public String getRole(@RequestParam(value = "id") Long id) {
        String result = roleService.getRoleJson(id);
        return  result;
    }

    @RequestMapping("/updateRoleResource")
    public String updateRoleResource(@RequestParam(value = "roleId") Long roleId, @RequestParam(value = "resourceIds[]") String[] resourceIds) {
        String result = roleService.updateRoleResource(roleId, resourceIds);
        return  result;
    }
}
