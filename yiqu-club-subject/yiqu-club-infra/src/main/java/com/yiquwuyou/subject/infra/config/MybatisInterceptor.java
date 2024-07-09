package com.yiquwuyou.subject.infra.config;

import com.yiquwuyou.subject.common.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * MybatisInterceptor类是一个MyBatis拦截器，用于在执行数据库操作前自动填充一些公共字段，如createBy, createTime等。
 * 当执行插入（INSERT）或更新（UPDATE）操作时，它会获取当前登录用户的ID，并将其设置为createBy或updateBy字段的值。
 * 同时，它也会设置createTime或updateTime字段的值为当前时间。
 * 如果操作的对象是一个Map，它会遍历Map中的所有值，并对每个值执行上述操作。
 * 这个拦截器主要用于自动管理数据的创建和更新信息，无需手动设置这些公共字段。
 *
 * @author: ChickenWing
 * @date: 2024/1/5
 */
@Component
@Slf4j
@Intercepts({@Signature(type = Executor.class, method = "update", args = {
        MappedStatement.class, Object.class
})})
public class MybatisInterceptor implements Interceptor {

    /**
     * 拦截器的主要方法，用于在执行数据库操作前自动填充一些公共字段
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取MappedStatement和SqlCommandType
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        // 获取操作的对象
        Object parameter = invocation.getArgs()[1];
        if (parameter == null) {
            return invocation.proceed();
        }
        // 获取当前登录用户的id
        String loginId = LoginUtil.getLoginId();
        if (StringUtils.isBlank(loginId)) {
            return invocation.proceed();
        }
        // 如果是插入或更新操作，替换实体属性
        if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
            replaceEntityProperty(parameter, loginId, sqlCommandType);
        }
        return invocation.proceed();
    }

    /**
     * 替换实体属性
     */
    private void replaceEntityProperty(Object parameter, String loginId, SqlCommandType sqlCommandType) {
        // 如果操作的对象是一个Map，遍历Map中的所有值，并对每个值执行替换操作
        if (parameter instanceof Map) {
            replaceMap((Map) parameter, loginId, sqlCommandType);
        } else {
            // 如果操作的对象不是Map，直接执行替换操作
            replace(parameter, loginId, sqlCommandType);
        }
    }

    /**
     * 替换Map中的所有值
     */
    private void replaceMap(Map parameter, String loginId, SqlCommandType sqlCommandType) {
        for (Object val : parameter.values()) {
            replace(val, loginId, sqlCommandType);
        }
    }

    /**
     * 替换对象的属性
     */
    private void replace(Object parameter, String loginId, SqlCommandType sqlCommandType) {
        // 如果是插入操作，处理插入
        if (SqlCommandType.INSERT == sqlCommandType) {
            dealInsert(parameter, loginId);
        } else {
            // 如果是更新操作，处理更新
            dealUpdate(parameter, loginId);
        }
    }

    /**
     * 处理更新操作，设置updateBy和updateTime字段的值
     */
    private void dealUpdate(Object parameter, String loginId) {
        Field[] fields = getAllFields(parameter);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object o = field.get(parameter);
                if (Objects.nonNull(o)) {
                    field.setAccessible(false);
                    continue;
                }
                if ("updateBy".equals(field.getName())) {
                    field.set(parameter, loginId);
                    field.setAccessible(false);
                } else if ("updateTime".equals(field.getName())) {
                    field.set(parameter, new Date());
                    field.setAccessible(false);
                } else {
                    field.setAccessible(false);
                }
            } catch (Exception e) {
                log.error("dealUpdate.error:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 处理插入操作，设置createBy, createTime和isDeleted字段的值
     */
    private void dealInsert(Object parameter, String loginId) {
        Field[] fields = getAllFields(parameter);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object o = field.get(parameter);
                if (Objects.nonNull(o)) {
                    field.setAccessible(false);
                    continue;
                }
                if ("isDeleted".equals(field.getName())) {
                    field.set(parameter, 0);
                    field.setAccessible(false);
                } else if ("createdBy".equals(field.getName())) {
                    field.set(parameter, loginId);
                    field.setAccessible(false);
                } else if ("createdTime".equals(field.getName())) {
                    field.set(parameter, new Date());
                    field.setAccessible(false);
                } else {
                    field.setAccessible(false);
                }
            } catch (Exception e) {
                log.error("dealInsert.error:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 获取对象的所有字段
     */
    private Field[] getAllFields(Object object) {
        Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    /**
     * 生成拦截器的代理对象
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置拦截器的属性，此方法暂未使用
     */
    @Override
    public void setProperties(Properties properties) {
    }

}