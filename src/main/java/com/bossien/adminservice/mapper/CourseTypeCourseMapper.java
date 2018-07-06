package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.CourseTypeCourse;

public interface CourseTypeCourseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseTypeCourse record);

    int insertSelective(CourseTypeCourse record);

    CourseTypeCourse selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseTypeCourse record);

    int updateByPrimaryKey(CourseTypeCourse record);

    CourseTypeCourse selectByCourseId(Long id);
}