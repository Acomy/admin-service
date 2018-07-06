package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Business;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BusinessMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Business record);

    int insertSelective(Business record);

    Business selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Business record);

    int updateByPrimaryKey(Business record);

    List<Business> selectByBusinessLevel();

    Business selectByBusinessName(String businessName);
}