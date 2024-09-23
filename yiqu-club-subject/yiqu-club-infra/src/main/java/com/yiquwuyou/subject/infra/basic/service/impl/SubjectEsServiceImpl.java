package com.yiquwuyou.subject.infra.basic.service.impl;

import com.alibaba.fastjson.JSON;
import com.yiquwuyou.subject.common.entity.PageResult;
import com.yiquwuyou.subject.common.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.subject.infra.basic.entity.EsSubjectFields;
import com.yiquwuyou.subject.infra.basic.entity.SubjectInfoEs;
import com.yiquwuyou.subject.infra.basic.es.EsIndexInfo;
import com.yiquwuyou.subject.infra.basic.es.EsRestClient;
import com.yiquwuyou.subject.infra.basic.es.EsSearchRequest;
import com.yiquwuyou.subject.infra.basic.es.EsSourceData;

import com.yiquwuyou.subject.infra.basic.service.SubjectEsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

// 使用@Service注解将此类标记为Spring的Service层组件
// @Slf4j注解自动为当前类生成一个日志对象log，方便进行日志记录
@Service
@Slf4j
public class SubjectEsServiceImpl implements SubjectEsService {

    /**
     * 将SubjectInfoEs对象的数据插入到Elasticsearch中。
     *
     * @param subjectInfoEs 要插入的SubjectInfoEs对象
     * @return 插入操作是否成功
     */
    @Override
    public boolean insert(SubjectInfoEs subjectInfoEs) {
        // 创建一个EsSourceData对象，用于封装要插入Elasticsearch的数据
        EsSourceData esSourceData = new EsSourceData();
        // 将SubjectInfoEs对象转换为Map<String, Object>格式，以便Elasticsearch存储
        Map<String, Object> data = convert2EsSourceData(subjectInfoEs);
        // 设置文档ID
        esSourceData.setDocId(subjectInfoEs.getDocId().toString());
        // 设置数据内容
        esSourceData.setData(data);
        // 调用EsRestClient的insertDoc方法将数据插入Elasticsearch，并返回操作结果
        return EsRestClient.insertDoc(getEsIndexInfo(), esSourceData);
    }

    /**
     * 将SubjectInfoEs对象转换为Map<String, Object>格式，以便Elasticsearch存储。
     *
     * @param subjectInfoEs 要转换的SubjectInfoEs对象
     * @return 转换后的Map对象
     */
    private Map<String, Object> convert2EsSourceData(SubjectInfoEs subjectInfoEs) {
        Map<String, Object> data = new HashMap<>();
        // 将SubjectInfoEs对象的各个字段添加到Map中
        data.put(EsSubjectFields.SUBJECT_ID, subjectInfoEs.getSubjectId());
        data.put(EsSubjectFields.DOC_ID, subjectInfoEs.getDocId());
        data.put(EsSubjectFields.SUBJECT_NAME, subjectInfoEs.getSubjectName());
        data.put(EsSubjectFields.SUBJECT_ANSWER, subjectInfoEs.getSubjectAnswer());
        data.put(EsSubjectFields.SUBJECT_TYPE, subjectInfoEs.getSubjectType());
        data.put(EsSubjectFields.CREATE_USER, subjectInfoEs.getCreateUser());
        data.put(EsSubjectFields.CREATE_TIME, subjectInfoEs.getCreateTime());
        return data;
    }

    /**
     * 定义一个方法，用于查询科目列表，并返回分页结果
     * @param req
     * @return
     */
    @Override
    public PageResult<SubjectInfoEs> querySubjectList(SubjectInfoEs req) {
        // 初始化分页结果对象
        PageResult<SubjectInfoEs> pageResult = new PageResult<>();

        // 根据请求参数构建Elasticsearch搜索请求
        EsSearchRequest esSearchRequest = createSearchListQuery(req);

        // 调用Elasticsearch客户端执行搜索请求
        SearchResponse searchResponse = EsRestClient.searchWithTermQuery(getEsIndexInfo(), esSearchRequest);

        // 初始化用于存储查询结果的列表
        List<SubjectInfoEs> subjectInfoEsList = new LinkedList<>();

        // 从响应中获取搜索命中记录
        SearchHits searchHits = searchResponse.getHits();

        // 如果没有搜索到任何记录，则直接设置分页结果的基本信息并返回
        if (searchHits == null || searchHits.getHits() == null) {
            pageResult.setPageNo(req.getPageNo()); // 设置当前页码
            pageResult.setPageSize(req.getPageSize()); // 设置每页大小
            pageResult.setRecords(subjectInfoEsList); // 设置查询结果列表（空）
            pageResult.setTotal(0); // 设置总记录数为0
            return pageResult; // 返回分页结果
        }

        // 获取具体的命中记录数组
        SearchHit[] hits = searchHits.getHits();

        // 遍历每个命中记录
        for (SearchHit hit : hits) {
            // 将每个命中记录转换为SubjectInfoEs对象
            SubjectInfoEs subjectInfoEs = convertResult(hit);

            // 如果转换结果不为null，则添加到结果列表中
            if (Objects.nonNull(subjectInfoEs)) {
                subjectInfoEsList.add(subjectInfoEs);
            }
        }

        // 设置分页结果的基本信息
        pageResult.setPageNo(req.getPageNo()); // 设置当前页码
        pageResult.setPageSize(req.getPageSize()); // 设置每页大小
        pageResult.setRecords(subjectInfoEsList); // 设置查询结果列表

        // 设置总记录数，注意将长整型转换为整型
        pageResult.setTotal(Long.valueOf(searchHits.getTotalHits().value).intValue());

        // 返回分页结果
        return pageResult;
    }

    /**
     * 将Elasticsearch的SearchHit对象转换为SubjectInfoEs对象，并处理高亮显示。
     *
     * @param hit Elasticsearch的搜索结果中的单个命中项
     * @return 转换后的SubjectInfoEs对象，包含从SearchHit中提取的数据和高亮显示的信息
     */
    private SubjectInfoEs convertResult(SearchHit hit) {
        // 从SearchHit中获取源数据作为Map
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        // 如果源数据为空，则返回null
        if (CollectionUtils.isEmpty(sourceAsMap)) {
            return null;
        }

        // 创建SubjectInfoEs对象用于存储转换后的数据
        SubjectInfoEs result = new SubjectInfoEs();

        // 从源数据中提取SubjectId并设置到result对象中
        result.setSubjectId(MapUtils.getLong(sourceAsMap, EsSubjectFields.SUBJECT_ID));

        // 从源数据中提取SubjectName并设置到result对象中
        result.setSubjectName(MapUtils.getString(sourceAsMap, EsSubjectFields.SUBJECT_NAME));

        // 从源数据中提取SubjectAnswer并设置到result对象中
        result.setSubjectAnswer(MapUtils.getString(sourceAsMap, EsSubjectFields.SUBJECT_ANSWER));

        // 从源数据中提取DocId并设置到result对象中
        result.setDocId(MapUtils.getLong(sourceAsMap, EsSubjectFields.DOC_ID));

        // 从源数据中提取SubjectType并设置到result对象中，注意这里假设SubjectType是Integer类型
        result.setSubjectType(MapUtils.getInteger(sourceAsMap, EsSubjectFields.SUBJECT_TYPE));

        // 计算并设置搜索得分，将Elasticsearch的原始得分乘以100并保留两位小数
        result.setScore(new BigDecimal(String.valueOf(hit.getScore())).multiply(new BigDecimal("100.00"))
                .setScale(2, RoundingMode.HALF_UP));

        // 处理SubjectName的高亮显示
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        HighlightField subjectNameField = highlightFields.get(EsSubjectFields.SUBJECT_NAME);
        if (Objects.nonNull(subjectNameField)) {
            // 获取高亮片段并拼接成完整的字符串
            Text[] fragments = subjectNameField.getFragments();
            StringBuilder subjectNameBuilder = new StringBuilder();
            for (Text fragment : fragments) {
                subjectNameBuilder.append(fragment.toString()); // 注意：这里可能需要调用toString()方法，具体取决于Text类的实现
            }
            // 将拼接后的高亮字符串设置回SubjectName
            result.setSubjectName(subjectNameBuilder.toString());
        }

        // 处理SubjectAnswer的高亮显示，逻辑与SubjectName相同
        HighlightField subjectAnswerField = highlightFields.get(EsSubjectFields.SUBJECT_ANSWER);
        if (Objects.nonNull(subjectAnswerField)) {
            Text[] fragments = subjectAnswerField.getFragments();
            StringBuilder subjectAnswerBuilder = new StringBuilder();
            for (Text fragment : fragments) {
                subjectAnswerBuilder.append(fragment.toString()); // 同样，这里可能需要调用toString()方法
            }
            result.setSubjectAnswer(subjectAnswerBuilder.toString());
        }

        // 返回转换并处理高亮后的SubjectInfoEs对象
        return result;
    }

    /**
     * 创建一个Elasticsearch搜索请求，用于根据给定的查询条件搜索SubjectInfoEs列表。
     *
     * @param req 查询请求参数，包含关键词、页码、页大小等信息
     * @return 构造好的EsSearchRequest对象
     */
    private EsSearchRequest createSearchListQuery(SubjectInfoEs req) {
        // 初始化EsSearchRequest对象，用于封装搜索请求的参数
        EsSearchRequest esSearchRequest = new EsSearchRequest();

        // 创建一个布尔查询构建器，用于构建复杂的布尔逻辑查询
        BoolQueryBuilder bq = new BoolQueryBuilder();

        // 创建一个匹配查询构建器，用于在SubjectName字段中搜索关键词
        MatchQueryBuilder subjectNameQueryBuilder =
                QueryBuilders.matchQuery(EsSubjectFields.SUBJECT_NAME, req.getKeyWord());

        // 将subjectNameQueryBuilder添加到bq的should条件中，表示该条件为可选的
        bq.should(subjectNameQueryBuilder);

        // 为subjectNameQueryBuilder设置权重，使其在搜索结果中优先级更高
        subjectNameQueryBuilder.boost(2);

        // 创建另一个匹配查询构建器，用于在SubjectAnswer字段中搜索关键词
        MatchQueryBuilder subjectAnswerQueryBuilder =
                QueryBuilders.matchQuery(EsSubjectFields.SUBJECT_ANSWER, req.getKeyWord());

        // 将subjectAnswerQueryBuilder也添加到bq的should条件中
        bq.should(subjectAnswerQueryBuilder);

//        // 创建一个匹配查询构建器，用于在SubjectType字段中搜索特定类型（这里以BRIEF为例）
//        MatchQueryBuilder subjectTypeQueryBuilder =
//                QueryBuilders.matchQuery(EsSubjectFields.SUBJECT_TYPE, SubjectInfoTypeEnum.BRIEF.getCode());
//
//        // 将subjectTypeQueryBuilder添加到bq的must条件中，表示该条件为必须的
//        bq.must(subjectTypeQueryBuilder);

        // 设置bq的minimumShouldMatch为1，表示should条件中至少有一个必须匹配
        bq.minimumShouldMatch(1);

        // 创建高亮构建器，用于对搜索结果中的匹配字段进行高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);

        // 设置高亮显示的前缀和后缀标签
        highlightBuilder.preTags("<span style = \"color:red\">");
        highlightBuilder.postTags("</span>");

        // 将布尔查询构建器设置到EsSearchRequest对象中
        esSearchRequest.setBq(bq);

        // 设置高亮构建器
        esSearchRequest.setHighlightBuilder(highlightBuilder);

        // 设置需要返回的字段，这里假设EsSubjectFields.FIELD_QUERY是一个包含需要返回字段的字符串或数组
        esSearchRequest.setFields(EsSubjectFields.FIELD_QUERY);

        // 设置分页参数，从(页码-1)*页大小开始获取数据
        esSearchRequest.setFrom((req.getPageNo() - 1) * req.getPageSize());

        // 设置每页的大小
        esSearchRequest.setSize(req.getPageSize());

        // 设置是否需要开启游标（Scroll），这里设置为false表示不使用游标
        esSearchRequest.setNeedScroll(false);

        // 返回构造好的EsSearchRequest对象
        return esSearchRequest;
    }

    /**
     * 获取Elasticsearch索引信息。
     *
     * @return EsIndexInfo对象，包含Elasticsearch集群名和索引名
     */
    private EsIndexInfo getEsIndexInfo() {
        EsIndexInfo esIndexInfo = new EsIndexInfo();
        // 设置Elasticsearch集群名
        esIndexInfo.setClusterName("4240245a487c");
        // 设置索引名
        esIndexInfo.setIndexName("subject_index");
        return esIndexInfo;
    }
}
