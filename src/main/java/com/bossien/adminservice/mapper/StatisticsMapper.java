package com.bossien.adminservice.mapper;

import com.bossien.common.model.Statistics;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StatisticsMapper {

    List<Statistics> selectRoleSize();
}
