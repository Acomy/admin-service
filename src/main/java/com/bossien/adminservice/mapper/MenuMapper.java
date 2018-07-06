package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Menu;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MenuMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);

    List<Menu> selectMenuList();

    List<Menu> selectRoleMenuList(Long roleId);
}