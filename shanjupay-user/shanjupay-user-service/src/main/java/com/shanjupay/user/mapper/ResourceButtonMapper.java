package com.shanjupay.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.user.api.dto.resource.ResourceDTO;
import com.shanjupay.user.entity.ResourceButton;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>
 * 菜单 Mapper 接口
 * </p>
 *
 *
 * @since 2019-08-13
 */
@Repository
public interface ResourceButtonMapper extends BaseMapper<ResourceButton> {

   /* @Select("<script>" +
            "select a.`NAME`,a.`CODE`,b.* from resource_button b " +
            "LEFT JOIN resource_application a ON a.`CODE`=b.APPLICATION_CODE " +
            "where APPLICATION_CODE=#{applicationCode} " +
            "and PRIVILEGE_CODE in <foreach collection='privilegeCodes' item='item' open='(' separator=',' close=')'>#{item}</foreach> " +
            "</script>")
    List<ResourceDTO> selectButtonByPrivilegeInApp(@Param("privilegeCodes") List<String> privilegeCodes, @Param("applicationCode") String applicationCode);
*/
}
