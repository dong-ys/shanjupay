package com.shanjupay.common.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 七牛云测试工具类
 */
public class QiniuUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUtils.class);

    /**
     * 文件上传的工具方法
     * @param accessKey
     * @param secretKey
     * @param bucket
     * @param bytes 文件字节数组
     * @param fileName 外部传进来，七牛云上的文件名和此保持一致
     */
    public static void upload2qiniu(String accessKey, String secretKey, String bucket, byte[] bytes, String fileName) throws RuntimeException{
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        try {
            //认证
            Auth auth = Auth.create(accessKey, secretKey);
            //认证通过后得到token(令牌)
            String upToken = auth.uploadToken(bucket);
            //上传参数：字节数组，key，token令牌
            //key：建议自己生成一个不重复的名称
            try {
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                LOGGER.error("上传文件到七牛: {}", ex.getMessage());
                try {
                    LOGGER.error(r.bodyString());
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
                throw new RuntimeException(r.bodyString());
            }
        }catch (Exception ex){
            LOGGER.error("上传文件到七牛: {}", ex.getMessage());
            throw new RuntimeException();
        }
    }

    //测试文件上传
    private static void testUpload() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "GFoUrDSZejTDpEbjZhKv5DISfukm0leiBuzo_1k-";
        String secretKey = "j9vDdcHOH8e2mVKNbEmYsqTKYHUlSv9jnzFSPnAv";
        String bucket = "shanjupay-dys";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = UUID.randomUUID().toString() + ".png";
        FileInputStream fileInputStream = null;
        try {
            String filePath = "C:\\Users\\dongys\\Desktop\\1.jpg";
            fileInputStream = new FileInputStream(filePath);
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            //byte[] uploadBytes = "hello qiniu cloud".getBytes(StandardCharsets.UTF_8);
            //认证
            Auth auth = Auth.create(accessKey, secretKey);
            //认证通过后得到token(令牌)
            String upToken = auth.uploadToken(bucket);
            //上传参数：字节数组，key，token令牌
            //key：建议自己生成一个不重复的名称
            try {
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getDownLoadUrl() throws UnsupportedEncodingException {
        String fileName = "3b48c236-7c89-42bc-8ef7-141d4344304d.png";
        String domainOfBucket = "http://quulrz3vx.hd-bkt.clouddn.com";
        String encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        String accessKey = "GFoUrDSZejTDpEbjZhKv5DISfukm0leiBuzo_1k-";
        String secretKey = "j9vDdcHOH8e2mVKNbEmYsqTKYHUlSv9jnzFSPnAv";
        Auth auth = Auth.create(accessKey, secretKey);
        long expireInSeconds = 3600;//1小时，可以自定义链接过期时间
        String finalUrl = auth.privateDownloadUrl(publicUrl, expireInSeconds);
        System.out.println(finalUrl);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        QiniuUtils.getDownLoadUrl();
        //System.exit(0);
    }
}
