package com.yiquwuyou.club.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 网关启动器
 * 
 * @author: yiquwuyou
 * @date: 2023/10/11
 */
@SpringBootApplication
@ComponentScan("com.yiquwuyou")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class);
    }

}