package com.yiquwuyou.subject.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的config管理
 * 这是一个配置类，用于通过Spring框架配置和管理线程池。
 * 通过两个注解，开局自动加载和管理线程池。
 * @author: ChickenWing
 * @date: 2024/7/05
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 获取并配置一个名为"labelThreadPool"的线程池
     *
     * @return 返回配置好的ThreadPoolExecutor实例
     */
    @Bean(name = "labelThreadPool")
    public ThreadPoolExecutor getLabelThreadPool() {
        // 创建一个ThreadPoolExecutor实例，需要指定多个参数
        // 1. corePoolSize：核心线程数，即使它们处于空闲状态，线程池也会保留所指定的线程数
        // 2. maximumPoolSize：线程池允许的最大线程数
        // 3. keepAliveTime：当线程数大于核心线程数时，这是多余空闲线程在终止前等待新任务的最长时间
        // 4. unit：keepAliveTime参数的时间单位
        // 5. workQueue：用于存放待执行任务的阻塞队列
        // 6. threadFactory：用于创建新线程的线程工厂
        // 7. handler：当线程池和队列都满了时的拒绝策略
        return new ThreadPoolExecutor(20, 100, 5,
                TimeUnit.SECONDS, // 时间单位为秒
                new LinkedBlockingDeque<>(40), // 使用LinkedBlockingDeque作为阻塞队列，容量为40
                new CustomNameThreadFactory("label"), // 使用自定义的线程工厂，为线程命名，这里假设CustomNameThreadFactory已定义
                new ThreadPoolExecutor.CallerRunsPolicy()); // 当任务无法被执行时，由提交任务的线程自己执行该任务
    }
    // ThreadPoolExecutor.CallerRunsPolicy是线程池的拒绝策略之一，表示如果无法处理新任务，则将其退回到调用者线程中执行。
}
