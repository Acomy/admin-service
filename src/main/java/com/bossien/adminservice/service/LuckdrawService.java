package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.BusinessMapper;
import com.bossien.adminservice.mapper.CompanyMapper;
import com.bossien.adminservice.mapper.UserRoleMapper;
import com.bossien.adminservice.mapper.UsersMapper;
import com.bossien.adminservice.mongo.MongoDao;
import com.bossien.adminservice.util.PageBean;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.*;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.ImportExecl;
import com.bossien.common.util.MD5Utils;
import com.bossien.common.util.SearchEntity;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class LuckdrawService {
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private MongoDao mongoDao;
    @Autowired
    private UsersMapper usersMapper;


    public String selectStuTotalScoreList(Long[] pid, Integer score, Integer person) {
//        List<Company> companyList = new ArrayList<>();
        List<StuTotalScore> stuTotalScoreList = new ArrayList<>();
//        if(pid.length > 0){
//            for(Long id : pid){
//                Company company = companyMapper.selectByPrimaryKey(id);
//                if(StringUtils.isEmpty(company)){
//                   continue;
//                }
//                companyList.add(company);
//                companyList.addAll(recursionCompany(id));
//            }
//        }
        List<Company> companyList = companyMapper.selectCompanyList(new SearchEntity(), pid, (byte)1);
        if(companyList.size() > 0){
            for(Company company : companyList){
                stuTotalScoreList.addAll(mongoDao.getQueryStuTotalScoreList(company.getId(), score));
            }
        }

        stuTotalScoreList = getRandomList(stuTotalScoreList, person);
        for(StuTotalScore stuTotalScore : stuTotalScoreList){
            Users users = usersMapper.selectByPrimaryKey(stuTotalScore.getUserid());
            if(StringUtils.isEmpty(users)){
                stuTotalScore.setUserName(users.getNickname());
                stuTotalScore.setPhone(users.getTelephone());
            }
        }
        return JSONObject.toJSONString(new ResponseData(200, "请求成功！", stuTotalScoreList));
    }

    public List<Company> recursionCompany(Long companyId){
        List<Company> companyList = new ArrayList<>();
        List<Company> list = companyMapper.selectCompanyTreeByPid(companyId, 0, (byte)1);
        if(list.size() > 0){
            companyList.addAll(list);
            for(Company company : list){
                if(company.getIsRegulatory() == 1){
                    companyList.addAll(recursionCompany(company.getId()));
                }
            }
        }
        return companyList;
    }

    /**
     * 从list中随机抽取若干不重复元素
     *
     * @param paramList:被抽取list
     * @param count:抽取元素的个数
     * @return:由抽取元素组成的新list
     */
    public static List getRandomList(List paramList,int count){
        if(paramList.size()<count){
            return paramList;
        }
        Random random=new Random();
        List<Integer> tempList=new ArrayList<Integer>();
        List<Object> newList=new ArrayList<Object>();
        int temp=0;
        for(int i=0;i<count;i++){
            temp=random.nextInt(paramList.size());//将产生的随机数作为被抽list的索引
            if(!tempList.contains(temp)){
                tempList.add(temp);
                newList.add(paramList.get(temp));
            }
            else{
                i--;
            }
        }
        return newList;
    }

}
