package com.shanjupay.merchant.api;

import com.shanjupay.common.domain.BusinessException;
import com.shanjupay.common.domain.PageVO;
import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.api.dto.StaffDTO;
import com.shanjupay.merchant.api.dto.StoreDTO;

public interface MerchantService {

    /**
     * 根据id查询商户
     * @param id 待查询商户的id
     * @return 商户的查询结果
     */
    MerchantDTO queryMerchantById(Long id);

    /**
     * 注册商户服务接口，接收账号、密码、手机号，为了可扩展性，使用MerchantDTO接收数据
     * @param merchantDTO 商户注册信息
     * @return 注册成功的商户信息
     */
    MerchantDTO createMerchant(MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 资质申请接口
     * @param merchantId 商户id
     * @param merchantDTO 资质申请的信息
     * @throws BusinessException
     */
    void applyMerchant(Long merchantId, MerchantDTO merchantDTO) throws BusinessException;

    /**
     * 新增门店
     * @param storeDTO 门店信息
     * @return 新增成功后的门店信息
     * @throws BusinessException
     */
    StoreDTO createStore(StoreDTO storeDTO) throws BusinessException;

    /**
     * 新增员工
     * @param staffDTO 员工信息
     * @return 新增成功的员工信息
     * @throws BusinessException
     */
    StaffDTO createStaff(StaffDTO staffDTO) throws BusinessException;

    /**
     * 将员工设置为门店的管理员
     * @param storeId
     * @param staffId
     * @throws BusinessException
     */
    void bindStaffToStore(Long storeId, Long staffId) throws BusinessException;

    /**
     * 根据租户id查询商户
     * @param tenantId
     * @return
     */
    MerchantDTO queryMerchantByTenantId(Long tenantId);

    /**
     * 门店列表的查询
     * @param storeDTO 查询条件，必要参数：商户id
     * @param pageNo 页码
     * @param pageSize 分页记录数
     * @return
     */
    PageVO<StoreDTO> queryStoreByPage(StoreDTO storeDTO, Integer pageNo, Integer pageSize);

    /**
     * 查询门店是否属于某商户
     * @param storeId
     * @param merchantId
     * @return
     */
    Boolean queryStoreInMerchant(Long storeId, Long merchantId);
}
