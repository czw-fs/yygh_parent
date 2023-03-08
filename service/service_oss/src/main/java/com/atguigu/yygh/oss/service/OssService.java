package com.atguigu.yygh.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.atguigu.yygh.oss.prop.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * @author: fs
 * @date: 2023/2/22 17:59
 * @Description: everything is ok
 */
@Service
public class OssService {

    @Autowired
    private OssProperties ossProperties;

    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ossProperties.getEndpoint();
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ossProperties.getKeyid();
        String accessKeySecret = ossProperties.getKeysecret();
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ossProperties.getBucketname();


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        //生成唯一文件名
        String fileName = new DateTime().toString("yyyy/MM/dd") + UUID.randomUUID().toString().replaceAll("-", "")+file.getOriginalFilename();


        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());
            // 设置该属性可以返回response。如果不设置，则返回的response为空。
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 如果上传成功，则返回200。
            System.out.println(result.getResponse().getStatusCode());

            return "https://" + ossProperties.getBucketname() + "." + ossProperties.getEndpoint() + "/" + fileName;
        } catch (Exception ce) {
            System.out.println(ce.getMessage());

            return null;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

