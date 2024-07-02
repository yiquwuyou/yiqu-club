package com.yiquwuyou.subject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**\
 * 刷题微服务启动类
 */
@SpringBootApplication
@ComponentScan("com.yiquwuyou")   // 扫描所有spring容器的类，交给spring管理
@MapperScan("com.yiquwuyou.**.mapper")   // 扫描所有mapper接口
public class SubjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubjectApplication.class);
    }
}
