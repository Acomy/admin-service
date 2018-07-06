package com.bossien.adminservice.mapper;

import com.bossien.common.model.GroupProject;
import com.bossien.common.model.basics.ProjectQuestion;
import com.bossien.common.model.basics.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectQuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ProjectQuestion record);

    int insertSelective(ProjectQuestion record);

    void batchInsert(List<ProjectQuestion> list);

    ProjectQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ProjectQuestion record);

    int updateByPrimaryKey(ProjectQuestion record);

    List<GroupProject> selectGroupByChrType(Long projectId);

    int selectProjectQuestionSize(@Param(value = "projectId") Long projectId, @Param(value = "questionId") Integer questionId);

    int deleteByProjectId(Long projectId);
}