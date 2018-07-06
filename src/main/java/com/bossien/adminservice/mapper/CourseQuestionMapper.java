package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.CourseQuestion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface CourseQuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseQuestion record);

    int insertSelective(CourseQuestion record);

    CourseQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseQuestion record);

    int updateByPrimaryKey(CourseQuestion record);

    CourseQuestion selectByCourseQuestionId(Integer querstionId);
}