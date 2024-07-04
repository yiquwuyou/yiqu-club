package com.yiquwuyou.oss.config;

import com.yiquwuyou.oss.adapter.AliStorageAdapter;
import com.yiquwuyou.oss.adapter.MinioStorageAdapter;
import com.yiquwuyou.oss.adapter.StorageAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储config
 * 在 config 中配置使用哪个文件存储适配器
 * @author: yiquwuyou
 * @date: 2023/10/14
 */
@Configuration
@RefreshScope
public class StorageConfig {

    // yml里找不到的原因是因为对应配置在nacos里
    @Value("${storage.service.type}")
    private String storageType;

    @Bean
    @RefreshScope
    public StorageAdapter storageService() {
        if ("minio".equals(storageType)) {
            return new MinioStorageAdapter();
        } else if ("aliyun".equals(storageType)) {
            return new AliStorageAdapter();
        } else {
            throw new IllegalArgumentException("未找到对应的文件存储处理器");
        }
    }

}