package com.yiquwuyou.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 刷题微服务启动类
 *
 * @author: yiquwuyou
 * @date: 2023/10/1
 */
@SpringBootApplication
@ComponentScan("com.yiquwuyou")
@MapperScan("com.yiquwuyou.**.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class);
    }

}