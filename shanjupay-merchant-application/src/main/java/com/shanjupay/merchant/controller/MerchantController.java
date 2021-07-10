package com.shanjupay.merchant.controller;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.common.util.SecurityUtil;
import com.shanjupay.merchant.convert.MerchantDetailConvert;
import com.shanjupay.merchant.convert.MerchantRegisterConvert;
import com.shanjupay.merchant.service.FileService;
import com.shanjupay.merchant.service.SmsService;
import com.shanjupay.merchant.vo.MerchantDetailVO;
import com.shanjupay.merchant.vo.MerchantRegisterVO;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@Api(value = "商户平台应用的接口")
public class MerchantController {

    @Reference
    MerchantService merchantService;

    @Autowired
    SmsService smsService;

    @Autowired
    FileService fileService;

    @ApiOperation("根据id查询商户信息")
    @GetMapping("/merchants/{id}")
    public MerchantDTO queryMerchantById(@PathVariable("id") Long id) {
        return merchantService.queryMerchantById(id);
    }

    @ApiOperation("获取登录用户的商户信息")
    @GetMapping(value = "/my/merchants")
    public MerchantDTO getMyMerchantInfo() {
        Long merchantId = SecurityUtil.getMerchantId();
        return merchantService.queryMerchantById(merchantId);
    }

    @ApiOperation("获取手机验证码")
    @ApiImplicitParam(value = "手机号", name = "phone", required = true, dataType = "string", paramType = "query")
    @GetMapping("/sms")
    public String getSMSCode(@RequestParam("phone") String phone) {
        //手机格式校验
        if (!PhoneUtil.isMatches(phone)) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //向验证码服务请求发送验证码
        return smsService.sendMsg(phone);
    }

    @ApiOperation("商户注册")
    @PostMapping("/merchants/register")
    @ApiImplicitParam(value = "商户注册信息", name = "merchantRegisterVO", required = true, dataType = "MerchantRegisterVO", paramType = "body")
    public MerchantRegisterVO registerMerchant(@RequestBody MerchantRegisterVO merchantRegisterVO) {
        //校验参数的合法性
        if (merchantRegisterVO == null) {
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        //手机格式校验
        if (!PhoneUtil.isMatches(merchantRegisterVO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        if (StringUtils.isBlank(merchantRegisterVO.getUsername())) {
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        if (StringUtils.isBlank(merchantRegisterVO.getPassword())) {
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        if (StringUtils.isBlank(merchantRegisterVO.getMobile())) {
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        //校验验证码
        smsService.checkVerifyCode(merchantRegisterVO.getVerifiykey(), merchantRegisterVO.getVerifiyCode());
        //调用dubbo接口
        MerchantDTO merchantDTO = MerchantRegisterConvert.INSTANCE.vo2dto(merchantRegisterVO);
        merchantService.createMerchant(merchantDTO);
        return merchantRegisterVO;
    }

    @ApiOperation("上传证件照")
    @PostMapping("/upload")
    public String upload(@ApiParam(value = "证件照", required = true) @RequestParam("file") MultipartFile file) throws IOException {
        //调用fileService上传文件
        //生成的文件名称fileName，要保证它的唯一
        //文件原始名称
        String originalFilename = file.getOriginalFilename();
        //扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") - 1);
        //文件名称
        String fileName = UUID.randomUUID() + suffix;
        //byte[] bytes,String fileName
        return fileService.upload(file.getBytes(), fileName);
    }

    @ApiOperation("资质申请")
    @PostMapping("/my/merchants/save")
    @ApiImplicitParams({@ApiImplicitParam(name = "merchantInfo", value = "商户认证资料", required = true, dataType = "MerchantDetailVO", paramType = "body")})
    public void saveMerchant(@RequestBody MerchantDetailVO merchantInfo) {
        //取出当前登录商户的id,解析token
        Long merchantId = SecurityUtil.getMerchantId();
        //Bearer eyJtZXJjaGFudElkIjoxNDA1MzkwMDc3NDcwNzg5NjMzfQ==
        MerchantDTO merchantDTO = MerchantDetailConvert.INSTANCE.vo2dto(merchantInfo);
        merchantService.applyMerchant(merchantId, merchantDTO);
    }

}
