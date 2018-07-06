package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.RoleResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleResourceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RoleResource record);

    int insertSelective(RoleResource record);

    RoleResource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RoleResource record);

    int updateByPrimaryKey(RoleResource record);

    int deleteByRoleId(Long roleId);

    List<String> selectRoleResourceList(Long roleId);
}