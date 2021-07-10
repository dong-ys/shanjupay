package com.shanjupay.user.api.dto.authorization;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权信息
 */
@Data
@ApiModel(value = "AuthorizationInfoDTO", description = "授权信息,包括角色与权限")
public class AuthorizationInfoDTO implements Serializable {

	/*private List<String>  roleCodes = new ArrayList<>();//角色编码集合

	private List<String> privilegeCodes = new ArrayList<>(); //权限编码集合
*/
	@ApiModelProperty("授权信息，角色-权限关系,key为角色编码，value为权限集合") //rolecode [pcode1,pcode2]
	private Map<String,List<String>>  rolePrivilegeMap = new HashMap<>();//角色-权限列表 映射


}
