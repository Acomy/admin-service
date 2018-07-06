package com.bossien.adminservice.scheduled;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.UsersMapper;
import com.bossien.common.base.SsoInitialization;
import com.bossien.common.model.basics.UserInfoVo;
import com.bossien.common.model.basics.Users;
import com.bossien.common.util.AESUtils;
import com.bossien.common.util.HttpClientUtils;
import com.bossien.common.util.SearchEntity;
import com.bossien.common.util.SignUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QuestionTask {
    @Autowired
    private UsersMapper usersMapper;

    @Scheduled(initialDelay = 1000, fixedRate = 120000000)
    //@Scheduled(cron = "0 0 *  * * ?")
    public void reportCurrentTime() {
//        SearchEntity searchEntity = new SearchEntity();
//        searchEntity.setPageSize(15000);
//        searchEntity.setPageNum(1);
//        PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
//        List<Users> usersList = usersMapper.selectUserList(searchEntity);
//        for (Users u : usersList) {
//            Map<String, String> params = new HashMap<>();
//            params.put("version", SsoInitialization.VERSION);
//            params.put("appCode", SsoInitialization.APP_CODE);
//
//            UserInfoVo userInfoVo = new UserInfoVo();
//            userInfoVo.setAccount(u.getUsername());
//            userInfoVo.setPassword(u.getPassword());
//            userInfoVo.setName(u.getNickname());
//            userInfoVo.setEmail(u.getEmail());
//            userInfoVo.setMobileNo(u.getTelephone());
//            userInfoVo.setSex(String.valueOf(u.getSex()));
//            userInfoVo.setIsValid(String.valueOf(u.getState()));
//
//            String dataStr = AESUtils.encrypt(JSONObject.toJSONString(userInfoVo), SsoInitialization.APP_KEY);
//            params.put("data", dataStr);
//            String signValue = SignUtils.getSignValue(params, SsoInitialization.APP_KEY);
//            params.put("signValue", signValue);
//
//            String result = HttpClientUtils.doPost(SsoInitialization.REGISTER_URL, params);
//            JSONObject jsonObject = JSONObject.parseObject(result);
//            String code = jsonObject.getString("code");
//            if("000000".equals(code)){
//                String dataJson = jsonObject.getString("data");
//                String dataInfo = AESUtils.decrypt(dataJson, SsoInitialization.APP_KEY);
//                UserInfoVo user = JSONObject.parseObject(dataInfo, UserInfoVo.class);
//                u.setOpenId(user.getOpenId());
//                usersMapper.updateByPrimaryKeySelective(u);
//            }else if("B000022".equals(code)){
//                Map<String, String> accountParams = new HashMap<>();
//                accountParams.put("version", SsoInitialization.VERSION);
//                accountParams.put("appCode", SsoInitialization.APP_CODE);
//                accountParams.put("account", u.getUsername());
//                String signAccount = SignUtils.getSignValue(accountParams, SsoInitialization.APP_KEY);
//                accountParams.put("signValue", signAccount);
//                String resultAccount = HttpClientUtils.doPost(SsoInitialization.ACCOUNT_URL, accountParams);
//                JSONObject object = JSONObject.parseObject(resultAccount);
//                String dataJson = object.getString("data");
//                String dataInfo = AESUtils.decrypt(dataJson, SsoInitialization.APP_KEY);
//                UserInfoVo user = JSONObject.parseObject(dataInfo, UserInfoVo.class);
//                u.setOpenId(user.getOpenId());
//                usersMapper.updateByPrimaryKeySelective(u);
//            }else{
//                String msg = jsonObject.getString("msg");
//                System.out.println(msg+":"+u.getUsername());
//            }
//        }
    }
}
