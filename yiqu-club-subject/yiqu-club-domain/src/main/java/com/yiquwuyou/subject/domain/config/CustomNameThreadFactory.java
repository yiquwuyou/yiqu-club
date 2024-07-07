package com.yiquwuyou.subject.domain.config;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义名称的线程工厂
 * 用于创建具有自定义名称前缀的线程，便于在调试和日志中识别线程来源。
 * 是我自己基于源码改的，基本没太大的改动，主要线程工厂的名称自定义。
 * @author: ChickenWing
 * @date: 2023/11/26
 */
public class CustomNameThreadFactory implements ThreadFactory {

    // 用于生成唯一的线程池编号
    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    // 线程所属的线程组，用于线程的管理
    private final ThreadGroup group;

    // 用于生成线程编号的原子整数，确保线程编号的唯一性
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    // 线程名称的前缀，包含线程池名称和编号
    private final String namePrefix;

    /**
     * 构造函数
     * 初始化线程工厂，设置线程名称前缀。
     *
     * @param name 线程池的名称，如果为空或仅包含空白字符，则默认为"pool"
     */
    public CustomNameThreadFactory(String name) {
        // 获取系统安全管理器，如果存在，则使用其线程组；否则，使用当前线程的线程组
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        // 如果传入的名称为空或仅包含空白字符，则使用默认名称"pool"
        if (StringUtils.isBlank(name)) {
            name = "pool";
        }

        // 构造线程名称前缀，包括线程池名称、编号和"-thread-"后缀
        namePrefix = name + "-" + poolNumber.getAndIncrement() + "-thread-";
    }

    /**
     * 创建新线程
     * 根据提供的Runnable对象创建并返回一个新的线程。
     *
     * @param r 线程要执行的任务
     * @return 带有自定义名称的新线程
     */
    @Override
    public Thread newThread(Runnable r) {
        // 创建新线程，设置线程组、任务、名称（前缀+编号）和优先级（这里优先级被设置为0，但通常不会这样设置，而是由JVM决定）
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                // 注意：这里的0是栈大小，不是线程优先级。线程优先级在Thread构造函数中不直接设置。
                0);

        // 通常情况下，新创建的线程不是守护线程，这里的检查和设置可能是多余的
        if (t.isDaemon()){
            t.setDaemon(false);
        }

        // 类似地，除非有特殊需求，否则设置线程优先级为NORMAL（默认）通常不是必要的
        if (t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }

        // 返回新创建的线程
        return t;
    }

}
