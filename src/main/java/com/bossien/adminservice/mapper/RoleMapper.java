package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Role;
import com.bossien.common.util.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List<Role> selectRoleList(SearchEntity searchEntity);

    int delete(Long[] ids);
}