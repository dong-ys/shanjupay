package com.shanjupay.paymentagent.api.conf;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class WXConfigParam implements Serializable {

    private String appId;
    private String appSecret;  //是APPID对应的接口密码，用于获取接口调用凭证access_token时使用。
    private String mchId;
    private String key;
    public String returnUrl;

}
