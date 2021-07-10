package com.shanjupay.paymentagent.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PaymentBillDTO implements Serializable {
    /*private Long merchantId;
    private String merchantName;
    private Long merchantAppId;*/
    private String merchantOrderNo;
    private String channelOrderNo;
    private String productName;
    private String createTime;
    private String posTime;
    private String equipmentNo;
    private String userAccount;
    private BigDecimal totalAmount;
    private BigDecimal tradeAmount;
    private BigDecimal discountAmount;
    private BigDecimal serviceFee;
    private String refundOrderNo;
    private BigDecimal refundMoney;
    private String platformChannel;
    //private String storeId;  //自己平台的商户编号，再根据该编号来确认是否是本平台的订单
    private String remark;

}
