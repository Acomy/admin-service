package com.bossien.adminservice.controller;

import com.bossien.adminservice.service.LuckdrawService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/luckdraw")
public class LuckdrawController {
    @Autowired
    private LuckdrawService luckdrawService;

    @RequestMapping("/selectStuTotalScoreList")
    public String selectRegulatoryCompanyTree(@RequestParam(value = "pid[]", required = false)Long[] pid, @RequestParam(value = "score", required = false)Integer score, @RequestParam(value = "person", required = false)Integer person){
        String result = luckdrawService.selectStuTotalScoreList(pid, score, person);
        return result;
    }
}
