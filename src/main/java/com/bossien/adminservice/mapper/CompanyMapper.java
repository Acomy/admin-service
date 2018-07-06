package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Company;
import com.bossien.common.util.SearchEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CompanyMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);

    List<Company> selectRegulatoryCompanyTree(@Param(value = "pid")Long pid);

    String getCompanyCodeByPid(@Param(value = "pid")Long pid, @Param(value = "isRegulatory")Byte isRegulatory);

    int selectRegulatoryCompanyTreeSize(@Param(value = "pid")Long pid);

    List<Company> selectCompanyTreeByPid(@Param(value = "pid")Long pid, @Param(value = "isRegulatory")int isRegulatory, @Param(value = "state")Byte state);

    List<Company> selectCompanyTreeById(@Param(value = "id")Long id, @Param(value = "isRegulatory")int isRegulatory, @Param(value = "state")Byte state);

    int selectALLCompanyTreeSize(@Param(value = "pid")Long pid);

    List<Company> selectCompanyList(@Param(value = "searchEntity")SearchEntity searchEntity, @Param(value = "pid") Long[] pid, @Param(value = "state")Byte state);

    List<Company> getRegulatoryCompanyAutocomplete(SearchEntity searchEntity);

    int delete(Long[] ids);

    Company selectByCompanyName(String companyName);

    List<Company> selectByCompanyCode(String code);
}