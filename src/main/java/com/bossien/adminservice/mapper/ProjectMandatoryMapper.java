package com.bossien.adminservice.mapper;

import com.bossien.common.model.GroupProject;
import com.bossien.common.model.basics.ProjectQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectMandatoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectQuestion record);

    int insertSelective(ProjectQuestion record);

    ProjectQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectQuestion record);

    int updateByPrimaryKey(ProjectQuestion record);

    List<GroupProject> selectGroupByChrType(Long projectId);

    int deleteByProjectId(Long projectId);
}