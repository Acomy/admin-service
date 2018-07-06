package com.bossien.adminservice.controller;

import com.bossien.common.model.basics.Menu;
import com.bossien.adminservice.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @RequestMapping("/selectMenuList")
    public String selectMenuList(){
        String result = menuService.selectMenuList();
        return result;
    }

    @RequestMapping("/getMenu")
    public String getMenu(@RequestParam(value = "id") Long id){
        String result = menuService.getMenu(id);
        return result;
    }

    @RequestMapping("/findMenuList")
    public String findMenuList(){
        String result = menuService.initMenu();
        return result;
    }

    @RequestMapping("/addMenu")
    public String addMenu(@RequestBody Menu menu){
        String result = menuService.addMenu(menu);
        return result;
    }

    @RequestMapping("/deleteMenu")
    public String deleteMenu(@RequestParam(value = "id") Long id){
        String result = menuService.deleteMenu(id);
        return result;
    }
}
