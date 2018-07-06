package com.bossien.adminservice.service;
import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.util.PageBean;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Resource;
import com.bossien.common.util.IdGenerator;
import com.bossien.adminservice.mapper.ResourceMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class ResourceService {
    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private HttpServletRequest request;

    public String selectResourceList(){
        String result = "";
        try{
            PageHelper.startPage(1, 200);
            List<Resource> userList = resourceMapper.selectResourceList();
            PageBean<Resource> pageBean = new PageBean<>(userList, 200, "请求成功！");
            result = JSONObject.toJSONString(pageBean);
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(200, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String selectResourceTree(Long roleId){
        String result = "";
        try{
            List<Resource> menuList = resourceMapper.selectResourceTree(roleId);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", menuList));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    public String addUpdateResource(Resource resource){
        String result = null;
        try{
            if(StringUtils.isEmpty(resource.getId())){
                long id =  new IdGenerator(0, 0).nextId();
                resource.setId(id);
                resource.setCreateUser(request.getHeader("username"));
                resource.setCreateDate(new Date());
                resourceMapper.insertSelective(resource);
            }else{
                resource.setOperUser(request.getHeader("username"));
                resource.setOperDate(new Date());
                resourceMapper.updateByPrimaryKeySelective(resource);
            }

            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String delete(Long id) {
        String result = null;
        try{
            resourceMapper.deleteByPrimaryKey(id);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", "true"));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", "false"));
            e.printStackTrace();
        }

        return  result;
    }

    public String getResource(Long id) {
        Resource resource = resourceMapper.selectByPrimaryKey(id);
        return  JSONObject.toJSONString(new ResponseData(200, "请求成功！", resource));
    }

    public Resource getResourceObj(Long id) {
        Resource resource = resourceMapper.selectByPrimaryKey(id);
        return  resource;
    }
}
