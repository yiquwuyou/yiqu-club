package com.yiquwuyou.subject.infra.basic.esRepo;

import com.yiquwuyou.subject.infra.basic.entity.SubjectInfoEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * SubjectEsRepository是一个用于与Elasticsearch中的SubjectInfoEs对象集合进行交互的接口。
 * 通过继承ElasticsearchRepository，它获得了一套标准的CRUD操作，并且还可以根据需要添加自定义的查询方法
 * 类似于操作mysql过程中的mapper层
 */
@Component
public interface SubjectEsRepository extends ElasticsearchRepository<SubjectInfoEs, Long> {
}
