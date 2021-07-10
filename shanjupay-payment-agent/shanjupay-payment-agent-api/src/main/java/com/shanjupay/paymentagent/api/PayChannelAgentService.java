package com.shanjupay.paymentagent.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;

/**
 * 与第三方支付渠道进行交互
 */
public interface PayChannelAgentService {

    /**
     * 调用支付宝的下单接口
     * @param aliConfigParam 支付渠道的配置参数（支付宝的必要参数）
     * @param alipayBean 业务参数（商户订单号，订单标题，订单描述等）
     * @return 统一返回PaymentResponseDTO
     */
    PaymentResponseDTO createPayOrderByAliWAP(AliConfigParam aliConfigParam, AlipayBean alipayBean) throws BusinessException;

    /**
     * 查询支付宝的订单状态
     * @param aliConfigParam 支付渠道的参数
     * @param outTradeNo 闪聚平台的订单号
     * @return
     * @throws BusinessException
     */
    PaymentResponseDTO queryPayOrderByAli(AliConfigParam aliConfigParam, String outTradeNo) throws BusinessException;
}
