package com.shanjupay.transaction.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDTO;

/**
 * 交易相关的服务接口
 */
public interface TransactionService {

    /**
     * 生成门店二维码URL
     * @param qrCodeDTO 传入merchantId，appId，storeId，channel，subject，body
     * @return 支付入口（url），将传入的参数转为json，再用base64编码
     * @throws BusinessException
     */
    String createStoreQRCode(QRCodeDTO qrCodeDTO) throws BusinessException;


    /**
     * 保存支付宝订单接口，1.保存订单到闪聚平台 2.调用支付渠道代理服务调用支付宝的接口
     * @param payOrderDTO
     * @return
     * @throws BusinessException
     */
    PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException;

    /**
     * 根据订单号查询订单信息
     * @param tradeNo
     * @return
     * @throws BusinessException
     */
    public PayOrderDTO queryPayOrder(String tradeNo) throws BusinessException;

    /**
     * 更新订单支付状态
     * @param tradeNo 闪聚平台订单号
     * @param payChannelTradeNo 支付宝或微信的交易流水号(第三方支付系统的订单号)
     * @param state 订单状态 交易支付状态 0-订单生成 1-支付中 2-支付完成 4-关闭 5-失败
     * @throws BusinessException
     */
    void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) throws BusinessException;
}
