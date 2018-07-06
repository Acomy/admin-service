package com.bossien.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Company;
import com.bossien.common.util.SearchEntity;
import com.bossien.adminservice.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Value("${file.path}")
    private String filePath;

    @RequestMapping("/selectRegulatoryCompanyTree")
    public String selectRegulatoryCompanyTree(@RequestParam(value = "pid", required = false)Long pid){
        String result = companyService.selectRegulatoryCompanyTree(pid);
        return result;
    }

    @RequestMapping("/selectByCompanyName")
    public String selectRegulatoryCompanyTree(@RequestParam(value = "companyName")String companyName){
        String result = companyService.selectByCompanyName(companyName);
        return result;
    }

    @RequestMapping("/getCompany")
    public String getCompany(@RequestParam(value = "id") Long id){
        String result = companyService.getCompany(id);
        return result;
    }

    @RequestMapping("/addUpdateCompany")
    public String addUpdateCompany(@RequestBody Company company){
        String result = companyService.addUpdateCompany(company);
        return result;
    }

    @RequestMapping("/updateCompanyState")
    public String updateCompanyState(@RequestBody Company company){
        String result = companyService.updateCompanyState(company);
        return result;
    }

    @RequestMapping("/selectByBusinessLevel")
    public String selectByBusinessLevel(){
        String result = companyService.selectByBusinessLevel();
        return result;
    }

    @RequestMapping("/selectALLCompanyTree")
    public String selectALLCompanyTree(@RequestParam(value = "pid", required = false)Long pid){
        String result = companyService.selectALLCompanyTree(pid);
        return result;
    }

    @RequestMapping("/selectCompanyList")
    public String selectCompanyList(@RequestBody SearchEntity searchEntity, @RequestParam(value = "pid[]", required = false) Long[] pid, @RequestParam(value = "state", required = false)Byte state){
        String result = companyService.selectCompanyList(searchEntity, pid, state);
        return result;
    }

    @RequestMapping("/getCompanyAutocomplete")
    public String getCompanyAutocomplete(@RequestParam(value = "keyword") String keyword, @RequestParam(value = "isRegulatory") Integer isRegulatory){
        SearchEntity searchEntity = new SearchEntity();
        searchEntity.setSearchName(keyword);
        searchEntity.setPageNum(1);
        searchEntity.setPageSize(100);
        if(isRegulatory == 1){
            String result = companyService.getRegulatoryCompanyAutocomplete(searchEntity);
            return result;
        }else{
            String result = companyService.getCompanyAutocomplete(searchEntity);
            return result;
        }
    }

    @RequestMapping(value = "/addBatchUploadCompany", method = {RequestMethod.POST})
    public String batchUploadCompany(@RequestParam(value = "file")MultipartFile multiReq, @RequestParam(value = "companyId") Long companyId) throws IOException {
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
        return companyService.addBatchUploadCompany(companyId, filePath +File.separator+ fileName);
    }
}
