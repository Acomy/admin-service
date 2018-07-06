package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.activemq.Producer;
import com.bossien.adminservice.mapper.CompanyMapper;
import com.bossien.adminservice.mapper.UsersMapper;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Company;
import com.bossien.common.model.basics.Role;
import com.bossien.common.model.basics.UserRole;
import com.bossien.common.model.basics.Users;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.ImportExecl;
import com.bossien.common.util.MD5Utils;
import com.bossien.common.util.SearchEntity;
import com.bossien.adminservice.mapper.UserRoleMapper;
import com.bossien.adminservice.util.PageBean;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Value("${roleId.xueyuan}")
    private String xueyuan;

    @Value("${constant.shibboleth}")
    private String shibboleth;

    @Value("${platform.code}")
    private String platformCode;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Producer producer;

    @Autowired
    private UsersMapper userDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private CompanyMapper companyMapper;

//    private Destination destination = new ActiveMQQueue("test.queue");
//
    public void sendMessage(){
        //producer.send(destination, "测试消息");
    }

    public String addBatchUploadUser(Long companyId, String filePath) {
        String result = "";
        try {
            if(StringUtils.isEmpty(companyId)){
                return JSONObject.toJSONString(new ResponseData(500, "单位ID不能为空！", false));
            }
            Company company = companyMapper.selectByPrimaryKey(companyId);
            if(StringUtils.isEmpty(company)){
                return JSONObject.toJSONString(new ResponseData(500, "所传单位不存在！", false));
            }
            ImportExecl poi = new ImportExecl();
            List<List<String>> list = poi.read(filePath);
            if(list.size() < 1){
                return JSONObject.toJSONString(new ResponseData(500, "所传模板不正确！", false));
            }
            List<String> cell = list.get(0);
            if(!"手机号码".equals(cell.get(0)) && !"密码".equals(cell.get(1)) && !"姓名".equals(cell.get(2)) && !"邮箱".equals(cell.get(3)) && !"性别".equals(cell.get(4))){
                return JSONObject.toJSONString(new ResponseData(500, "所传模板不正确！", false));
            }

            StringBuilder questionStr = new StringBuilder();
            questionStr.append("导入成功，存在问题手机号:");
            if (list != null) {

                for (int i = 1; i < list.size(); i++) {
                    List<String> cellList = list.get(i);

                    if (!StringUtils.isEmpty(cellList.get(0))) {
                        try {

                            if(platformCode.equals("qg")){
                                Long phone = Long.valueOf(cellList.get(0));

                                if(phone.toString().length() == 11){
                                    Users username = userDao.selectByUsername(cellList.get(0), null);
                                    Users telephone = userDao.selectByTelephone(cellList.get(0), null);
                                    if(!StringUtils.isEmpty(telephone) || !StringUtils.isEmpty(username)){
                                        questionStr.append(cellList.get(0));
                                        questionStr.append(";");
                                        continue;
                                    }
                                }else{
                                    questionStr.append(cellList.get(0));
                                    questionStr.append(";");
                                    continue;
                                }
                            } else {
                                String phone = cellList.get(0);

                                if(phone.toString().length() > 0){
                                    Users username = userDao.selectByUsername(cellList.get(0), null);
                                    Users telephone = userDao.selectByTelephone(cellList.get(0), null);
                                    if(!StringUtils.isEmpty(telephone) || !StringUtils.isEmpty(username)){
                                        questionStr.append(cellList.get(0));
                                        questionStr.append(";");
                                        continue;
                                    }
                                }else{
                                    questionStr.append(cellList.get(0));
                                    questionStr.append(";");
                                    continue;
                                }
                            }


                        }catch (Exception e){
                            questionStr.append(cellList.get(0));
                            questionStr.append(";");
                            continue;
                        }

                        Users users = new Users();
                        long id = new IdGenerator(0, 0).nextId();
                        users.setId(id);
                        users.setUsername(cellList.get(0));
                        users.setPassword(MD5Utils.encode(cellList.get(1)));
                        users.setNickname(cellList.get(2));
                        users.setRegistType((byte) 1);
                        users.setTelephone(cellList.get(0));
                        users.setEmail(cellList.get(3));
                        if(("女").equals(cellList.get(4))){
                            users.setSex((byte)1);
                        }else{
                            users.setSex((byte)0);
                        }
                        users.setState((byte)1);
                        users.setCompanyId(companyId);
                        users.setCreateUser("admin");
                        users.setCreateDate(new Date());
                        int num = userDao.insertSelective(users);
                        if (num > 0) {
                            long roleId = new IdGenerator(0, 0).nextId();
                            UserRole userRole = new UserRole();
                            userRole.setId(roleId);
                            userRole.setUserId(users.getId());
                            userRole.setRoleId(Long.valueOf("418061569927151616"));
                            userRole.setCreateUser(request.getHeader("username"));
                            userRole.setCreateDate(new Date());
                            userRoleMapper.insertSelective(userRole);
                        }
                    }
                }
            }
            if(questionStr.length() > 13){
                result = JSONObject.toJSONString(new ResponseData(200, questionStr.toString(), false));
            }else{
                result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }
        } catch (Exception e) {
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    public String selectUserList(SearchEntity searchEntity){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Users> userList = userDao.selectUserList(searchEntity);

            for(Users users : userList){
//                List<UserRole> userRoleList = userRoleMapper.selectByUserId(users.getId());
//                StringBuilder roleIds = new StringBuilder();
//                StringBuilder roleNamses = new StringBuilder();
//                if(userRoleList.size() > 0){
//                    for(UserRole userRole : userRoleList){
//                        Role role = roleService.getRole(userRole.getRoleId());
//                        roleIds.append(role.getId()).append(",");
//                        roleNamses.append(role.getRoleName()).append(",");
//                    }
//                    users.setRoleIds(roleIds.toString().substring(0, roleIds.length()-1));
//                    users.setRoleNames(roleNamses.toString().substring(0, roleNamses.length()-1));
//                }

                if(!StringUtils.isEmpty(users.getCompanyId())){
                    Company company = companyMapper.selectByPrimaryKey(Long.valueOf(users.getCompanyId().longValue()));
                    if(!StringUtils.isEmpty(company)){
                        users.setCompanyName(company.getCompanyName());
                    }
                }
            }
            PageBean<Users> pageBean = new PageBean<>(userList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    public String selectAccountList(SearchEntity searchEntity){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Users> userList = userDao.selectAccountList(searchEntity, Long.valueOf(xueyuan));

            for(Users users : userList){
                if(!StringUtils.isEmpty(users.getCompanyId())){
                    Company company = companyMapper.selectByPrimaryKey(Long.valueOf(users.getCompanyId().longValue()));
                    if(!StringUtils.isEmpty(company)){
                        users.setCompanyName(company.getCompanyName());
                    }
                }
            }
            PageBean<Users> pageBean = new PageBean<>(userList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectByCompanyIdList(SearchEntity searchEntity, Long[] companyId){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Users> userList = userDao.selectByCompanyIdList(searchEntity, companyId);
//            for(Users users : userList){
//                List<UserRole> userRoleList = userRoleMapper.selectByUserId(users.getId());
//                StringBuilder roleIds = new StringBuilder();
//                StringBuilder roleNamses = new StringBuilder();
//                if(userRoleList.size() > 0){
//                    for(UserRole userRole : userRoleList){
//                        Role role = roleService.getRole(userRole.getRoleId());
//                        roleIds.append(role.getId()).append(",");
//                        roleNamses.append(role.getRoleName()).append(",");
//                    }
//                    users.setRoleIds(roleIds.toString().substring(0, roleIds.length()-1));
//                    users.setRoleNames(roleNamses.toString().substring(0, roleNamses.length()-1));
//                }
//            }
            PageBean<Users> pageBean = new PageBean<>(userList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String addUpdateUser(Users users, String roleIds){
        String result = null;
        try{
            Users u1 = userDao.selectByUsername(users.getUsername(), users.getId());
            if(!StringUtils.isEmpty(u1)){
                return JSONObject.toJSONString(new ResponseData(500, "用户已存在！", true));
            }

            if(!StringUtils.isEmpty(users.getTelephone())){
                Users u2 = userDao.selectByTelephone(users.getTelephone(), users.getId());
                if(!StringUtils.isEmpty(u2)){
                    return JSONObject.toJSONString(new ResponseData(500, "手机号码已存在！", true));
                }
            }

            if(StringUtils.isEmpty(roleIds)){
                return JSONObject.toJSONString(new ResponseData(500, "角色不能为空！", false));
            }else{
                Role role = roleService.getRole(Long.valueOf(roleIds));
                if(StringUtils.isEmpty(role)){
                    return JSONObject.toJSONString(new ResponseData(500, "非法请求，角色不存在！", false));
                }
            }
            if(StringUtils.isEmpty(users.getId())){
                long id =  new IdGenerator(0, 0).nextId();
                users.setId(id);
                users.setPassword(MD5Utils.encode(users.getPassword()));
                users.setCreateUser(request.getHeader("username"));
                users.setCreateDate(new Date());
                users.setState((byte)1);
                //用户注册
                users.setRegistType((byte)2);
                userDao.insertSelective(users);
                roleService.updateUserRole(id, roleIds);
            }else{
                Users u = userDao.selectByPrimaryKey(users.getId());
                if(!StringUtils.isEmpty(u)){
                    if(!StringUtils.isEmpty(users.getPassword())&&users.getPassword().length()>3){
                        u.setPassword(MD5Utils.encode(users.getPassword()));
                    }
                    //u.setUsername(users.getUsername());
                    //u.setTelephone(users.getTelephone());
                    u.setOperUser(request.getHeader("username"));
                    u.setOperDate(new Date());
                    u.setSex(users.getSex());
                    u.setEmail(users.getEmail());
                    u.setRemark(users.getRemark());
                    u.setNickname(users.getNickname());
                    u.setCompanyId(users.getCompanyId());
                    userDao.updateByPrimaryKeySelective(u);
                    //roleService.updateUserRole(users.getId(), roleIds);
                }
            }

            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String updateUser(Users users){
        String result = null;
        try{
            Users u1 = userDao.selectByUsername(users.getUsername(), users.getId());
            if(!StringUtils.isEmpty(u1)){
                return JSONObject.toJSONString(new ResponseData(500, "用户已存在！", true));
            }
            Users u2 = userDao.selectByTelephone(users.getTelephone(), users.getId());
            if(!StringUtils.isEmpty(u2)){
                return JSONObject.toJSONString(new ResponseData(500, "手机号码已存在！", true));
            }

            Users u = userDao.selectByPrimaryKey(users.getId());
            if(!StringUtils.isEmpty(users.getPassword())){
                if(StringUtils.isEmpty(users.getOldPassword())){
                    return JSONObject.toJSONString(new ResponseData(500, "原密码不能为空！", false));
                }
                if(!MD5Utils.encode(users.getOldPassword()).equals(u.getPassword())){
                    return JSONObject.toJSONString(new ResponseData(500, "原密码输入错误！", false));
                }
            }

            if(!StringUtils.isEmpty(u)){
                if(!StringUtils.isEmpty(users.getPassword())){
                    u.setPassword(MD5Utils.encode(users.getPassword()));
                }
                //u.setTelephone(users.getTelephone());
                u.setOperUser(request.getHeader("username"));
                u.setOperDate(new Date());
                u.setSex(users.getSex());
                u.setEmail(users.getEmail());
                u.setRemark(users.getRemark());
                u.setNickname(users.getNickname());
                userDao.updateByPrimaryKeySelective(u);
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String updateUserState(Users users){
        String result = null;
        try{
            if(!StringUtils.isEmpty(users.getId())){
                Users u = userDao.selectByPrimaryKey(users.getId());
                if(!StringUtils.isEmpty(u)){
                    u.setState(users.getState());
                    u.setOperUser(request.getHeader("username"));
                    u.setOperDate(new Date());
                    userDao.updateByPrimaryKeySelective(u);
                }
            }

            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectByUsername(String username, Long id){

        try{
            Users user = userDao.selectByUsername(username, id);
            if(StringUtils.isEmpty(user)){
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }else{
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", false));
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }
    }


    public String findUserPassword(String username, String shibboleth){

        try{
            if(!this.shibboleth.equals(shibboleth)){
                return JSONObject.toJSONString(new ResponseData(500, "口令错误！", false));
            }
            Users user = userDao.selectByUsername(username, null);
            if(!StringUtils.isEmpty(user)){
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", user.getPassword()));
            }else{
                return JSONObject.toJSONString(new ResponseData(500, "用户不存在！", false));
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }
    }

    public String selectByTelephone(String telephone, Long id){
        try{
            if(StringUtils.isEmpty(telephone)){

            }
            Users user = userDao.selectByTelephone(telephone, id);
            if(StringUtils.isEmpty(user)){
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }else{
                return JSONObject.toJSONString(new ResponseData(200, "请求成功！", false));
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
        }

    }

    public String delete(Long[] ids) {
        String result = null;
        try{
            for(int num=0; num<ids.length; num++){
                Users users = userDao.selectByPrimaryKey(ids[num]);
                if(StringUtils.isEmpty(users)){
                    return JSONObject.toJSONString(new ResponseData(500, "非法请求！", false));
                }else if(users.getExamNum() > 0){
                    return JSONObject.toJSONString(new ResponseData(500, "用户已答题，不能被删除！", false));
                }
                userDao.deleteByPrimaryKey(users.getId());
                userRoleMapper.deleteByUserId(users.getId());
            }
            //userDao.delete(ids);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", "true"));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", "false"));
            e.printStackTrace();
        }

        return  result;
    }


    public String updateMergerCompany(Long companyId, Long[] mergerIds) {
        String result = null;
        try{
            int num = 0;
            Company primaryKey = companyMapper.selectByPrimaryKey(companyId);
            userDao.updateMergerCompany(companyId, mergerIds);
            for(Long id : mergerIds){
                Company company = companyMapper.selectByPrimaryKey(id);
                if(!StringUtils.isEmpty(company)){
                    num += company.getPeopleNumber();
                    company.setState((byte)2);
                    companyMapper.updateByPrimaryKeySelective(company);
                }
            }
            primaryKey.setPeopleNumber(primaryKey.getPeopleNumber()+num);
            companyMapper.updateByPrimaryKeySelective(primaryKey);
            //companyMapper.delete(mergerIds);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", "true"));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", "false"));
            e.printStackTrace();
        }
        return  result;
    }

    public String getUser(Long id) {
        Users users = userDao.selectByPrimaryKey(id);
        List<UserRole> userRoleList = userRoleMapper.selectByUserId(users.getId());
        if(!StringUtils.isEmpty(userRoleList)){
            users.setRoleIds(String.valueOf(userRoleList.get(0).getRoleId()));
        }
        if(!StringUtils.isEmpty(users.getCompanyId())){
            Company company = companyMapper.selectByPrimaryKey(users.getCompanyId());
            if(!StringUtils.isEmpty(company)){
                users.setCompanyName(company.getCompanyName());
            }
        }
        return  JSONObject.toJSONString(new ResponseData(200, "请求成功！", users));
    }
}
