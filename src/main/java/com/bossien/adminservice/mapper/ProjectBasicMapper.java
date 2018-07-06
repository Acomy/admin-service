package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.ProjectBasic;
import com.bossien.common.util.SearchEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectBasicMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectBasic record);

    int insertSelective(ProjectBasic record);

    ProjectBasic selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectBasic record);

    int updateByPrimaryKey(ProjectBasic record);

    List<ProjectBasic> selectByProjectExamination();

    List<ProjectBasic> selectByProjectState(Byte state);

    List<ProjectBasic> selectByProjectForm();

    List<ProjectBasic> selectProjectBasicList(SearchEntity searchEntity);

    Integer selectSpeedByProjectId(Long projectId);

    ProjectBasic selectByBasicName(@Param(value = "basicName") String basicName, @Param(value = "id") Long id);
}