package com.zzyl.oss;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AliyunOssUtils {

    @Autowired
    private AliyunOssProperties aliyunOssProperties;

    /**
     * 阿里云OSS 上传文件
     * @param file
     * @return 返回文件上传到服务器的路径
     */
    public String uploadFile(MultipartFile file) {

        String accessKeyId = aliyunOssProperties.getAccessKeyId();
        String secretAccessKey = aliyunOssProperties.getSecretAccessKey();
        String endpoint = aliyunOssProperties.getEndpoint();
        String region = aliyunOssProperties.getRegion();
        String bucketName = aliyunOssProperties.getBucketName();
        String domain = aliyunOssProperties.getDomain();

        // 1.参数校验
        if(ObjUtil.isEmpty(file) || file.isEmpty()){
            throw new RuntimeException("上传文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if(StrUtil.isEmpty(filename)){
            throw new RuntimeException("上传文件名错误！");
        }
        String path = null;
        OSS ossClient = null;
        try {
            CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, secretAccessKey);
            ossClient = OSSClientBuilder.create()
                    .endpoint(endpoint)
                    .credentialsProvider(credentialsProvider)
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .build();
            //.jpg
            String suffix = filename.substring(filename.lastIndexOf("."));
            filename = DateUtils.datePath() + "/"
                    + UUID.randomUUID().toString().replaceAll("-", "")
                    + suffix;

            ossClient.putObject(bucketName, filename, file.getInputStream());
            path = "https://" + bucketName + "." + domain + "/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return path;
    }

}