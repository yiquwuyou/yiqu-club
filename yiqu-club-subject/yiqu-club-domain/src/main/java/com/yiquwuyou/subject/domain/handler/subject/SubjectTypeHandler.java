package com.yiquwuyou.subject.domain.handler.subject;

import com.yiquwuyou.subject.common.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.subject.domain.entity.SubjectInfoBO;
import com.yiquwuyou.subject.domain.entity.SubjectOptionBO;

/**
 * 定义接口，所有策略模式的实现类都要实现这个接口
 * 这样通过该接口就可以访问到对应的实现类
 */
public interface SubjectTypeHandler {

    /**
     * 枚举身份的识别
     */
    SubjectInfoTypeEnum getHandlerType();

    /**
     * 实际的题目的插入
     */
    void add(SubjectInfoBO subjectInfoBO);

    /**
     * 实际的题目的查询
     */
    SubjectOptionBO query(int subjectId);
}