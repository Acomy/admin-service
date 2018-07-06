package com.bossien.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/testInterface")
    public String getCompany(@RequestParam(value = "url") String url){
        ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity(url, String.class);
        if(stringResponseEntity.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "faild";
        }
    }
}
