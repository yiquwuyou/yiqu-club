package com.yiquwuyou.subject.domain.handler.subject;

import com.yiquwuyou.subject.common.enums.SubjectInfoTypeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目类型工厂
 *
 * @author: yiquwuyou
 * @date: 2023/10/5
 */
@Component
public class SubjectTypeHandlerFactory implements InitializingBean {

    // 通过@Resource注解注入所有的SubjectTypeHandler实现类
    @Resource
    private List<SubjectTypeHandler> subjectTypeHandlerList;

    // map集合，用来存取 SubjectTypeHandler实现类，以SubjectInfoTypeEnum为key
    private Map<SubjectInfoTypeEnum, SubjectTypeHandler> handlerMap = new HashMap<>();

    // 通过subjectType获取对应的SubjectTypeHandler实现类
    public SubjectTypeHandler getHandler(int subjectType) {
        SubjectInfoTypeEnum subjectInfoTypeEnum = SubjectInfoTypeEnum.getByCode(subjectType);
        return handlerMap.get(subjectInfoTypeEnum);
    }

    // 通过afterPropertiesSet方法将所有的SubjectTypeHandler实现类放入handlerMap中
    @Override
    public void afterPropertiesSet() throws Exception {
        for (SubjectTypeHandler subjectTypeHandler : subjectTypeHandlerList) {
            handlerMap.put(subjectTypeHandler.getHandlerType(), subjectTypeHandler);
        }
    }

}