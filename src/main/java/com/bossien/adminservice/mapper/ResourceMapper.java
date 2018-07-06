package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Resource;
import com.bossien.common.util.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ResourceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);

    List<Resource> selectResourceList();

    List<Resource> selectResourceTree(Long roleId);
}