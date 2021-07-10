package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${sms.url}")
    String url;

    @Value("${sms.effectiveTime}")
    String effectiveTime;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public String sendMsg(String phone) throws BusinessException {
        //向验证码服务发送请求的地址
        String sms_url = url + "/generate?name=sms&effectiveTime="+effectiveTime;
        //请求体
        Map<String, Object> body = new HashMap<>();
        body.put("mobile", phone);
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        //请求信息，传入body和header
        HttpEntity httpEntity = new HttpEntity(body, httpHeaders);
        //向url请求

        Map bodyMap = null;
        ResponseEntity<Map> exchange = null;
        try {
            exchange = restTemplate.exchange(sms_url, HttpMethod.POST, httpEntity, Map.class);
            log.info("请求验证码服务，得到响应:{}", JSON.toJSONString(exchange));
            bodyMap = exchange.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100107);
        }
        if (bodyMap == null || bodyMap.get("result") == null){
            throw new BusinessException(CommonErrorCode.E_100107);
        }
        Map result = (Map) bodyMap.get("result");
        String key = (String) result.get("key");
        log.info("得到发送验证码对应的key:{}", key);
        return key;
    }

    @Override
    public void checkVerifyCode(String verifyKey, String verifyCode) throws BusinessException {
        //校验验证码的url
        String url = "http://localhost:56085/sailing/verify?name=sms&verificationCode="+verifyCode+"&verificationKey="+verifyKey;
        Map bodyMap = null;
        try {
            ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, Map.class);
            bodyMap = exchange.getBody();
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new BusinessException(CommonErrorCode.E_100102);
            //throw new RuntimeException("校验验证码失败");
        }
        if (bodyMap == null || bodyMap.get("result") == null || !(Boolean) bodyMap.get("result")){
            throw new BusinessException(CommonErrorCode.E_100102);
            //throw new RuntimeException("校验验证码失败");
        }
    }
}
