package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.RoleResourceMapper;
import com.bossien.adminservice.redis.RedisDao;
import com.bossien.adminservice.util.PageBean;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Role;
import com.bossien.common.model.basics.RoleResource;
import com.bossien.common.model.basics.UserRole;
import com.bossien.common.util.DateUtil;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.SearchEntity;
import com.bossien.adminservice.mapper.RoleMapper;
import com.bossien.adminservice.mapper.UserRoleMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private ResourceService resourceService;

    public String selectRoleList(SearchEntity searchEntity){
        String result = "";
        try{
            PageHelper.startPage(searchEntity.getPageNum(), searchEntity.getPageSize());
            List<Role> roleList = roleMapper.selectRoleList(searchEntity);
            if(!StringUtils.isEmpty(roleList)){
                for(Role role : roleList){
                    redisDao.setDatabase(1);
                    Long expire = redisDao.getExpire(String.valueOf(role.getId()));
                    role.setExpire(DateUtil.formatDateTime(expire));
                }
            }
            PageBean<Role> pageBean = new PageBean<>(roleList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }


    public String updateRoleExpire(Long roleId){
        String result = "";
        try{
            redisDao.setDatabase(1);
            Long expire = redisDao.getExpire(String.valueOf(roleId));
            if(expire < 3600*24*3){
                redisDao.setExpire(String.valueOf(roleId), 60*24*14);
                result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
            }else{
                result = JSONObject.toJSONString(new ResponseData(500, "时间还很充足不需要更新！", false));
            }
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String updateRoleResource(Long roleId, String[] resourceIds){
        String result = "";
        try{
            roleResourceMapper.deleteByRoleId(roleId);
            if(resourceIds.length > 0){
                for(int i=0; i<resourceIds.length; i++){
                    String[] resource = resourceIds[i].split("_");
                    RoleResource roleResource = new RoleResource();
                    long id =  new IdGenerator(0, 0).nextId();
                    roleResource.setId(id);
                    roleResource.setRoleId(roleId);
                    roleResource.setMenuId(Long.valueOf(resource[1]));
                    roleResource.setResourceId(Long.valueOf(resource[0]));
                    roleResource.setCreateUser(request.getHeader("username"));
                    roleResource.setCreateDate(new Date());
                    roleResourceMapper.insertSelective(roleResource);
                }
                List<String> roleResourceList = roleResourceMapper.selectRoleResourceList(roleId);
                redisDao.setDatabase(1);
                redisDao.delete(String.valueOf(roleId));
                redisDao.setForSetKey(String.valueOf(roleId),roleResourceList.toArray(new String[0]), 60*24*14);
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectRoleAllList(){
        String result = "";
        try{
            List<Role> roleList = roleMapper.selectRoleList(null);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", roleList));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    public String addUpdateRole(Role role){
        String result = null;
        try{
            if(StringUtils.isEmpty(role.getId())){
                long id =  new IdGenerator(0, 0).nextId();
                role.setId(id);
                role.setCreateUser(request.getHeader("username"));
                role.setCreateDate(new Date());
                roleMapper.insertSelective(role);
            }else{
                role.setOperUser(request.getHeader("username"));
                role.setOperDate(new Date());
                roleMapper.updateByPrimaryKeySelective(role);
            }

            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String delete(Long[] ids) {
        String result = null;
        try{
            roleMapper.delete(ids);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", "true"));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", "false"));
            e.printStackTrace();
        }

        return  result;
    }

    public String getRoleJson(Long id) {
        Role role = roleMapper.selectByPrimaryKey(id);
        return  JSONObject.toJSONString(new ResponseData(200, "请求成功！", role));
    }

    public Role getRole(Long id) {
        Role role = roleMapper.selectByPrimaryKey(id);
        return role;
    }

    public String updateUserRole(Long userId, String roleIds){
        String result = null;
        try{
            userRoleMapper.deleteByUserId(userId);
            if(!roleIds.isEmpty()){
                String[] roleStr = roleIds.split(",");
                for(int num =0; num < roleStr.length; num++){
                    long id =  new IdGenerator(0, 0).nextId();
                    UserRole userRole = new UserRole();
                    userRole.setId(id);
                    userRole.setUserId(userId);
                    userRole.setRoleId(Long.valueOf(roleStr[num]));
                    userRole.setCreateUser(request.getHeader("username"));
                    userRole.setCreateDate(new Date());
                    userRoleMapper.insertSelective(userRole);
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }
}
