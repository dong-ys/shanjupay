package com.shanjupay.transaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("platform_channel")
public class PlatformChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 平台支付渠道名称
     */
    @TableField("CHANNEL_NAME")
    private String channelName;

    /**
     * 平台支付渠道编码
     */
    @TableField("CHANNEL_CODE")
    private String channelCode;


}
