package com.bossien.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mongo.MongoDao;
import com.bossien.adminservice.service.StatisticsService;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.CompanyRanking;
import com.bossien.common.model.UserRanking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MongoDao mongoDao;

    @RequestMapping("/findUserSize")
    public String findUserSize() {
        String result = statisticsService.findUserSize();
        return  result;
    }

    @RequestMapping("/selectRoleSize")
    public String selectRoleSize() {
        String result = statisticsService.selectRoleSize();
        return  result;
    }

    @RequestMapping("/getPersonOrderInCompany")
    public String getCountOfStudent(@RequestParam(required = false) String companyId) {
        /*ResponseEntity<String> forEntity = restTemplate.getForEntity("http://flowtreatment-service/areaTotalScore/queryPersonOrderInCompany?companyId=" + companyId, String.class);
        if(StringUtils.isEmpty(forEntity.getBody()) || forEntity.getBody().length() == 0){
            return JSONObject.toJSONString(new ResponseData(500, "数据为空！", false));
        }
        return  statisticsService.getPersonOrderInCompany(forEntity.getBody());*/
        List<UserRanking> userRankingList = mongoDao.getUserRanking(companyId);
        if(StringUtils.isEmpty(userRankingList) || userRankingList.size() == 0){
            return JSONObject.toJSONString(new ResponseData(500, "数据为空！", false));
        }
        return  statisticsService.getPersonOrderInCompany(userRankingList);
    }

    @RequestMapping("/getCompanyInParent")
    public String queryCompanyInParent(@RequestParam(required = false) String companyId) {
        /*ResponseEntity<String> forEntity = restTemplate.getForEntity("http://flowtreatment-service/areaTotalScore/queryCompanyInParent?companyId=" + companyId, String.class);
        if(StringUtils.isEmpty(forEntity.getBody()) || forEntity.getBody().length() == 0){
            return JSONObject.toJSONString(new ResponseData(500, "数据为空！", false));
        }
        return  statisticsService.queryCompanyInParent(forEntity.getBody());*/
        List<CompanyRanking> companyRankingList = mongoDao.getCompanyRanking(companyId);
        if(StringUtils.isEmpty(companyRankingList) || companyRankingList.size() == 0){
            return JSONObject.toJSONString(new ResponseData(500, "数据为空！", false));
        }
        return  statisticsService.queryCompanyInParent(companyRankingList);
    }

}
