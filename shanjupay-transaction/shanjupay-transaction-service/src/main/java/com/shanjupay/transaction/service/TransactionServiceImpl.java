package com.shanjupay.transaction.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qiniu.util.Json;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.AmountUtil;
import com.shanjupay.common.util.EncryptUtil;
import com.shanjupay.common.util.IdWorkerUtils;
import com.shanjupay.common.util.PaymentUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.AlipayBean;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PayOrderDTO;
import com.shanjupay.transaction.api.dto.QRCodeDTO;
import com.shanjupay.transaction.convert.PayOrderConvert;
import com.shanjupay.transaction.entity.PayOrder;
import com.shanjupay.transaction.mapper.PayOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    //从配置文件读取支付入口地址
    @Value("${shanjupay.payurl}")
    String payurl;

    @Reference
    MerchantService merchantService;

    @Reference
    AppService appService;

    @Autowired
    PayOrderMapper payOrderMapper;
    
    @Autowired
    PayChannelService payChannelService;

    @Reference
    PayChannelAgentService payChannelAgentService;

    /**
     * 生成一个支付入口的url
     *
     * @param qrCodeDTO 传入merchantId，appId，storeId，channel，subject，body
     * @return
     * @throws BusinessException
     */
    @Override
    public String createStoreQRCode(QRCodeDTO qrCodeDTO) throws BusinessException {
        //校验商户id和应用id和门店id的合法性
        verifyAppAndStore(qrCodeDTO.getMerchantId(), qrCodeDTO.getAppId(), qrCodeDTO.getStoreId());
        //组装url所需要的数据
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setMerchantId(qrCodeDTO.getMerchantId());
        payOrderDTO.setAppId(qrCodeDTO.getAppId());
        payOrderDTO.setStoreId(qrCodeDTO.getStoreId());
        payOrderDTO.setSubject(qrCodeDTO.getSubject());//显示订单标题
        payOrderDTO.setChannel("shanju_c2b");//服务类型
        payOrderDTO.setBody(qrCodeDTO.getBody());//订单内容
        //转成json
        String jsonString = JSON.toJSONString(payOrderDTO);
        //base64编码
        String ticket = EncryptUtil.encodeUTF8StringBase64(jsonString);
        String url = payurl + "/" + ticket;
        return url;
    }

    @Override
    public PaymentResponseDTO submitOrderByAli(PayOrderDTO payOrderDTO) throws BusinessException {
        payOrderDTO.setChannel("ALIPAY_WAP");//支付渠道
        //保存订单到闪聚平台数据库
        PayOrderDTO save = save(payOrderDTO);

        //调用支付渠道代理服务支付宝下单接口
        PaymentResponseDTO paymentResponseDTO = alipayH5(save.getTradeNo());
        return paymentResponseDTO;
    }

    /**
     * 调用支付宝下单接口
     * @return
     */
    private PaymentResponseDTO alipayH5(String tradeNo){
        //订单信息，从数据库查询订单
        PayOrderDTO payOrderDTO = queryPayOrder(tradeNo);
        //组装alipayBean
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOutTradeNo(payOrderDTO.getTradeNo());//订单号
        try {
            alipayBean.setTotalAmount(AmountUtil.changeF2Y(payOrderDTO.getTotalAmount().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_300006);
        }
        alipayBean.setSubject(payOrderDTO.getSubject());
        alipayBean.setBody(payOrderDTO.getBody());
        alipayBean.setExpireTime("30m");

        //支付渠道配置参数，从数据库查询
        //String appId,String platformChannel,String payChannel
        PayChannelParamDTO payChannelParamDTO = payChannelService.queryParamByAppPlatformAndPayChannel(payOrderDTO.getAppId(), "shanju_c2b", "ALIPAY_WAP");
        String paramJson = payChannelParamDTO.getParam();
        //支付渠道参数
        AliConfigParam aliConfigParam = JSON.parseObject(paramJson, AliConfigParam.class);
        //字符编码
        aliConfigParam.setCharest("utf-8");
        //AliConfigParam aliConfigParam, AlipayBean alipayBean
        PaymentResponseDTO payOrderByAliWAP = payChannelAgentService.createPayOrderByAliWAP(aliConfigParam, alipayBean);
        return payOrderByAliWAP;
    }

    @Override
    public PayOrderDTO queryPayOrder(String tradeNo){
        PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getTradeNo, tradeNo));
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    @Override
    public void updateOrderTradeNoAndTradeState(String tradeNo, String payChannelTradeNo, String state) throws BusinessException {
        LambdaUpdateWrapper<PayOrder> payOrderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        payOrderLambdaUpdateWrapper.eq(PayOrder::getTradeNo, tradeNo)
                .set(PayOrder::getTradeState, state)
                .set(PayOrder::getPayChannelTradeNo, payChannelTradeNo);
        if (state != null && state.equals("2")){
            payOrderLambdaUpdateWrapper.set(PayOrder::getPaySuccessTime, LocalDateTime.now());
        }
        payOrderMapper.update(null, payOrderLambdaUpdateWrapper);
    }

    /**
     * 保存订单到闪聚平台数据库
     * @param payOrderDTO
     * @return
     * @throws BusinessException
     */
    private PayOrderDTO save(PayOrderDTO payOrderDTO) throws BusinessException{
        PayOrder payOrder = PayOrderConvert.INSTANCE.dto2entity(payOrderDTO);
        //订单号
        payOrder.setTradeNo(PaymentUtil.genUniquePayOrderNo());
        //创建时间
        payOrder.setCreateTime(LocalDateTime.now());
        //过期时间
        payOrder.setExpireTime(LocalDateTime.now().plus(30, ChronoUnit.MINUTES));
        //货币种类
        payOrder.setCurrency("CNY");
        //订单状态
        payOrder.setTradeState("0");
        payOrderMapper.insert(payOrder);
        return PayOrderConvert.INSTANCE.entity2dto(payOrder);
    }

    private void verifyAppAndStore(Long merchantId, String appId, Long storeId) {
        //校验应用是否属于商户
        Boolean aBoolean = appService.queryAppInMerchant(appId, merchantId);
        if (!aBoolean) {
            throw new BusinessException(CommonErrorCode.E_200005);
        }
        //校验门店是否属于商户
        Boolean aBoolean1 = merchantService.queryStoreInMerchant(storeId, merchantId);
        if (!aBoolean1) {
            throw new BusinessException(CommonErrorCode.E_200006);
        }
    }

}
