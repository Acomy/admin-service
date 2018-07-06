package com.bossien.adminservice.controller;

import com.bossien.common.model.basics.Resource;
import com.bossien.adminservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @RequestMapping("/selectResourceList")
    public String selectResourceList() {
        String result = resourceService.selectResourceList();
        return  result;
    }

    @RequestMapping("/addUpdateResource")
    public String addUpdateResource(@RequestBody Resource resource){
        String result = resourceService.addUpdateResource(resource);
        return  result;
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "id") Long id) {
        String result = resourceService.delete(id);
        return  result;
    }

    @RequestMapping("/getResource")
    public String getResource(@RequestParam(value = "id") Long id) {
        String result = resourceService.getResource(id);
        return  result;
    }

    @RequestMapping("/selectResourceTree")
    public String selectResourceTree(@RequestParam(value = "roleId") Long roleId) {
        String result = resourceService.selectResourceTree(roleId);
        return  result;
    }
}
