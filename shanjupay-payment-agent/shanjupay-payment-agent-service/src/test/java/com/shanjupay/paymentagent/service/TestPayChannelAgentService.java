package com.shanjupay.paymentagent.service;

import com.shanjupay.paymentagent.api.PayChannelAgentService;
import com.shanjupay.paymentagent.api.conf.AliConfigParam;
import com.shanjupay.paymentagent.api.dto.PaymentResponseDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPayChannelAgentService {

    @Autowired
    PayChannelAgentService payChannelAgentService;

    @Test
    public void testQueryPayOrderByAli() {
        String APP_ID = "2021000117677115";
        String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCM1QVMVouOYVcIrCx41D476xBfWX95oKzu1kLoMzLBxAJ1qB7J9JVtjcAGA22T1K1A/pU7/RuPGmyyVsZU5G/bx8XKmUtF+7I28bj/LWHy/2LRjh21eTLU+FahGdDI6CxMasXuM67dtfb9yz1UKXjwAT08zGImGhMp1PnxPPip4lqt02wbjCIIWGj6QGZ9y36NkhxwQ+l+dcOEd/paIvaD43usbkc5V0ojbE/IrQdZIlacQYeLtPEcTvxfO7iIOHIOBJ/ZpADUqfcs6malsxGC3Yx1j6HJr7uzUspxDk9007uJVvSKHbvvBZ81cThWZHWxz0k+at5tkWvxb6fKKvsZAgMBAAECggEATaGHQCNc3mlt7ovqH9jvTVwwND9Dc6pMaYOWciT2z7TanjzEbBL04+UFessc9iaAMpmjbeQB8wpf7JbE0TrzJjWjJCEIS1WJUZxBm/hKTAbXVCiA2+4pRRh3uGpgmr7Q3HbnEH1Q4UpTEmecMO6DidlFBmSrndE1yxNpi79OW2ZnQcuv+ak36i1VRHhckvuKAos+OxZsux/Ov6FSa+1ZDmczvgBhnnh4BuSbMaaobGFO+tQvRWlR9+R0w1seHaijehBgkUS75X6AgYtaiY1PInlw10/Bo+cgOJCqlXVkybFuW3haYKLNphosukZOzV1Me+vOC2dfzw916WysPzRB4QKBgQDLKI60sRitsqztysFh9rjNMQ06BrXRsZz8ns3zcSU8gi8yWFGcSl14eHIgoHqf/spcrAfiPd02LyWr++lIk3yQZeC87a5/wSxNCd8RY37DVLyCXnkF8akY93sOh3NxheEq7A42JV6HCKjGx4k0AXURAaK25r9VhNvrglqz9heApQKBgQCxdmxJvRGjVdhiTMBGcvm2sJFoU7l9pdcFO3b2cmVy8mkAHlN2NJAS1o24daMali6ujaHXk8Ql5IqauYVOfSsZojeRVRxQk2j8m2Ytz07YiOoum5HY3ADEcD1blJHEMC66MMJnDgtsyk1ZT9pgDy1CA2hon7auGEqpX1FYJKEyZQKBgQCF1dQdSUW+uCSgCqBXDxgzZQsXnPAOknmfGmcG5xTmxcSd3/zzfzIQeRiGUfI2kL1zS4luC989INYqdKv8Od0nq1XgYJxCYVnz7nrbk7YgrCP7po0H02nYnsCck7MDqHTrt9Ks7PVW8+dr/GtwrT0X1QICiASoK/xNK/Tg9VAKWQKBgQCpMLBZNQrl4Ghmf6opAGEXTguznW8RXdU8Rc9LcxFJn9d/p2oHHmHls+x12qHgLSLghGOydkzhm5yRmyJ/DFLVI5U62BwgrgnAHCA0uJsBUreCLAvG5ylMH35AG8tFcnJ6TRZqzjcSR2WrKriWMKIDd+BJL0wpeztjc17IXFPbCQKBgF4gG0Soe6XOEPh3qI0dKC2dpIrVOvzBVtWJnZhsevxyign5UPvtsvHdb3pDvh8U5A8RE/EX7BrJXvarAM/FCXpN0zBFt/RXQRfeQS3KMRmKvd5ytPEV8Cr8kzaIbfx85vgX9lVIbrKW1Kn/KBMAFA/TW2wP3vZXoGtiStO7cXbC";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhMxCpOOOlkkD+hwVxx3R1LwGb2yY2b3+0nbjVTaAyhLbK0Aeu15/9Zh8oCJUWTB9dkjIrvmdDApMuRhEqCXZrGXUIyVI3o7I/JGCARduw/NrN+vnucguNU+fB7vOQzbXifkOqeWOxD77EeF2NLLimoXHu+8w17ewhCi9BHm4q0aXWUMGgrrJy2PorxS5vO1v8+vFw7njHLEy1YmIRRJdoNppSCJyCHIQZwZ1TQ3nRjuJ4YG2HoAy2o2/7BDo4Xt/VIk0LCTFnumVG6tLiS5bTKbi7nPA/OkRxlqZR6CLFMCo95SEAmLKmtmOia/SseDp+3mvpbpmd7vvRsl6ymRXlQIDAQAB";
        String CHARSET = "utf-8";
        String serverUrl = "https://openapi.alipaydev.com/gateway.do";
        AliConfigParam aliConfigParam = new AliConfigParam();
        aliConfigParam.setUrl(serverUrl);
        aliConfigParam.setCharest(CHARSET);
        aliConfigParam.setAlipayPublicKey(ALIPAY_PUBLIC_KEY);
        aliConfigParam.setRsaPrivateKey(APP_PRIVATE_KEY);
        aliConfigParam.setAppId(APP_ID);
        aliConfigParam.setFormat("json");
        aliConfigParam.setSigntype("RSA2");
        PaymentResponseDTO paymentResponseDTO = payChannelAgentService.queryPayOrderByAli(aliConfigParam, "SJ1407573686474100736");
        System.out.println(paymentResponseDTO);

    }
}
