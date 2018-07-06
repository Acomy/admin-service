package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.ExamStrategy;
import org.springframework.stereotype.Component;

@Component
public interface ExamStrategyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExamStrategy record);

    int insertSelective(ExamStrategy record);

    ExamStrategy selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExamStrategy record);

    int updateByPrimaryKey(ExamStrategy record);

    ExamStrategy selectByProjectId(Long projectId);

    int deleteByProjectId(Long projectId);
}