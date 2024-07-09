package com.yiquwuyou.subject.common.context;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录上下文对象
 * 用于存储和管理与当前线程关联的登录信息，如用户ID等。
 * 使用InheritableThreadLocal确保这些信息可以在线程间继承。
 *
 * @author: ChickenWing
 * @date: 2023/11/26
 */
public class LoginContextHolder {

    // 定义一个InheritableThreadLocal变量，用于存储Map<String, Object>类型的线程局部变量
    // InheritableThreadLocal允许子线程继承父线程中的ThreadLocal变量的值
    private static final InheritableThreadLocal<Map<String, Object>> THREAD_LOCAL
            = new InheritableThreadLocal<>();

    /**
     * 设置当前线程的登录上下文信息
     *
     * @param key   键，通常用于指定要存储的信息类型，如"loginId"
     * @param val   值，与键关联的信息值
     */
    public static void set(String key, Object val) {
        // 获取当前线程的ThreadLocalMap
        Map<String, Object> map = getThreadLocalMap();
        // 将键值对放入ThreadLocalMap中
        map.put(key, val);
    }

    /**
     * 获取当前线程指定键的登录上下文信息
     *
     * @param key 键，用于指定要获取的信息类型
     * @return 返回与键关联的信息值，如果未找到则返回null
     */
    public static Object get(String key){
        // 获取当前线程的ThreadLocalMap
        Map<String, Object> threadLocalMap = getThreadLocalMap();
        // 从ThreadLocalMap中获取指定键的值
        return threadLocalMap.get(key);
    }

    /**
     * 快捷方法，用于获取当前线程的登录ID
     *
     * @return 返回当前线程的登录ID，如果未设置则返回null
     */
    public static String getLoginId(){
        // 直接从ThreadLocalMap中获取"loginId"键对应的值，并强制转换为String类型
        return (String) getThreadLocalMap().get("loginId");
    }

    /**
     * 移除当前线程的登录上下文信息
     * 实际上是移除整个ThreadLocalMap，以释放资源
     */
    public static void remove(){
        // 调用InheritableThreadLocal的remove方法，移除当前线程的ThreadLocalMap
        THREAD_LOCAL.remove();
    }

    /**
     * 获取当前线程的ThreadLocalMap
     * 如果当前线程还没有ThreadLocalMap，则创建一个新的ConcurrentHashMap并设置到ThreadLocal中
     *
     * @return 返回当前线程的ThreadLocalMap
     */
    public static Map<String, Object> getThreadLocalMap() {
        // 尝试从InheritableThreadLocal中获取当前线程的ThreadLocalMap
        Map<String, Object> map = THREAD_LOCAL.get();
        // 如果map为空（即当前线程还没有ThreadLocalMap），则创建一个新的ConcurrentHashMap
        if (map == null) {
            map = new ConcurrentHashMap<>();
            // 将新的ConcurrentHashMap设置到InheritableThreadLocal中，与当前线程关联
            THREAD_LOCAL.set(map);
        }
        // 返回当前线程的ThreadLocalMap
        return map;
    }

    // 注意：这里虽然使用了Objects.isNull进行空检查会更优雅，但考虑到Java标准库中没有Objects.isNull方法（可能是笔误，应为Objects.isNull(map)），所以直接使用map == null进行判断。
}