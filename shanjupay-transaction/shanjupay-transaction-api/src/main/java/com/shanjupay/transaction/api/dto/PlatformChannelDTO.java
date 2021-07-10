package com.shanjupay.transaction.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 平台提供支付渠道
 */
@ApiModel(value = "PlatformChannelDTO", description = "平台支付渠道")
@Data
public class PlatformChannelDTO implements Serializable {

    @ApiModelProperty("支付渠道ID")
    private Long id;
    /**
     * 支付渠道名称
     */
    @ApiModelProperty("支付渠道名称")
    private String channelName;
    /**
     * 支付渠道编码
     */
    @ApiModelProperty("支付渠道编码")
    private String channelCode;
}
