package com.shanjupay.transaction.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "PayChannelDTO", description = "原始第三方支付渠道")
@Data
public class PayChannelDTO implements Serializable {

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
