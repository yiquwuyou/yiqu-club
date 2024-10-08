package com.yiquwuyou.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * oss服务启动器
 * 
 * @author: yiquwuyou
 * @date: 2023/10/11
 */
@SpringBootApplication
@ComponentScan("com.yiquwuyou")
public class OssApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class);
    }

}
