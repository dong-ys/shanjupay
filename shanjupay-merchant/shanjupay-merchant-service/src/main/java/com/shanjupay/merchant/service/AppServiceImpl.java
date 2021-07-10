package com.shanjupay.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.util.StringUtil;
import com.shanjupay.merchant.api.AppService;
import com.shanjupay.merchant.api.dto.AppDTO;
import com.shanjupay.merchant.convert.AppConvert;
import com.shanjupay.merchant.entity.App;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.mapper.AppMapper;
import com.shanjupay.merchant.mapper.MerchantMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class AppServiceImpl implements AppService {

    @Autowired
    AppMapper appMapper;

    @Autowired
    MerchantMapper merchantMapper;

    /**
     * @param merchantId 商户id
     * @param appDTO     应用信息
     * @return
     * @throws BusinessException
     */
    @Override
    public AppDTO createApp(Long merchantId, AppDTO appDTO) throws BusinessException {
        if (merchantId == null || appDTO == null || StringUtil.isBlank(appDTO.getAppName())){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        /*
            1）校验商户是否通过资质审核 如果商户资质审核没有通过不允许创建应用。
            2）生成应用ID 应用Id使用UUID方式生成。
            3）保存商户应用信息 应用名称需要校验唯一性。
         */
        //1）校验商户是否通过资质审核 如果商户资质审核没有通过不允许创建应用。
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null){
            throw new BusinessException(CommonErrorCode.E_200002);
        }
        //取出商户资质申请状态
        String auditStatus = merchant.getAuditStatus();
        if (!"2".equals(auditStatus)){
            throw new BusinessException(CommonErrorCode.E_200003);
        }
        //校验应用名称
        if (isExistAppName(appDTO.getAppName())){
            throw new BusinessException(CommonErrorCode.E_200004);
        }

        //2）生成应用ID 应用Id使用UUID方式生成。
        String appId = UUID.randomUUID().toString();

        //调用appMapper向应用表插入数据
        App entity = AppConvert.INSTANCE.dto2entity(appDTO);
        entity.setAppId(appId);
        entity.setMerchantId(merchantId);
        appMapper.insert(entity);
        return AppConvert.INSTANCE.entity2dto(entity);
    }

    /**
     *
     * @param MerchantId 商户id
     * @return
     * @throws BusinessException
     */
    @Override
    public List<AppDTO> queryAppByMerchant(Long MerchantId) throws BusinessException {
        List<App> apps = appMapper.selectList(new LambdaQueryWrapper<App>().eq(App::getMerchantId, MerchantId));
        return AppConvert.INSTANCE.listentity2dto(apps);
    }

    /**
     *
     * @param appId
     * @return
     * @throws BusinessException
     */
    @Override
    public AppDTO getAppById(String appId) throws BusinessException {
        App entity = appMapper.selectOne(new LambdaQueryWrapper<App>().eq(App::getAppId, appId));
        return AppConvert.INSTANCE.entity2dto(entity);
    }

    @Override
    public Boolean queryAppInMerchant(String appId, Long merchantId) {
        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>().eq(App::getAppId, appId).eq(App::getMerchantId, merchantId));
        return count > 0;
    }

    /**
     *
     * @param appName
     * @return
     */
    private Boolean isExistAppName(String appName){
        Integer count = appMapper.selectCount(new LambdaQueryWrapper<App>().eq(App::getAppName, appName));
        return count > 0;
    }
}
