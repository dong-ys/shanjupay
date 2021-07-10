package com.shanjupay.merchant.convert;

import com.shanjupay.merchant.api.dto.MerchantDTO;
import com.shanjupay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MerchantConvert {

    /**
     * 创建转换类实例
     */
    MerchantConvert INSTANCE = Mappers.getMapper(MerchantConvert.class);

    /**
     * 把dto转换成entity
     */
    Merchant dto2entity(MerchantDTO merchantDTO);

    /**
     * 把entity转换成dto
     */
    MerchantDTO entity2dto(Merchant merchant);

    public static void main(String[] args) {
        Merchant merchant = new Merchant();
        merchant.setUsername("text");
        merchant.setMobile("123456");
        MerchantDTO merchantDTO = MerchantConvert.INSTANCE.entity2dto(merchant);
        System.out.println(merchantDTO);
    }
}
