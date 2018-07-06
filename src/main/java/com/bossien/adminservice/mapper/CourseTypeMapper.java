package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.CourseType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CourseTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseType record);

    int insertSelective(CourseType record);

    CourseType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseType record);

    int updateByPrimaryKey(CourseType record);

    List<CourseType> selectCourseTypeTree();

    CourseType selectByCourseTypeId(Integer courseTypeId);
}