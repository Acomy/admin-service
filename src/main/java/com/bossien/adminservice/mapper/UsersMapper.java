package com.bossien.adminservice.mapper;

import com.bossien.common.model.basics.Users;
import com.bossien.common.util.SearchEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface UsersMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    int updateUserName(Map<String, String> map);

    List<Users> selectUserList(SearchEntity searchEntity);

    List<Users> selectAccountList(@Param(value = "searchEntity")SearchEntity searchEntity, @Param(value = "roleId")Long roleId);

    Users selectByUsername(@Param(value = "username") String usernmae, @Param(value = "id") Long id);

    Users selectByTelephone(@Param(value = "telephone") String telephone, @Param(value = "id") Long id);

    int delete(Long[] ids);

    List<Users> selectByCompanyIdList(@Param(value = "searchEntity") SearchEntity searchEntity, @Param(value = "companyId")Long[] companyId);

    int selectUserSizeByCompanyId(Long companyId);

    int updateMergerCompany(@Param(value = "companyId") Long companyId, @Param(value = "mergerIds") Long[] mergerIds);

    int findUserSize();

    int updateCompanyUserState(@Param(value = "companyId")Long companyId, @Param(value = "state")Byte state);
}