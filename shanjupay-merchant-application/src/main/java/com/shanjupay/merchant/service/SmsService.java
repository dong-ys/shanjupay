package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

public interface SmsService {

    /**
     * 发送手机验证码
     * @param phone 手机号
     * @return 验证码对应的key
     */
    String sendMsg(String phone);

    /**
     * 校验验证码
     * @param verifyKey 验证码的key
     * @param verifyCode 验证码的值
     */
    void checkVerifyCode(String verifyKey, String verifyCode) throws BusinessException;
}
