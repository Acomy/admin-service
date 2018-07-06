package com.bossien.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.redis.RedisDao;
import com.bossien.adminservice.service.UserService;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Users;
import com.bossien.common.util.SearchEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisDao redisDao;

    @Value("${file.path}")
    private String filePath;

    @RequestMapping(value = "/addBatchUploadUser", method = {RequestMethod.POST})
    public String batchUploadCompany(@RequestParam(value = "file")MultipartFile multiReq, @RequestParam(value = "companyId") Long companyId) throws FileNotFoundException {
        String uploadFilePath = multiReq.getOriginalFilename();
        // 截取上传文件的后缀
        String uploadFileSuffix = uploadFilePath.substring(
                uploadFilePath.indexOf('.') + 1, uploadFilePath.length());
        String fileName = UUID.randomUUID() +"."+ uploadFileSuffix;
        try {
            File dest = new File(filePath +File.separator+ fileName);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            multiReq.transferTo(dest);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userService.addBatchUploadUser(companyId, filePath+ File.separator + fileName);
    }

    @RequestMapping("/addUpdateUser")
    public String addUpdateUser(@RequestBody Users users, @RequestParam(value = "roleIds") String roleIds) {
        String result = userService.addUpdateUser(users, roleIds);
        return  result;
    }

    @RequestMapping("/updateUser")
    public String updateUser(@RequestBody Users users, @RequestParam(value = "roleIds", required = false) String roleIds) {
        String result = userService.updateUser(users);
        return  result;
    }

    @RequestMapping("/updateUserState")
    public String updateUserState(@RequestBody Users users) {
        String result = userService.updateUserState(users);
        return  result;
    }

    @RequestMapping("/selectUserList")
    public String selectUserList(@RequestBody SearchEntity searchEntity) {
        String result = userService.selectUserList(searchEntity);
        return  result;
    }

    @RequestMapping("/selectAccountList")
    public String selectAccountList(@RequestBody SearchEntity searchEntity) {
        String result = userService.selectAccountList(searchEntity);
        return  result;
    }


    @RequestMapping("/selectByCompanyIdList")
    public String selectByCompanyIdList(@RequestBody SearchEntity searchEntity, @RequestParam(value = "companyId[]", required = false)Long[] companyId){
        String result = userService.selectByCompanyIdList(searchEntity, companyId);
        return  result;
    }

    @RequestMapping("/findUserPassword")
    public String findUserPassword(@RequestParam(value = "username") String username, @RequestParam(value = "shibboleth") String shibboleth) {
        String result = userService.findUserPassword(username, shibboleth);
        return  result;
    }

    @RequestMapping("/selectByUsername")
    public String selectByUsername(@RequestParam(value = "username") String username, @RequestParam(value = "id", required = false) Long id) {
        String result = userService.selectByUsername(username, id);
        return  result;
    }

    @RequestMapping("/selectByTelephone")
    public String selectByTelephone(@RequestParam(value = "telephone") String telephone, @RequestParam(value = "id", required = false) Long id) {
        String result = userService.selectByTelephone(telephone, id);
        return  result;
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam(value = "ids[]") Long[] ids) {
        String result = userService.delete(ids);
        return  result;
    }

    @RequestMapping("/getUser")
    public String getUser(@RequestParam(value = "id") Long id) {
        String result = userService.getUser(id);
        return  result;
    }

    @RequestMapping("/updateSignOut")
    public ResponseData index(HttpServletRequest request) {
        String username = request.getHeader("Username");
        String token = request.getHeader("Authorization");
        String usernameRedis = redisDao.getForValueValue(username);
        if(token.equals(usernameRedis)){
            redisDao.delete(username);
        }
        return new ResponseData(200, "退出成功！", true);
    }

    @RequestMapping("/updateMergerCompany")
    public String getUser(@RequestParam(value = "companyId") Long companyId, @RequestParam(value = "mergerIds[]") Long[] mergerIds) {
        String result = userService.updateMergerCompany(companyId, mergerIds);
        return  result;
    }
}
