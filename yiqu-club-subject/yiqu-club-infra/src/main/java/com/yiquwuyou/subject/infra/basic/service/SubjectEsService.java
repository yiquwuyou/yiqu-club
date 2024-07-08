package com.yiquwuyou.subject.infra.basic.service;

import com.yiquwuyou.subject.common.entity.PageResult;
import com.yiquwuyou.subject.infra.basic.entity.SubjectInfoEs;

/**
 * SubjectEsService是一个用于操作Elasticsearch中的SubjectInfoEs对象集合的服务接口
 */
public interface SubjectEsService {

    boolean insert(SubjectInfoEs subjectInfoEs);

    PageResult<SubjectInfoEs> querySubjectList(SubjectInfoEs subjectInfoEs);

}
