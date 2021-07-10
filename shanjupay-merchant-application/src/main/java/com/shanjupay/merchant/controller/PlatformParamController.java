package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.transaction.api.PayChannelService;
import com.shanjupay.transaction.api.dto.PayChannelDTO;
import com.shanjupay.transaction.api.dto.PayChannelParamDTO;
import com.shanjupay.transaction.api.dto.PlatformChannelDTO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import sun.plugin.services.PlatformService;

import java.util.List;

/**
 * 平台支付参数配置相关的controller
 */
@Api(value = "商户平台-渠道和支付参数相关", tags = "商户平台-渠道和支付参数", description = "商户平台-渠道和支付参数相关")
@RestController
@Slf4j
public class PlatformParamController {

    @Reference
    PayChannelService payChannelService;


    @ApiOperation("获取平台服务类型")
    @GetMapping(value="/my/platform-channels")
    public List<PlatformChannelDTO> queryPlatformChannel(){
        return payChannelService.queryPlatformChannel();
    }

    @ApiOperation("绑定服务类型")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id",name = "appId",required = true,dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "服务类型code",name = "platformChannelCodes",required = true,dataType = "String",paramType = "query")
    })
    @PostMapping(value="/my/apps/{appId}/platform-channels")
    void bindPlatformForApp(@PathVariable("appId") String appId,@RequestParam("platformChannelCodes") String platformChannelCodes){
        payChannelService.bindPlatformChannelForApp(appId,platformChannelCodes);
    }

    @ApiOperation("查询应用是否绑定了某个服务类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用appId", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "platformChannel", value = "服务类型", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/my/merchants/apps/platformchannels")
    public int queryAppBindPlatformChannel(@RequestParam("tenantId") String tenantId, @RequestParam("appId") String appId, @RequestParam("platformChannel") String platformChannel){
        return payChannelService.queryAppBindPlatformChannel(appId,platformChannel);
    }

    @ApiOperation("根据服务类型查询支付渠道")
    @ApiImplicitParam(name = "platformChannelCode", value = "服务类型代码", required = true, dataType = "String", paramType = "path")
    @GetMapping(value="/my/pay-channels/platform-channel/{platformChannelCode}")
    List<PayChannelDTO> queryPayChannelByPlatformChannel(@PathVariable("platformChannelCode") String platformChannelCode) throws BusinessException{
        return payChannelService.queryPayChannelByPlatformChannel(platformChannelCode);

    }

    @ApiOperation("商户配置支付渠道参数")
    @ApiImplicitParam(name = "payChannelParamDTO", value = "支付渠道参数", required = true, dataType = "PayChannelParamDTO", paramType = "body")
    @RequestMapping(value = "/my/pay-channel-params",method = {RequestMethod.POST,RequestMethod.PUT})
    void createPayChannelParam(@RequestBody PayChannelParamDTO payChannelParamDTO){
        if(payChannelParamDTO == null || payChannelParamDTO.getChannelName() == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //商户id
        Long merchantId = SecurityUtil.getMerchantId();
        payChannelParamDTO.setMerchantId(merchantId);
        payChannelService.savePayChannelParam(payChannelParamDTO);

    }

    @ApiOperation("根据应用和服务类型获取支付渠道参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id",name = "appId",dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "服务类型代码",name = "platformChannel",dataType = "String",paramType = "path")
    })
    @GetMapping(value = "/my/pay-channel-params/apps/{appId}/platform-channels/{platformChannel}")
    public  List<PayChannelParamDTO> queryPayChannelParam(@PathVariable("appId")String appId,@PathVariable("platformChannel")String platformChannel){
        return payChannelService.queryPayChannelParamByAppAndPlatform(appId,platformChannel);
    }

    @ApiOperation("根据应用和服务类型和支付渠道获取单个支付渠道参数")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用id",name = "appId",dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "服务类型代码",name = "platformChannel",dataType = "String",paramType = "path"),
            @ApiImplicitParam(value = "支付渠道代码",name = "payChannel",dataType = "String",paramType = "path")
    })
    @GetMapping(value = "/my/pay-channel-params/apps/{appId}/platform-channels/{platformChannel}/pay-channels/{payChannel}")
    public  PayChannelParamDTO queryPayChannelParam(@PathVariable("appId")String appId,@PathVariable("platformChannel")String platformChannel,@PathVariable("payChannel") String payChannel){
        return payChannelService.queryParamByAppPlatformAndPayChannel(appId,platformChannel,payChannel);
    }

}

