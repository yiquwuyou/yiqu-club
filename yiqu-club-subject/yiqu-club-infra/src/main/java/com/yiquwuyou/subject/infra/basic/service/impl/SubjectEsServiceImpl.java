package com.yiquwuyou.subject.infra.basic.service.impl;

import com.alibaba.fastjson.JSON;
import com.yiquwuyou.subject.infra.basic.entity.SubjectInfoEs;
import com.yiquwuyou.subject.infra.basic.esRepo.SubjectEsRepository;
import com.yiquwuyou.subject.infra.basic.service.SubjectEsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SubjectEsServiceImpl是一个用于操作Elasticsearch中的SubjectInfoEs对象的服务类。
 */
@Service
@Slf4j
public class SubjectEsServiceImpl implements SubjectEsService {

    // 使用@Resource注解自动注入ElasticsearchRestTemplate对象
    // ElasticsearchRestTemplate是Spring Data Elasticsearch提供的用于操作Elasticsearch的高级模板
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    // 使用@Resource注解自动注入SubjectEsRepository对象
    // SubjectEsRepository是Spring Data Elasticsearch自动生成的仓库接口实现，用于操作SubjectInfoEs对象
    @Resource
    private SubjectEsRepository subjectEsRepository;

    // 实现SubjectEsService接口中的createIndex方法，用于创建Elasticsearch索引
    @Override
    public void createIndex() {
        // 获取SubjectInfoEs类对应的索引操作对象
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(SubjectInfoEs.class);
        // 创建索引，如果索引已经存在则不会重复创建
        indexOperations.create();
        // 创建映射（Mapping），这通常是在索引创建时自动完成的，但也可以显式地执行
        // 注意：在某些Spring Data Elasticsearch版本中，创建映射可能不需要手动调用putMapping
        Document mapping = indexOperations.createMapping(SubjectInfoEs.class);
        indexOperations.putMapping(mapping);
    }

    // 实现SubjectEsService接口中的addDoc方法，用于向Elasticsearch添加文档
    @Override
    public void addDoc() {
        // 创建一个SubjectInfoEs对象的列表
        List<SubjectInfoEs> list = new ArrayList<>();
        // 向列表中添加两个SubjectInfoEs对象
        list.add(new SubjectInfoEs(1L, "redis是什么", "redis是一个缓存", "鸡翅", new Date()));
        list.add(new SubjectInfoEs(2L, "mysql是什么", "mysql是数据库", "鸡翅", new Date()));
        // 使用SubjectEsRepository的saveAll方法批量保存对象到Elasticsearch
        subjectEsRepository.saveAll(list);
    }

    // 实现SubjectEsService接口中的find方法，用于查询Elasticsearch中的所有文档
    @Override
    public void find() {
        // 使用SubjectEsRepository的findAll方法查询所有文档
        Iterable<SubjectInfoEs> all = subjectEsRepository.findAll();
        // 遍历查询结果，并使用log记录每个文档的信息
        // JSON.toJSONString是阿里巴巴的fastjson库提供的方法，用于将对象转换为JSON字符串
        for (SubjectInfoEs subjectInfoEs : all) {
            log.info("subjectInfoEs:{}", JSON.toJSONString(subjectInfoEs));
        }
    }

    // 实现SubjectEsService接口中的search方法，用于在Elasticsearch中进行搜索
    @Override
    public void search() {
        // 使用NativeSearchQueryBuilder构建查询对象
        // 这里构建了一个match查询，用于在subjectName字段中搜索包含"redis"的文档
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("subjectName", "redis"))
                .build();
        // 执行查询并获取结果
        SearchHits<SubjectInfoEs> search = elasticsearchRestTemplate.search(nativeSearchQuery, SubjectInfoEs.class);
        // 获取查询到的文档列表
        List<SearchHit<SubjectInfoEs>> searchHits = search.getSearchHits();
        // 使用log记录查询到的文档列表信息
        log.info("searchHits:{}", JSON.toJSONString(searchHits));
    }

}
