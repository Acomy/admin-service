package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Examination;
import com.bossien.common.util.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExaminationMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Examination record);

    int insertSelective(Examination record);

    Examination selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Examination record);

    int updateByPrimaryKey(Examination record);

    List<Examination> selectByProjectId(Long projectId);

    List<Examination> selectExaminationList(SearchEntity searchEntity);
}