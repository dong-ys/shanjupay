package com.shanjupay.transaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shanjupay.transaction.entity.AppPlatformChannel;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 说明了应用选择了平台中的哪些支付渠道 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2019-11-15
 */
@Repository
public interface AppPlatformChannelMapper extends BaseMapper<AppPlatformChannel> {

}
