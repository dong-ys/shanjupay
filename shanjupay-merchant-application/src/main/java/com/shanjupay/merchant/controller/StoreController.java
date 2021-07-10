package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.QRCodeUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.TransactionService;
import com.shanjupay.transaction.api.dto.QRCodeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api(value = "商户平台-门店管理", tags = "商户平台-门店管理", description = "商户平台-门店的增删改 查")
@RestController
public class StoreController {

    @Reference
    MerchantService merchantService;

    @Reference
    TransactionService transactionService;

    @Value("${shanjupay.c2b.subject}")
    String subject;

    @Value("${shanjupay.c2b.body}")
    String body;

    @ApiOperation("门店列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query")
    })
    @PostMapping("/my/stores/merchants/page")
    public PageVO<StoreDTO> queryStoreByPage(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Long merchantId = SecurityUtil.getMerchantId();
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setMerchantId(merchantId);

        return merchantService.queryStoreByPage(storeDTO, pageNo, pageSize);
    }

    @ApiOperation("生成商户应用门店二维码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "商户应用id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "storeId", value = "商户门店id", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping(value = "/my/apps/{appId}/stores/{storeId}/app-store-qrcode")
    public String createCScanBStoreQRCode(@PathVariable("storeId") Long storeId, @PathVariable("appId") String  appId) {
        Long merchantId = SecurityUtil.getMerchantId();
        MerchantDTO merchantDTO = merchantService.queryMerchantById(merchantId);
        QRCodeDTO qrCodeDTO = new QRCodeDTO();
        qrCodeDTO.setAppId(appId);
        qrCodeDTO.setStoreId(storeId);
        qrCodeDTO.setMerchantId(merchantId);

        //标题
        String subjectFormat = String.format(subject, merchantDTO.getMerchantName());
        qrCodeDTO.setSubject(subjectFormat);
        //内容
        String bodyFormat = String.format(body, merchantDTO.getMerchantName());
        qrCodeDTO.setBody(bodyFormat);
        //获取二维码的url
        String storeQRCode = transactionService.createStoreQRCode(qrCodeDTO);
        //调用工具类
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String qrCode = null;
        try {
            qrCode = qrCodeUtil.createQRCode(storeQRCode, 200, 200);
        }catch (IOException e){
            throw new BusinessException(CommonErrorCode.E_200007);
        }
        return qrCode;
    }
}
