package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Question;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QuestionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Question record);

    int insertSelective(Question record);

    Question selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Question record);

    int updateByPrimaryKey(Question record);

    List<Question> selectProjectQuestion(@Param(value = "projectId")Long projectId,
                                         @Param(value = "singleCount")byte singleCount, @Param(value = "manyCount")byte manyCount, @Param(value = "judgeCount")byte judgeCount);

    List<Question> selectQuestionByCourseId(Long courseId);

    Question selectByQuestionId(Integer questionId);

    Question selectByQuestionNo(String questionNo);
}