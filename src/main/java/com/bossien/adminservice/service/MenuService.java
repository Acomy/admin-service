package com.bossien.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.bossien.adminservice.mapper.MenuMapper;
import com.bossien.common.base.ResponseData;
import com.bossien.common.model.basics.Menu;
import com.bossien.common.util.IdGenerator;
import com.bossien.common.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class MenuService {
    @Value("${roleId.xueyuan}")
    private String xueyuan;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private HttpServletRequest request;

    public String selectMenuList(){
        String result = "";
        try{
            List<Menu> menuList = menuMapper.selectMenuList();
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", menuList));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }

    public String addMenu(Menu menu){
        String result = null;
        try{
            if(StringUtils.isEmpty(menu.getId())){
                //next value for MYCATSEQ_USER,
                long id =  new IdGenerator(0, 0).nextId();
                menu.setId(id);
                menu.setCreateUser(request.getHeader("username"));
                menu.setCreateDate(new Date());
                menuMapper.insertSelective(menu);
            }else{
                menu.setOperUser(request.getHeader("username"));
                menu.setOperDate(new Date());
                menuMapper.updateByPrimaryKeySelective(menu);
            }

            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String deleteMenu(Long id){
        String result = null;
        try{
            menuMapper.deleteByPrimaryKey(id);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", true));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", false));
            e.printStackTrace();
        }
        return result;
    }

    public String initMenu(){
        String result = null;
        String token = request.getHeader("Authorization");
        JWTUtils.JWTResult jwt = JWTUtils.getInstance().checkToken(token);

        if(!StringUtils.isEmpty(jwt)){
            String roleId = jwt.getRoleList().get(0);

            if(roleId.equals(xueyuan)){
                return JSONObject.toJSONString(new ResponseData(402, "您没有权限访问该系统！", null));
            }
            List<Menu> menuList = menuMapper.selectRoleMenuList(Long.valueOf(roleId));
            List<Menu> parents = new ArrayList<>();

            ListIterator<Menu> menuIterator = menuList.listIterator();

            while (menuIterator.hasNext()){
                Menu menu = menuIterator.next();
                if(menu.getPid() == 0){
                    parents.add(menu);
                    menuIterator.remove();
                }
            }

            for(Menu menu : menuList){
                for(Menu m : parents){
                    if(menu.getPid().equals(m.getId())){
                        m.getMenuList().add(menu);
                    }
                }
            }
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", parents));
        }else{
            result = JSONObject.toJSONString(new ResponseData(200, "Token不能为空！", null));
        }
        return result;
    }

    public String getMenu(Long id){
        String result = null;
        try{
            Menu menu = menuMapper.selectByPrimaryKey(id);
            result = JSONObject.toJSONString(new ResponseData(200, "请求成功！", menu));
        }catch (Exception e){
            result = JSONObject.toJSONString(new ResponseData(500, "请求失败！", null));
            e.printStackTrace();
        }
        return result;
    }
}
