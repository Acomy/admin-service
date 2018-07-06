package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    int deleteByUserId(Long userId);

    List<UserRole> selectByUserId(Long userId);
}