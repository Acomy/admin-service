package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.BusinessMapper;
import com.bossien.adminservice.mapper.UserRoleMapper;
import com.bossien.adminservice.mapper.UsersMapper;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Business;
import com.bossien.common.model.basics.Company;
import com.bossien.common.model.basics.UserRole;
import com.bossien.common.model.basics.Users;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.ImportExecl;
import com.bossien.common.util.MD5Utils;
import com.bossien.common.util.SearchEntity;
import com.bossien.adminservice.mapper.CompanyMapper;
import com.bossien.adminservice.util.PageBean;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class CompanyService {
    @Value("${roleId.chaojiguanli}")
    private String chaojiguanli;
    @Value("${roleId.yunying}")
    private String yunying;
    @Value("${roleId.jianguan}")
    private String jianguan;
    @Value("${roleId.danweiguanli}")
    private String danweiguanli;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    private IdGenerator idWorker = new IdGenerator(0, 0);;

    public String getCompany(Long id) {
        Company company = companyMapper.selectByPrimaryKey(id);
        if(!StringUtils.isEmpty(company.getPid())){
            Company company1 = companyMapper.selectByPrimaryKey(company.getPid());
            company.setParentName(company1.getCompanyName());
        }
        return  JSONObject.toJSONString(new ResponseData(200, "请求成功！", company));
    }

    public String selectByBusinessLevel(){
        String result = "";
        try{
            List<Business> businessList = businessMapper.selectByBusinessLevel();
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", businessList));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    public String selectRegulatoryCompanyTree(Long pid) {
        String result = "";
        try {
            String username = request.getHeader("username");
            Users users = usersMapper.selectByUsername(username, null);
            if(!StringUtils.isEmpty(users)){

                UserRole userRole = userRoleMapper.selectByUserId(users.getId()).get(0);
                //PageHelper.startPage(1, 1000);
                List<Company> companyList  = new ArrayList<>();

                if(!StringUtils.isEmpty(pid)){
                    companyList = companyMapper.selectCompanyTreeByPid(pid, 1, null);

                }else{
                    Company company = companyMapper.selectByPrimaryKey(users.getCompanyId());
                    if(StringUtils.isEmpty(users.getCompanyId()) || chaojiguanli.equals(String.valueOf(userRole.getRoleId())) || yunying.equals(String.valueOf(userRole.getRoleId())) || company.getPid() == 0){
                        companyList = companyMapper.selectCompanyTreeByPid(null, 1, null);
                    }else{
                        companyList = companyMapper.selectCompanyTreeById(users.getCompanyId(), 1, null);
                        companyList.addAll(recursionCompany(users.getCompanyId(), 1));
                    }
                }
//                for(Company company : companyList){
//                    int size = companyMapper.selectRegulatoryCompanyTreeSize(company.getId());
//                    if(size > 0){
//                        company.setIsParent("true");
//                    }
//                }

                result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", companyList));
            }
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public List<Company> recursionCompany(Long companyId, int isRegulatory){
        List<Company> companyList = new ArrayList<>();
        List<Company> list = companyMapper.selectCompanyTreeByPid(companyId, isRegulatory, (byte)1);
        if(!StringUtils.isEmpty(list) && list.size()>0){
            companyList.addAll(list);
            for(Company company : list){
                if(!StringUtils.isEmpty(company) && company.getIsRegulatory() == 1){
                    companyList.addAll(recursionCompany(company.getId(), isRegulatory));
                }
            }
        }

        return companyList;
    }

    public String selectALLCompanyTree(Long pid) {
        String result = "";
        try {
            String username = request.getHeader("username");
            Users users = usersMapper.selectByUsername(username, null);
            List<Company> companyList = new ArrayList<>();
            if(!StringUtils.isEmpty(users)){
                UserRole userRole = userRoleMapper.selectByUserId(users.getId()).get(0);

                //PageHelper.startPage(1, 20000);
                if(!StringUtils.isEmpty(pid)){
                    companyList = companyMapper.selectCompanyTreeByPid(pid, 2, (byte)1);

                }else{
                    Company company = companyMapper.selectByPrimaryKey(users.getCompanyId());
                    if(StringUtils.isEmpty(users.getCompanyId()) || chaojiguanli.equals(String.valueOf(userRole.getRoleId())) || yunying.equals(String.valueOf(userRole.getRoleId())) || company.getPid() == 0){
                        companyList = companyMapper.selectCompanyTreeByPid(null, 2, (byte)1);
                    }else{
                        companyList = companyMapper.selectCompanyTreeById(users.getCompanyId(), 2, (byte)1);
                        companyList.addAll(recursionCompany(users.getCompanyId(), 2));
                    }
                }
            }else{
                companyList = companyMapper.selectCompanyTreeByPid(pid, 2, (byte)1);
            }

//            for(Company company : companyList){
//                if(company.getIsRegulatory() == 1){
//                    int size = companyMapper.selectALLCompanyTreeSize(company.getId());
//                    if(size > 0){
//                        company.setIsParent("true");
//                    }
//                }
//            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", companyList));
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectByCompanyName(String companyName){

        try{
            Company company = companyMapper.selectByCompanyName(companyName);
            if(StringUtils.isEmpty(company)){
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }else{
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", false));
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }
    }

    public String addUpdateCompany(Company company){
        String result = "";
        try {
            if(StringUtils.isEmpty(company.getId())){
                if(company.getIsRegulatory() != 0 && company.getIsRegulatory() != 1){
                    return JSONObject.toJSONString(new ResponseData(500, "非法请求！", false));
                }

                Company company1 = companyMapper.selectByCompanyName(company.getCompanyName());
                if(!StringUtils.isEmpty(company1)){
                    return JSONObject.toJSONString(new ResponseData(200, "单位已重复！", false));
                }

                if(StringUtils.isEmpty(company.getPid())){
                    return JSONObject.toJSONString(new ResponseData(500, "非法请求！", false));
                }
                Company c = companyMapper.selectByPrimaryKey(company.getPid());
                if(StringUtils.isEmpty(c)){
                    return JSONObject.toJSONString(new ResponseData(500, "非法请求！", false));
                }

                String maxCode = companyMapper.getCompanyCodeByPid(c.getId(), company.getIsRegulatory());

                if(StringUtils.isEmpty(maxCode)){
                    if(company.getIsRegulatory() == 0){
                        company.setCode(String.valueOf(c.getCode())+"001");
                    }else if(company.getIsRegulatory() == 1){
                        company.setCode(String.valueOf(c.getCode())+"01");
                    }
                }else{
                    int code = Integer.valueOf(maxCode)+1;
                    String codeStr = String.format("%"+maxCode.length()+"d", code).replace(" ", "0");
                    company.setCode(codeStr);
                }
                List<Company> company2List = companyMapper.selectByCompanyCode(company.getCode());
                if(company2List.size() > 0){
                    return JSONObject.toJSONString(new ResponseData(500, "单位Code重复，请联系管理员！", false));
                }

                long companyId =  Long.valueOf(String.valueOf(idWorker.nextId()).substring(9));
                Company company2 = companyMapper.selectByPrimaryKey(companyId);
                if(!StringUtils.isEmpty(company2)){
                    companyId =  Long.valueOf(String.valueOf(idWorker.nextId()).substring(9));
                }

                company.setId(companyId);

                company.setState((byte)1);
                company.setCreateUser(request.getHeader("username"));
                company.setCreateDate(new Date());
                company.setIsRegulatory(company.getIsRegulatory());
                int num = companyMapper.insertSelective(company);
                if(num > 0){
                    long userId =  idWorker.nextId();
                    long roleId =  idWorker.nextId();

                    UserRole userRole = new UserRole();
                    userRole.setId(roleId);
                    userRole.setUserId(userId);
                    if(company.getIsRegulatory() == 0){
                        //单位管理角色ID
                        userRole.setRoleId(Long.valueOf(danweiguanli));
                    }else if(company.getIsRegulatory() == 1){
                        //监管管理角色ID
                        userRole.setRoleId(Long.valueOf(jianguan));
                    }

                    userRole.setCreateUser(request.getHeader("username"));
                    userRole.setCreateDate(new Date());
                    userRoleMapper.insertSelective(userRole);

                    Users users = new Users();
                    users.setId(userId);
                    users.setUsername(company.getCode());
                    users.setPassword(MD5Utils.encode("123456"));
                    users.setNickname(company.getCompanyName());
                    users.setRegistType((byte)2);
                    users.setState((byte)1);
                    users.setCompanyId(companyId);
                    users.setCreateUser(request.getHeader("username"));
                    users.setCreateDate(new Date());
                    usersMapper.insertSelective(users);
                }
            }else{
                Company c = companyMapper.selectByPrimaryKey(company.getId());
                if(!StringUtils.isEmpty(c)){
                    c.setContacter(company.getContacter());
                    c.setAddress(company.getAddress());
                    c.setPostCode(company.getPostCode());
                    c.setContacterTelephone(company.getContacterTelephone());
                    c.setPeopleNumber(company.getPeopleNumber());
                    if(!StringUtils.isEmpty(company.getCompanyName())){
                        c.setCompanyName(company.getCompanyName());
                    }
                    //c.setCode(company.getCode());

                    if(!c.getPid().equals(company.getPid())) {
                        c.setPid(company.getPid());
                        //修改单位层级修改单位code
                        Map<String, String> userNameMap = new HashMap<String, String>();
                        userNameMap.put("oldUserName",c.getCode());
                        Company parent = companyMapper.selectByPrimaryKey(company.getPid());
                        if(StringUtils.isEmpty(parent)){
                            return JSONObject.toJSONString(new ResponseData(500, "非法请求！", false));
                        }

                        String maxCode = companyMapper.getCompanyCodeByPid(parent.getId(), c.getIsRegulatory());

                        if(StringUtils.isEmpty(maxCode)){
                            if(c.getIsRegulatory() == 0){
                                c.setCode(String.valueOf(parent.getCode())+"001");
                            }else if(c.getIsRegulatory() == 1){
                                c.setCode(String.valueOf(parent.getCode())+"01");
                            }
                        }else{
                            int code = Integer.valueOf(maxCode)+1;
                            String codeStr = String.format("%"+maxCode.length()+"d", code).replace(" ", "0");
                            c.setCode(codeStr);
                        }
                        List<Company> company2List = companyMapper.selectByCompanyCode(c.getCode());
                        if(company2List.size() > 0){
                            return JSONObject.toJSONString(new ResponseData(500, "单位Code重复，请联系管理员！", false));
                        }

                        userNameMap.put("newUserName",c.getCode());

                        //修改单位层级会修改单位code同时修改单位管理员的用户名
                        usersMapper.updateUserName(userNameMap);
                    }

                    if(!StringUtils.isEmpty(company.getBusinessId())){
                        c.setBusinessId(company.getBusinessId());
                    }
                    c.setOperUser(request.getHeader("username"));
                    c.setOperDate(new Date());
                    companyMapper.updateByPrimaryKey(c);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String updateCompanyState(Company company){
        String result = "";
        try {
            if(!StringUtils.isEmpty(company.getId())){
                Company c = companyMapper.selectByPrimaryKey(company.getId());
                if(!StringUtils.isEmpty(c)){
                    if(c.getIsRegulatory() == 1){
                        return JSONObject.toJSONString(new ResponseData(500, "监管单位不能废弃！", false));
                    }
                    c.setState(company.getState());
                    c.setOperUser(request.getHeader("username"));
                    c.setOperDate(new Date());
                    int num = companyMapper.updateByPrimaryKey(c);

                    if(num > 0){
                        usersMapper.updateCompanyUserState(c.getId(), company.getState());
                    }
                }else{
                    return JSONObject.toJSONString(new ResponseData(500, "单位不存在！", false));
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectCompanyList(SearchEntity searchEntity, Long[] pid, @RequestParam(value = "state", required = false)Byte state) {
        String result = "";
        try {
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Company> companyList = companyMapper.selectCompanyList(searchEntity, pid, state);
            if(companyList!=null && companyList.size()>0){
                for(Company company : companyList){
                    int userSize = usersMapper.selectUserSizeByCompanyId(company.getId());
                    company.setRemark(String.valueOf(userSize));
                    Company c = companyMapper.selectByPrimaryKey(company.getPid());
                    if(!StringUtils.isEmpty(c)){
                        company.setParentName(c.getCompanyName());
                    }
                }
            }
            PageBean<Company> pageBean = new PageBean<>(companyList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    public String getCompanyAutocomplete(SearchEntity searchEntity) {
        String result = "";
        try {
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Company> companyList = companyMapper.selectCompanyList(searchEntity, null, (byte)1);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", companyList)).replaceAll("companyName", "title");
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    public String getRegulatoryCompanyAutocomplete(SearchEntity searchEntity) {
        String result = "";
        try {
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Company> companyList = companyMapper.getRegulatoryCompanyAutocomplete(searchEntity);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", companyList)).replaceAll("companyName", "title");
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String addBatchUploadCompany(Long pid, String filePath) {
        String result = "";
        try {
            Company parent = companyMapper.selectByPrimaryKey(pid);
            if(StringUtils.isEmpty(parent)){
                return JSONObject.toJSONString(new ResponseData(500, "单位不存在！", false));
            }

            ImportExecl poi = new ImportExecl();
            List<List<String>> list = poi.read(filePath);

            List<String> cell = list.get(0);
            if(!"单位名称".equals(cell.get(0)) && !"联系电话".equals(cell.get(1)) && !"联系地址".equals(cell.get(2)) && !"所属行业".equals(cell.get(3))){
                return JSONObject.toJSONString(new ResponseData(500, "所传模板不正确！", false));
            }

            StringBuilder questionStr = new StringBuilder();
            questionStr.append("导入成功，重复单位:");
            if (list != null) {
                for (int i = 1; i < list.size(); i++) {
                    List<String> cellList = list.get(i);
                    if(StringUtils.isEmpty(cellList.get(0))){
                        continue;
                    }

                    Company c = companyMapper.selectByCompanyName(cellList.get(0));
                    if(!StringUtils.isEmpty(c)){
                        questionStr.append(cellList.get(0));
                        questionStr.append(";");
                        continue;
                    }
                    Company company = new Company();
                    long companyId =  Long.valueOf(String.valueOf(idWorker.nextId()).substring(9));
                    long userId =  idWorker.nextId();
                    long roleId =  idWorker.nextId();

                    Company c1 = companyMapper.selectByPrimaryKey(companyId);
                    if(!StringUtils.isEmpty(c1)){
                        companyId = Long.valueOf(String.valueOf(idWorker.nextId()).substring(9));
                        Company c2 = companyMapper.selectByPrimaryKey(companyId);
                        if(!StringUtils.isEmpty(c2)){
                            companyId = Long.valueOf(String.valueOf(idWorker.nextId()).substring(9));
                        }
                    }
                    company.setId(companyId);
                    String maxCode = companyMapper.getCompanyCodeByPid(pid, (byte)0);
                    if(StringUtils.isEmpty(maxCode)){
                        company.setCode(String.valueOf(parent.getCode())+"001");
                    }else{
                        int code = Integer.valueOf(maxCode)+1;
                        String codeStr = String.format("%"+maxCode.length()+"d", code).replace(" ", "0");
                        company.setCode(codeStr);
                    }

                    company.setCompanyName(cellList.get(0));
                    company.setState((byte)1);
                    company.setIsRegulatory((byte)0);
                    company.setPid(pid);
                    company.setCreateDate(new Date());
                    company.setCreateUser(request.getHeader("username"));

                    company.setContacterTelephone(cellList.get(1));
                    company.setAddress(cellList.get(2));
                    Business business = businessMapper.selectByBusinessName(cellList.get(3));
                    if(!StringUtils.isEmpty(business)){
                        company.setBusinessId(business.getId());
                    }
                    if(cellList.size() > 4){
                        if(!StringUtils.isEmpty(cellList.get(4))){
                            if(cellList.get(4).equals("厅直单位")){
                                company.setTypeId((byte)1);
                            }else if(cellList.get(4).equals("县市水务局")){
                                company.setTypeId((byte)2);
                            }else if(cellList.get(4).equals("施工单位")){
                                company.setTypeId((byte)3);
                            }else{
                                company.setTypeId((byte)0);
                            }
                        }
                    }

                    int num = companyMapper.insertSelective(company);
                    if(num > 0){
                        Users u = usersMapper.selectByUsername(company.getCode(), null);
                        if(StringUtils.isEmpty(u)){
                            UserRole userRole = new UserRole();
                            userRole.setId(roleId);
                            userRole.setUserId(userId);
                            userRole.setRoleId(Long.valueOf("418069886418288640"));  //单位管理角色ID
                            userRole.setCreateUser("admin");
                            userRole.setCreateDate(new Date());
                            userRoleMapper.insertSelective(userRole);

                            Users users = new Users();
                            users.setId(userId);
                            users.setUsername(company.getCode());
                            users.setPassword(MD5Utils.encode("123456"));
                            users.setNickname(company.getCompanyName());
                            users.setRegistType((byte)1);
                            users.setState((byte)1);
                            users.setCompanyId(companyId);
                            users.setCreateUser("admin");
                            users.setCreateDate(new Date());
                            usersMapper.insertSelective(users);
                        }
                    }
                }
                if(questionStr.length() > 10){
                    result = JSONObject.toJSONString(new ResponseData(200, questionStr.toString(), false));
                }else{
                    result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
                }
            }
        } catch(Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }
}
