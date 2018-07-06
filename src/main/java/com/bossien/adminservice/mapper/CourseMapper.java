package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Course;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CourseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Course record);

    int insertSelective(Course record);

    Course selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Course record);

    int updateByPrimaryKey(Course record);

    List<Course> selectCourseList(@Param(value = "inttypeid") Long[] inttypeid, @Param(value = "courseName")String courseName);

    Course selectByCourseId(Integer courseId);
}