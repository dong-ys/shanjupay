package com.shanjupay.transaction.controller;

/**
 * 浏览器类型
 */
public enum BrowserType {
    ALIPAY,//支付宝
    WECHAT,//微信
    PC_BROWSER,//pc端浏览器
    MOBILE_BROWSER; //手机端浏览器


    /**
     * 根据UA获取浏览器类型
     * @param userAgent userAgent
     * @return 浏览器类型
     */
    public static BrowserType valueOfUserAgent(String userAgent) {
        if (userAgent != null && userAgent.contains("AlipayClient")) {
            return BrowserType.ALIPAY;
        }else if (userAgent != null && userAgent.contains("MicroMessenger")) {
            return BrowserType.WECHAT;
        }else{
            return BrowserType.MOBILE_BROWSER;
        }
    }
}
