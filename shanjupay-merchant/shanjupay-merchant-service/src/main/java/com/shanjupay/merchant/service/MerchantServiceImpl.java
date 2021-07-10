package com.shanjupay.merchant.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.CommonErrorCode;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.common.util.PhoneUtil;
import com.shanjupay.merchant.api.MerchantService;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;
import com.shanjupay.merchant.api.dto.StoreStaffDTO;
import com.shanjupay.merchant.convert.MerchantConvert;
import com.shanjupay.merchant.convert.StaffConvert;
import com.shanjupay.merchant.convert.StoreConvert;
import com.shanjupay.merchant.entity.Merchant;
import com.shanjupay.merchant.entity.Staff;
import com.shanjupay.merchant.entity.Store;
import com.shanjupay.merchant.entity.StoreStaff;
import com.shanjupay.merchant.mapper.MerchantMapper;
import com.shanjupay.merchant.mapper.StaffMapper;
import com.shanjupay.merchant.mapper.StoreMapper;
import com.shanjupay.merchant.mapper.StoreStaffMapper;
import com.shanjupay.user.api.TenantService;
import com.shanjupay.user.api.dto.tenant.CreateTenantRequestDTO;
import com.shanjupay.user.api.dto.tenant.TenantDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantMapper merchantMapper;

    @Autowired
    StoreMapper storeMapper;

    @Autowired
    StaffMapper staffMapper;

    @Autowired
    StoreStaffMapper storeStaffMapper;

    @Reference
    TenantService tenantService;

    /**
     *
     * @param id 待查询商户的id
     * @return
     */
    @Override
    public MerchantDTO queryMerchantById(Long id) {
        Merchant merchant = merchantMapper.selectById(id);
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    /**
     * 调用SaaS接口：新增租户、用户、绑定租户和用户的关系，初始化权限
     * @param merchantDTO 商户注册信息
     * @return
     */
    @Override
    public MerchantDTO createMerchant(MerchantDTO merchantDTO) {
        //校验参数的合法性
        if (merchantDTO == null){
            throw new BusinessException(CommonErrorCode.E_100108);
        }
        if (StringUtils.isBlank(merchantDTO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100112);
        }
        if (StringUtils.isBlank(merchantDTO.getPassword())){
            throw new BusinessException(CommonErrorCode.E_100111);
        }
        if (StringUtils.isBlank(merchantDTO.getUsername())){
            throw new BusinessException(CommonErrorCode.E_100110);
        }
        //手机格式校验
        if(!PhoneUtil.isMatches(merchantDTO.getMobile())){
            throw new BusinessException(CommonErrorCode.E_100109);
        }
        //校验手机号的唯一性
        //根据手机号查询商户表，如果存在记录则说明手机号存在
        Integer count = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getMobile, merchantDTO.getMobile()));
        if (count > 0){
            throw new BusinessException(CommonErrorCode.E_100113);
        }

        //调用SaaS接口
        //构造调用的参数
        CreateTenantRequestDTO createTenantRequestDTO = new CreateTenantRequestDTO();
        createTenantRequestDTO.setMobile(merchantDTO.getMobile());
        createTenantRequestDTO.setUsername(merchantDTO.getUsername());
        createTenantRequestDTO.setPassword(merchantDTO.getPassword());
        createTenantRequestDTO.setTenantTypeCode("shanju-merchant"); //租户类型
        createTenantRequestDTO.setBundleCode("shanju-merchant"); //套餐，根据套餐进行分配权限
        createTenantRequestDTO.setName(merchantDTO.getUsername()); //租户名和账号名一样
        //如果租户在Saas已经存在，SaaS直接返回，否则添加
        TenantDTO tenantAndAccount = tenantService.createTenantAndAccount(createTenantRequestDTO);
        //获取租户的id
        if (tenantAndAccount == null || tenantAndAccount.getId() == null){
            throw new BusinessException(CommonErrorCode.E_200012);
        }
        Long tenantId = tenantAndAccount.getId();

        //租户id在商户表唯一
        //根据租户id从商户表查询，如果存在则不允许添加商户
        Integer count1 = merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        if (count1 > 0){
            throw new BusinessException(CommonErrorCode.E_200017);
        }

        //使用MapStruct进行对象转换
        Merchant merchant = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //设置所对应的租户的Id
        merchant.setTenantId(tenantId);
        //设置审核状态为0-未进行资质申请
        merchant.setAuditStatus("0");
        //merchant.setMobile(merchantDTO.getMobile());
        merchantMapper.insert(merchant);

        //新增门店
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setStoreName("根门店");
        storeDTO.setMerchantId(merchant.getId());
        storeDTO = createStore(storeDTO);

        //新增员工
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setMobile(merchantDTO.getMobile());
        staffDTO.setUsername(merchantDTO.getUsername());
        staffDTO.setStoreId(storeDTO.getId()); //员工所属门店id
        staffDTO.setMerchantId(merchant.getId());
        staffDTO = createStaff(staffDTO);

        //为门店设置管理员
        bindStaffToStore(storeDTO.getId(), staffDTO.getId());

        MerchantDTO merchantDTOResult = MerchantConvert.INSTANCE.entity2dto(merchant);
        return merchantDTOResult;
    }

    /**
     *
     * @param merchantId 商户id
     * @param merchantDTO 资质申请的信息
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException {
        if (merchantId == null || merchantDTO == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //校验merchantId合法性
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null){
            throw new BusinessException(CommonErrorCode.E_200002);
        }

        //将dto转为entity
        Merchant entity = MerchantConvert.INSTANCE.dto2entity(merchantDTO);
        //将必要的参数设置到entity
        entity.setId(merchant.getId());
        entity.setMobile(merchant.getMobile());
        entity.setAuditStatus("1");
        entity.setTenantId(merchant.getTenantId());
        //调用mapper更新商户表
        merchantMapper.updateById(entity);
    }

    @Override
    public StoreDTO createStore(StoreDTO storeDTO) throws BusinessException {
        Store entity = StoreConvert.INSTANCE.dto2entity(storeDTO);
        log.info("新增门店: {}", JSON.toJSONString(entity));
        storeMapper.insert(entity);
        return StoreConvert.INSTANCE.entity2dto(entity);
    }

    @Override
    public StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException {
        //参数合法性校验
        if (staffDTO == null || StringUtils.isBlank(staffDTO.getMobile())
                || StringUtils.isBlank(staffDTO.getUsername())
                || staffDTO.getStoreId() == null){
            throw new BusinessException(CommonErrorCode.E_300009);
        }
        //在同一个商户下 员工的账号和手机号唯一
        if (isExistStaffByMobile(staffDTO.getMobile(), staffDTO.getMerchantId())){
            throw new BusinessException(CommonErrorCode.E_100113);
        }
        if (isExistStaffByUsername(staffDTO.getUsername(), staffDTO.getMerchantId())){
            throw new BusinessException(CommonErrorCode.E_100114);
        }
        Staff entity = StaffConvert.INSTANCE.dto2entity(staffDTO);
        //设置员工的所属门店
        //entity.setStoreId();
        staffMapper.insert(entity);
        return StaffConvert.INSTANCE.entity2dto(entity);
    }

    @Override
    public void bindStaffToStore(Long storeId, Long staffId) throws BusinessException {
        StoreStaff storeStaff = new StoreStaff();
        storeStaff.setStaffId(staffId);
        storeStaff.setStoreId(storeId);
        storeStaffMapper.insert(storeStaff);
    }

    @Override
    public MerchantDTO queryMerchantByTenantId(Long tenantId) {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getTenantId, tenantId));
        return MerchantConvert.INSTANCE.entity2dto(merchant);
    }

    @Override
    public PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize) {
        //分页条件
        Page<Store> page = new Page<>(pageNo, pageSize);
        //查询条件拼装
        LambdaQueryWrapper<Store> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //如果传入商户id，此时要拼装查询条件
        if (storeDTO != null && storeDTO.getMerchantId() != null){
            lambdaQueryWrapper.eq(Store::getMerchantId, storeDTO.getMerchantId());
        }
        //再拼装其他查询条件
        if (storeDTO != null && StringUtils.isNotEmpty(storeDTO.getStoreName())){
            lambdaQueryWrapper.eq(Store::getStoreName, storeDTO.getStoreName());
        }
        //查询数据库
        IPage<Store> storeIPage = storeMapper.selectPage(page, lambdaQueryWrapper);
        List<Store> records = storeIPage.getRecords();
        List<StoreDTO> storeDTOS = StoreConvert.INSTANCE.listentity2dto(records);
        return new PageVO(storeDTOS, storeIPage.getTotal(), pageNo, pageSize);
    }

    @Override
    public Boolean queryStoreInMerchant(Long storeId, Long merchantId) {
        Integer count = storeMapper.selectCount(new LambdaQueryWrapper<Store>().eq(Store::getId, storeId).eq(Store::getMerchantId, merchantId));
        return count > 0;
    }

    /**
     * 员工手机号在同一个商户下是唯一的
     * @param mobile
     * @param merchantId
     * @return
     */
    private Boolean isExistStaffByMobile(String mobile, Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getMobile, mobile).eq(Staff::getMerchantId, merchantId));
        return count > 0;
    }

    /**
     * 员工账号在同一个商户下是唯一的
     * @param username
     * @param merchantId
     * @return
     */
    private Boolean isExistStaffByUsername(String username, Long merchantId){
        Integer count = staffMapper.selectCount(new LambdaQueryWrapper<Staff>().eq(Staff::getUsername, username).eq(Staff::getMerchantId, merchantId));
        return count > 0;
    }
}
