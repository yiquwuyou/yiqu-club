package com.yiquwuyou.subject.infra.basic.service;

/**
 * SubjectEsService是一个用于操作Elasticsearch中的SubjectInfoEs对象集合的服务接口
 */
public interface SubjectEsService {

    void createIndex();

    void addDoc();

    void find();

    void search();

}
