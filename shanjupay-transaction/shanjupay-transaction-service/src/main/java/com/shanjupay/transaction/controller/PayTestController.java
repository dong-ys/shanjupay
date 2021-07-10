package com.shanjupay.transaction.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 支付宝接口测试类
 */
@Controller
public class PayTestController {

    String APP_ID = "2021000117677115";
    String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCM1QVMVouOYVcIrCx41D476xBfWX95oKzu1kLoMzLBxAJ1qB7J9JVtjcAGA22T1K1A/pU7/RuPGmyyVsZU5G/bx8XKmUtF+7I28bj/LWHy/2LRjh21eTLU+FahGdDI6CxMasXuM67dtfb9yz1UKXjwAT08zGImGhMp1PnxPPip4lqt02wbjCIIWGj6QGZ9y36NkhxwQ+l+dcOEd/paIvaD43usbkc5V0ojbE/IrQdZIlacQYeLtPEcTvxfO7iIOHIOBJ/ZpADUqfcs6malsxGC3Yx1j6HJr7uzUspxDk9007uJVvSKHbvvBZ81cThWZHWxz0k+at5tkWvxb6fKKvsZAgMBAAECggEATaGHQCNc3mlt7ovqH9jvTVwwND9Dc6pMaYOWciT2z7TanjzEbBL04+UFessc9iaAMpmjbeQB8wpf7JbE0TrzJjWjJCEIS1WJUZxBm/hKTAbXVCiA2+4pRRh3uGpgmr7Q3HbnEH1Q4UpTEmecMO6DidlFBmSrndE1yxNpi79OW2ZnQcuv+ak36i1VRHhckvuKAos+OxZsux/Ov6FSa+1ZDmczvgBhnnh4BuSbMaaobGFO+tQvRWlR9+R0w1seHaijehBgkUS75X6AgYtaiY1PInlw10/Bo+cgOJCqlXVkybFuW3haYKLNphosukZOzV1Me+vOC2dfzw916WysPzRB4QKBgQDLKI60sRitsqztysFh9rjNMQ06BrXRsZz8ns3zcSU8gi8yWFGcSl14eHIgoHqf/spcrAfiPd02LyWr++lIk3yQZeC87a5/wSxNCd8RY37DVLyCXnkF8akY93sOh3NxheEq7A42JV6HCKjGx4k0AXURAaK25r9VhNvrglqz9heApQKBgQCxdmxJvRGjVdhiTMBGcvm2sJFoU7l9pdcFO3b2cmVy8mkAHlN2NJAS1o24daMali6ujaHXk8Ql5IqauYVOfSsZojeRVRxQk2j8m2Ytz07YiOoum5HY3ADEcD1blJHEMC66MMJnDgtsyk1ZT9pgDy1CA2hon7auGEqpX1FYJKEyZQKBgQCF1dQdSUW+uCSgCqBXDxgzZQsXnPAOknmfGmcG5xTmxcSd3/zzfzIQeRiGUfI2kL1zS4luC989INYqdKv8Od0nq1XgYJxCYVnz7nrbk7YgrCP7po0H02nYnsCck7MDqHTrt9Ks7PVW8+dr/GtwrT0X1QICiASoK/xNK/Tg9VAKWQKBgQCpMLBZNQrl4Ghmf6opAGEXTguznW8RXdU8Rc9LcxFJn9d/p2oHHmHls+x12qHgLSLghGOydkzhm5yRmyJ/DFLVI5U62BwgrgnAHCA0uJsBUreCLAvG5ylMH35AG8tFcnJ6TRZqzjcSR2WrKriWMKIDd+BJL0wpeztjc17IXFPbCQKBgF4gG0Soe6XOEPh3qI0dKC2dpIrVOvzBVtWJnZhsevxyign5UPvtsvHdb3pDvh8U5A8RE/EX7BrJXvarAM/FCXpN0zBFt/RXQRfeQS3KMRmKvd5ytPEV8Cr8kzaIbfx85vgX9lVIbrKW1Kn/KBMAFA/TW2wP3vZXoGtiStO7cXbC";
    String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhMxCpOOOlkkD+hwVxx3R1LwGb2yY2b3+0nbjVTaAyhLbK0Aeu15/9Zh8oCJUWTB9dkjIrvmdDApMuRhEqCXZrGXUIyVI3o7I/JGCARduw/NrN+vnucguNU+fB7vOQzbXifkOqeWOxD77EeF2NLLimoXHu+8w17ewhCi9BHm4q0aXWUMGgrrJy2PorxS5vO1v8+vFw7njHLEy1YmIRRJdoNppSCJyCHIQZwZ1TQ3nRjuJ4YG2HoAy2o2/7BDo4Xt/VIk0LCTFnumVG6tLiS5bTKbi7nPA/OkRxlqZR6CLFMCo95SEAmLKmtmOia/SseDp+3mvpbpmd7vvRsl6ymRXlQIDAQAB";
    String CHARSET = "utf-8";
    String serverUrl = "https://openapi.alipaydev.com/gateway.do";
    String sign_type = "RSA2";

    @GetMapping("/alipaytest")
    public void alipayTest(HttpServletRequest httpRequest,
                       HttpServletResponse httpResponse) throws ServletException, IOException {

        //构造sdk的客户端对象
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, sign_type); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        //alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        //alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\"20150320010101002\"," +
                " \"total_amount\":\"88.88\"," +
                " \"subject\":\"Iphone6 16G\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");//填充业务参数
        String form="";
        try {
            //请求支付宝的下单接口，发起http请求
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest);
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
