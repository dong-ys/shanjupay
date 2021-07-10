package com.shanjupay.merchant.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "MerchantDTO", description = "商户信息")
@Data
public class MerchantDTO implements Serializable {
    @ApiModelProperty("商户id")
    private Long id;
    @ApiModelProperty("企业名称")
    private String merchantName;
    @ApiModelProperty("企业编号")
    private Long merchantNo;
    @ApiModelProperty("企业地址")
    private String merchantAddress;
    @ApiModelProperty("行业类型")
    private String merchantType;
    @ApiModelProperty("营业执照")
    private String businessLicensesImg;
    @ApiModelProperty("法人身份证正面")
    private String idCardFrontImg;
    @ApiModelProperty("法人身份证反面")
    private String idCardAfterImg;
    @ApiModelProperty("联系人")
    private String username;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("手机号,关联统一账号")
    private String mobile;
    @ApiModelProperty("联系人地址")
    private String contactsAddress;
    @ApiModelProperty("审核状态,0‐未申请,1‐已申请待审核,2‐审核通过,3‐审核拒绝")
    private String auditStatus;
    @ApiModelProperty("租户ID")
    private Long tenantId;
}
