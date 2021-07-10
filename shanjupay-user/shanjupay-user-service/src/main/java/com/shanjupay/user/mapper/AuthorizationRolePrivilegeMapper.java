package com.shanjupay.user.mapper;

import com.shanjupay.user.entity.AuthorizationRolePrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 角色-权限关系 Mapper 接口
 * </p>
 *
 *
 * @since 2019-08-13
 */
@Repository
public interface AuthorizationRolePrivilegeMapper extends BaseMapper<AuthorizationRolePrivilege> {

    @Insert("<script>" +
            "INSERT INTO authorization_role_privilege(ROLE_ID,PRIVILEGE_ID) VALUES " +
            "<foreach collection='pids' item='item'  separator=','>(#{rid},#{item})</foreach> " +
            "</script>")
    //@Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "ID")
    void insertRolePrivilege(@Param("rid") Long rid,@Param("pids") List<Long> pids);
}
