package com.yiquwuyou.subject.domain.service;

import com.yiquwuyou.subject.common.entity.PageResult;
import com.yiquwuyou.subject.domain.entity.SubjectInfoBO;

/**
 * 题目领域服务
 * 
 * @author: ChickenWing
 * @date: 2023/10/3
 */
public interface SubjectInfoDomainService {

    /**
     * 新增题目
     */
    void add(SubjectInfoBO subjectInfoBO);

    /**
     * 分页查询
     * @param subjectInfoBO
     * @return
     */
    PageResult<SubjectInfoBO> getSubjectPage(SubjectInfoBO subjectInfoBO);


    /**
     * 查询题目信息
     */
    SubjectInfoBO querySubjectInfo(SubjectInfoBO subjectInfoBO);
}