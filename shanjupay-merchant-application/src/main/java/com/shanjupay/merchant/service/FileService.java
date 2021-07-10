package com.shanjupay.merchant.service;

import com.shanjupay.common.domain.BusinessException;

public interface FileService {

    /**
     * 上传文件
     * @param bytes 文件字节数组
     * @param fileName 数组名
     * @return 文件访问路径(url)
     * @throws BusinessException
     */
    String upload(byte[] bytes, String fileName) throws BusinessException;
}
