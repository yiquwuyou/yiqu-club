package com.yiquwuyou.subject.infra.basic.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * 自定义的工具类
 * 封装es连接和操作
 * 初始化 RestHighLevelClient，用于与Elasticsearch集群进行交互
 * 可同时与多个集群进行交互，通过key取到对应的RestHighLevelClient
 * RestHighLevelClient 是 Elasticsearch 官方提供的 Java 客户端库中的一个高级别客户端，用于与 Elasticsearch 集群进行交互
 */
@Component // 声明这个类为Spring的组件，Spring会自动扫描并管理这个类的实例
@Slf4j // Lombok注解，自动生成一个名为log的日志对象，类型为org.slf4j.Logger
public class EsRestClient {

    // 使用HashMap存储不同Elasticsearch集群配置对应的RestHighLevelClient实例
    public static Map<String, RestHighLevelClient> clientMap = new HashMap<>();

    // 注入EsConfigProperties配置类，该类应包含Elasticsearch集群的配置信息
    @Resource
    private EsConfigProperties esConfigProperties;

    // RequestOptions是Elasticsearch客户端（如Elasticsearch Rest High Level Client）中的一个类，用于配置与Elasticsearch服务器的请求相关的各种选项
    private static final RequestOptions COMMON_OPTIONS;

    // 这里，我们使用默认的RequestOptions配置作为基础，然后构建出我们自己的配置
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 初始化方法，用于初始化RestHighLevelClient
     * 将客户端实例放入clientMap中，以集群配置名称为键
     */
    // 使用@PostConstruct注解标记的方法会在依赖注入完成后自动执行，用于初始化操作
    @PostConstruct
    public void initialize() {
        // 从EsConfigProperties中获取Elasticsearch集群的配置列表
        List<EsClusterConfig> esConfigs = esConfigProperties.getEsConfigs();
        // 遍历配置列表，为每个集群配置初始化RestHighLevelClient
        for (EsClusterConfig esConfig : esConfigs) {
            // 记录日志，显示正在初始化的集群配置名称和节点信息
            log.info("initialize.config.name:{},node:{}", esConfig.getName(), esConfig.getNodes());
            // 调用initRestClient方法初始化RestHighLevelClient
            RestHighLevelClient restHighLevelClient = initRestClient(esConfig);
            // 如果初始化成功，则将客户端实例放入clientMap中，以集群配置名称为键
            if (restHighLevelClient != null) {
                clientMap.put(esConfig.getName(), restHighLevelClient);
            } else {
                // 如果初始化失败，则记录错误日志
                log.error("config.name:{},node:{}.initError", esConfig.getName(), esConfig.getNodes());
            }
        }
    }

    /**
     * 根据集群配置名称获取RestHighLevelClient实例
     * RestHighLevelClient 是 Elasticsearch 官方提供的 Java 客户端库中的一个高级别客户端，用于与 Elasticsearch 集群进行交互
     * @param esClusterConfig
     * @return
     */
    // 私有方法，用于根据EsClusterConfig配置初始化RestHighLevelClient
    private RestHighLevelClient initRestClient(EsClusterConfig esClusterConfig) {
        // 将节点信息字符串按逗号分割成数组
        String[] ipPortArr = esClusterConfig.getNodes().split(",");
        // 使用ArrayList存储解析后的HttpHost对象
        // HttpHost是Elasticsearch客户端的节点对象，包含节点的IP和端口号
        List<HttpHost> httpHostList = new ArrayList<>(ipPortArr.length);
        // 遍历ipPortArr数组，解析出每个节点的IP和端口号，并创建HttpHost对象
        for (String ipPort : ipPortArr) {
            // 将IP和端口号字符串按冒号分割
            String[] ipPortInfo = ipPort.split(":");
            // 如果分割后的数组长度为2（即包含IP和端口号），则继续处理
            if (ipPortInfo.length == 2) {
                // 将IP和端口号转换为HttpHost对象，并添加到列表中
                HttpHost httpHost = new HttpHost(ipPortInfo[0], NumberUtils.toInt(ipPortInfo[1]));
                httpHostList.add(httpHost);
            }
        }
        // 将List转换为数组，因为RestClient.builder方法需要HttpHost数组作为参数
        HttpHost[] httpHosts = new HttpHost[httpHostList.size()];
        httpHostList.toArray(httpHosts);

        // 使用RestClientBuilder构建RestClient，然后基于RestClient创建RestHighLevelClient
        RestClientBuilder builder = RestClient.builder(httpHosts);
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
        // 返回初始化好的RestHighLevelClient实例
        return restHighLevelClient;
    }

    // 定义一个静态方法，根据集群名称从clientMap中获取对应的RestHighLevelClient实例
    private static RestHighLevelClient getClient(String clusterName) {
        // 从clientMap中通过集群名称获取对应的RestHighLevelClient实例
        // 如果该集群名称对应的客户端不存在，则返回null
        return clientMap.get(clusterName);
    }

    // 定义一个静态方法，用于向Elasticsearch索引中插入文档
    public static boolean insertDoc(EsIndexInfo esIndexInfo, EsSourceData esSourceData) {
        try {
            // 创建一个IndexRequest对象，指定要插入文档的索引名称
            IndexRequest indexRequest = new IndexRequest(esIndexInfo.getIndexName());
            // 设置文档的源数据（即文档的内容）
            indexRequest.source(esSourceData.getData());
            // 设置文档的ID（如果Elasticsearch索引支持ID生成，则可以省略此步骤）
            indexRequest.id(esSourceData.getDocId());
            // 使用与集群名称关联的RestHighLevelClient实例来执行插入操作
            // COMMON_OPTIONS是一个预定义的RequestOptions实例，用于控制请求的选项（如超时等）
            getClient(esIndexInfo.getClusterName()).index(indexRequest, COMMON_OPTIONS);
            // 如果执行成功，则返回true
            return true;
        } catch (Exception e) {
            // 如果在插入文档的过程中发生异常，则记录错误日志并返回false
            log.error("insertDoc.exception:{}", e.getMessage(), e);
        }
        // 如果捕获到异常或由于某种原因未能成功插入文档，则返回false
        return false;
    }

    // 定义一个静态方法，用于更新Elasticsearch索引中的文档
    public static boolean updateDoc(EsIndexInfo esIndexInfo, EsSourceData esSourceData) {
        try {
            // 创建一个UpdateRequest对象，用于指定更新操作
            UpdateRequest updateRequest = new UpdateRequest();
            // 设置要更新文档的索引名称
            updateRequest.index(esIndexInfo.getIndexName());
            // 设置要更新文档的ID
            updateRequest.id(esSourceData.getDocId());
            // 设置要更新的文档内容（这里假设是部分更新，只更新文档中的某些字段）
            updateRequest.doc(esSourceData.getData());
            // 使用与集群名称关联的RestHighLevelClient实例来执行更新操作
            // COMMON_OPTIONS可能是一个预定义的RequestOptions实例，用于控制请求的选项
            getClient(esIndexInfo.getClusterName()).update(updateRequest, COMMON_OPTIONS);
            // 如果执行成功，则返回true
            return true;
        } catch (Exception e) {
            // 如果在更新文档的过程中发生异常，则记录错误日志并返回false
            log.error("updateDoc.exception:{}", e.getMessage(), e);
        }
        // 如果捕获到异常或由于某种原因未能成功更新文档，则返回false
        return false;
    }

    /**
     * 批量更新Elasticsearch文档。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含集群名称和索引名称。
     * @param esSourceDataList 需要更新的文档列表，每个文档都封装在EsSourceData中。
     * @return 如果所有文档都成功更新或没有文档需要更新，则返回true；如果有任何更新失败，则返回false。
     */
    public static boolean batchUpdateDoc(EsIndexInfo esIndexInfo, List<EsSourceData> esSourceDataList) {
        try {
            boolean flag = false; // 标记是否有文档被添加到批量请求中
            BulkRequest bulkRequest = new BulkRequest(); // 创建批量请求对象

            // 遍历文档列表
            for (EsSourceData esSourceData : esSourceDataList) {
                String docId = esSourceData.getDocId(); // 获取文档ID
                if (StringUtils.isNotBlank(docId)) { // 检查文档ID是否非空
                    UpdateRequest updateRequest = new UpdateRequest(); // 创建更新请求对象
                    updateRequest.index(esIndexInfo.getIndexName()); // 设置索引名称
                    updateRequest.id(esSourceData.getDocId()); // 设置文档ID
                    updateRequest.doc(esSourceData.getData()); // 设置要更新的文档数据
                    bulkRequest.add(updateRequest); // 将更新请求添加到批量请求中
                    flag = true; // 标记有文档被添加到批量请求中
                }
            }

            // 如果有文档被添加到批量请求中，则执行批量更新
            if (flag) {
                BulkResponse bulk = getClient(esIndexInfo.getClusterName()).bulk(bulkRequest, COMMON_OPTIONS); // 发送批量请求
                if (bulk.hasFailures()) { // 检查是否有任何更新失败
                    return false; // 如果有失败，则返回false
                }
            }

            return true; // 所有更新都成功或没有文档需要更新
        } catch (Exception e) {
            log.error("batchUpdateDoc.exception:{}", e.getMessage(), e); // 记录异常信息
        }
        return false; // 发生异常时返回false
    }

    /**
     * 删除Elasticsearch索引中的所有文档。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含集群名称和索引名称。
     * @return 如果删除操作成功，则返回true；如果发生异常，则返回false。
     */
    public static boolean delete(EsIndexInfo esIndexInfo) {
        try {
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(esIndexInfo.getIndexName()); // 创建删除查询请求对象
            deleteByQueryRequest.setQuery(QueryBuilders.matchAllQuery()); // 设置查询条件为匹配所有文档
            BulkByScrollResponse response = getClient(esIndexInfo.getClusterName()).deleteByQuery(deleteByQueryRequest, COMMON_OPTIONS); // 发送删除查询请求
            long deleted = response.getDeleted(); // 获取已删除的文档数量
            log.info("deleted.size:{}", deleted); // 记录已删除的文档数量
            return true; // 删除操作成功
        } catch (Exception e) {
            log.error("delete.exception:{}", e.getMessage(), e); // 记录异常信息
        }
        return false; // 发生异常时返回false
    }

    /**
     * 根据文档ID删除Elasticsearch中的文档。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含索引名称和集群名称。
     * @param docId       要删除的文档的ID。
     * @return 如果文档成功删除或不存在（视为删除成功），则返回true；如果删除过程中发生异常，则返回false。
     */
    public static boolean deleteDoc(EsIndexInfo esIndexInfo, String docId) {
        try {
            // 创建删除请求对象，并设置索引名称和文档ID
            DeleteRequest deleteRequest = new DeleteRequest(esIndexInfo.getIndexName());
            deleteRequest.id(docId);

            // 发送删除请求，并获取响应
            DeleteResponse response = getClient(esIndexInfo.getClusterName()).delete(deleteRequest, COMMON_OPTIONS);

            // 记录删除操作的响应信息（可选，用于调试或日志记录）
            log.info("deleteDoc.response:{}", JSON.toJSONString(response));

            // 假设只要请求发送出去，就认为操作成功（不考虑文档是否实际存在）
            return true;
        } catch (Exception e) {
            // 捕获并记录异常信息
            log.error("deleteDoc.exception:{}", e.getMessage(), e);
        }
        // 如果发生异常，返回false
        return false;
    }

    /**
     * 根据文档ID检查Elasticsearch中文档是否存在。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含索引名称和集群名称。
     * @param docId       要检查的文档的ID。
     * @return 如果文档存在，则返回true；如果不存在或检查过程中发生异常，则返回false。
     */
    public static boolean isExistDocById(EsIndexInfo esIndexInfo, String docId) {
        try {
            // 创建获取请求对象，并设置索引名称和文档ID
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());
            getRequest.id(docId);

            // 发送存在性检查请求，并直接返回结果
            return getClient(esIndexInfo.getClusterName()).exists(getRequest, COMMON_OPTIONS);
        } catch (Exception e) {
            // 捕获并记录异常信息
            log.error("isExistDocById.exception:{}", e.getMessage(), e);
        }
        // 如果发生异常，返回false
        return false;
    }

    /**
     * 根据文档ID获取Elasticsearch中文档的内容。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含索引名称和集群名称。
     * @param docId       要获取的文档的ID。
     * @return 文档的源数据（Map<String, Object>），如果文档不存在或获取过程中发生异常，则返回null。
     */
    public static Map<String, Object> getDocById(EsIndexInfo esIndexInfo, String docId) {
        try {
            // 创建获取请求对象，并设置索引名称和文档ID
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());
            getRequest.id(docId);

            // 发送获取请求，并获取响应
            GetResponse response = getClient(esIndexInfo.getClusterName()).get(getRequest, COMMON_OPTIONS);

            // 如果文档存在，则返回文档的源数据；否则返回null（但实际上，如果文档不存在，这里会抛出异常，除非在客户端配置中允许）
            Map<String, Object> source = response.getSource();
            return source;
        } catch (Exception e) {
            // 捕获并记录异常信息（注意：这里错误地使用了isExistDocById.exception作为日志标签）
            log.error("getDocById.exception:{}", e.getMessage(), e);
        }
        // 如果发生异常或文档不存在（取决于Elasticsearch客户端的配置），返回null
        return null;
    }

    /**
     * 根据文档ID从Elasticsearch中获取指定字段的文档内容。
     *
     * @param esIndexInfo Elasticsearch索引信息，包含索引名和集群名。
     * @param docId       要查询的文档的唯一标识符（ID）。
     * @param fields      需要返回的文档字段数组，如果为空或null，则返回所有字段。
     * @return            包含指定字段的文档内容的Map，如果发生错误或文档不存在则返回null。
     */
    public static Map<String, Object> getDocById(EsIndexInfo esIndexInfo, String docId, String[] fields) {
        try {
            // 创建一个GetRequest对象，用于设置要查询的索引名
            GetRequest getRequest = new GetRequest(esIndexInfo.getIndexName());

            // 设置要查询的文档ID
            getRequest.id(docId);

            // 创建一个FetchSourceContext对象，用于控制哪些字段应该被包含在响应中
            // 参数true表示启用源检索，fields指定了要检索的字段数组，null表示不包含排除字段
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, fields, null);

            // 设置GetRequest对象的fetchSourceContext，即指定哪些字段应该被包含在响应中
            getRequest.fetchSourceContext(fetchSourceContext);

            // 使用getClient方法根据集群名获取Elasticsearch客户端，并执行GetRequest请求
            // COMMON_OPTIONS可能是一个预定义的RequestOptions对象，用于控制请求的选项
            GetResponse response = getClient(esIndexInfo.getClusterName()).get(getRequest, COMMON_OPTIONS);

            // 从GetResponse对象中获取文档的源内容（即文档数据），并将其存储在Map中
            Map<String, Object> source = response.getSource();

            // 返回包含指定字段的文档内容的Map
            return source;
        } catch (Exception e) {
            // 如果在查询过程中发生异常，则记录错误日志
            // 注意：这里的日志记录方法名可能是isExistDocById.exception，但方法名应该是getDocById，这可能是个笔误
            log.error("getDocById.exception:{}", e.getMessage(), e);

            // 如果发生异常，则返回null
            return null;
        }
    }

    /**
     * 使用Term查询或其他布尔查询构建并执行Elasticsearch搜索。
     *
     * @param esIndexInfo       Elasticsearch索引信息，包含索引名和集群名。
     * @param esSearchRequest   封装了搜索请求信息的对象，包括查询条件、字段、分页信息等。
     * @return                  执行搜索后的响应对象，包含搜索结果。如果发生错误，则返回null。
     */
    public static SearchResponse searchWithTermQuery(EsIndexInfo esIndexInfo, EsSearchRequest esSearchRequest) {
        try {
            // 获取布尔查询构建器，它可能包含了Term查询或其他类型的查询条件
            BoolQueryBuilder bq = esSearchRequest.getBq();

            // 获取需要返回的字段数组
            String[] fields = esSearchRequest.getFields();

            // 获取分页的起始位置
            int from = esSearchRequest.getFrom();

            // 获取分页的大小（每页显示的记录数）
            int size = esSearchRequest.getSize();

            // 获取用于滚动查询的时间（以分钟为单位），如果不需要滚动查询，则此值可能不被使用
            Long minutes = esSearchRequest.getMinutes();

            // 获取是否需要滚动查询的标志
            Boolean needScroll = esSearchRequest.getNeedScroll();

            // 获取排序的字段名
            String sortName = esSearchRequest.getSortName();

            // 获取排序的顺序
            SortOrder sortOrder = esSearchRequest.getSortOrder();

            // 创建一个SearchSourceBuilder对象，用于构建搜索请求的主体
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            // 设置查询条件
            searchSourceBuilder.query(bq);

            // 设置需要返回的字段和分页参数
            searchSourceBuilder.fetchSource(fields, null).from(from).size(size);

            // 如果提供了高亮构建器，则添加高亮配置
            if (Objects.nonNull(esSearchRequest.getHighlightBuilder())) {
                searchSourceBuilder.highlighter(esSearchRequest.getHighlightBuilder());
            }

            // 如果指定了排序字段名，则根据该字段进行排序
            if (StringUtils.isNotBlank(sortName)) {
                searchSourceBuilder.sort(sortName, sortOrder); // 注意：这里应该指定排序顺序，但原代码未使用sortOrder变量
            }

            // 总是按照得分降序排序（注意：这可能会覆盖前面根据sortName的排序）
            searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));

            // 创建一个SearchRequest对象，用于执行搜索
            SearchRequest searchRequest = new SearchRequest();

            // 设置搜索类型（这里使用默认类型）
            searchRequest.searchType(SearchType.DEFAULT);

            // 设置要搜索的索引名
            searchRequest.indices(esIndexInfo.getIndexName());

            // 设置搜索请求的源（即前面构建的SearchSourceBuilder对象）
            searchRequest.source(searchSourceBuilder);

            // 如果需要滚动查询，则设置滚动参数
            if (needScroll) {
                Scroll scroll = new Scroll(TimeValue.timeValueMinutes(minutes));
                searchRequest.scroll(scroll);
            }

            // 执行搜索请求，并返回响应
            SearchResponse search = getClient(esIndexInfo.getClusterName()).search(searchRequest, COMMON_OPTIONS);
            return search;
        } catch (Exception e) {
            // 如果在搜索过程中发生异常，则记录错误日志
            log.error("searchWithTermQuery.exception:{}", e.getMessage(), e);
        }
        // 如果发生异常或未找到结果，则返回null
        return null;
    }

    // 定义一个方法，用于批量向Elasticsearch插入文档
    public static boolean batchInsertDoc(EsIndexInfo esIndexInfo, List<EsSourceData> esSourceDataList) {
        // 检查日志级别，如果启用了INFO级别，则打印日志
        if (log.isInfoEnabled()) {
            log.info("批量新增ES文档数量:" + esSourceDataList.size()); // 输出将要批量插入的文档数量
            log.info("目标索引名:" + esIndexInfo.getIndexName()); // 输出目标索引的名称
        }

        try {
            boolean flag = false; // 定义一个标志位，用于记录是否成功添加了至少一个文档到批量请求中
            BulkRequest bulkRequest = new BulkRequest(); // 创建一个批量请求对象

            // 遍历传入的文档列表
            for (EsSourceData source : esSourceDataList) {
                String docId = source.getDocId(); // 获取每个文档的ID
                if (StringUtils.isNotBlank(docId)) { // 检查文档ID是否非空
                    IndexRequest indexRequest = new IndexRequest(esIndexInfo.getIndexName()); // 为每个文档创建一个索引请求，并指定索引名
                    indexRequest.id(docId); // 设置文档的ID
                    indexRequest.source(source.getData()); // 设置文档的内容（通常是JSON字符串）
                    bulkRequest.add(indexRequest); // 将索引请求添加到批量请求中
                    flag = true; // 标记成功添加了至少一个文档
                }
            }

            // 如果成功添加了至少一个文档到批量请求中
            if (flag) {
                // 执行批量请求，并传入Elasticsearch客户端配置和批量请求选项
                BulkResponse response = getClient(esIndexInfo.getClusterName()).bulk(bulkRequest, COMMON_OPTIONS);
                // 检查批量响应中是否有失败的请求
                if (response.hasFailures()) {
                    // 如果有失败的请求，则返回false
                    return false;
                }
            }

            // 如果没有异常发生且所有请求都成功，则返回true
        } catch (Exception e) {
            // 如果发生异常，则记录错误日志
            log.error("batchInsertDoc.error", e);
        }

        // 如果没有异常且至少有一个文档被处理（即使所有文档都失败了，只要至少有一个被尝试插入，这里也会返回true），或者没有文档被处理但也没有异常发生，则返回true
        return true;
    }


    /**
     * 根据查询条件更新Elasticsearch索引中的文档。
     * @param esIndexInfo Elasticsearch索引信息。
     * @param queryBuilder 查询构建器，用于指定哪些文档需要被更新。
     * @param script 脚本，用于定义如何更新文档。
     * @param batchSize 批量处理的大小。
     * @return 总是返回true，因为此方法不处理操作结果的成功与否（依赖于日志记录）。
     */
    public static boolean updateByQuery(EsIndexInfo esIndexInfo, QueryBuilder queryBuilder, Script script, int batchSize) {
        // 如果日志级别允许，记录索引名称
        if (log.isInfoEnabled()) {
            log.info("updateByQuery.indexName:" + esIndexInfo.getIndexName());
        }
        try {
            // 创建UpdateByQueryRequest对象，指定索引名称
            UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest(esIndexInfo.getIndexName());
            // 设置查询条件
            updateByQueryRequest.setQuery(queryBuilder);
            // 设置更新脚本
            updateByQueryRequest.setScript(script);
            // 设置批量处理的大小
            updateByQueryRequest.setBatchSize(batchSize);
            // 设置在版本冲突时是否中止处理（这里设置为不中止）
            updateByQueryRequest.setAbortOnVersionConflict(false);
            // 执行更新操作，并获取响应
            BulkByScrollResponse response = getClient(esIndexInfo.getClusterName()).updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
            // 获取操作失败的列表（这里只是获取了，但没有进行任何处理）
            List<BulkItemResponse.Failure> failures = response.getBulkFailures();
        } catch (Exception e) {
            // 捕获并记录异常
            log.error("updateByQuery.error", e);
        }
        // 方法总是返回true，不基于操作结果
        return true;
    }

    /**
     * 分词方法，使用指定的分词器对文本进行分词。
     * @param esIndexInfo Elasticsearch索引信息（尽管此方法中未直接使用，但可能用于获取集群或索引的特定配置）。
     * @param text 需要分词的文本。
     * @return 分词后的词元列表。
     * @throws Exception 如果分词过程中发生错误，则抛出异常。
     */
    public static List<String> getAnalyze(EsIndexInfo esIndexInfo, String text) throws Exception {
        List<String> list = new ArrayList<String>();
        // 创建一个指向_analyze端点的GET请求
        Request request = new Request("GET", "_analyze");
        // 创建JSON对象来设置请求体
        JSONObject entity = new JSONObject();
        // 设置分词器为ik_smart
        entity.put("analyzer", "ik_smart");
        // 设置需要分词的文本
        entity.put("text", text);
        // 将JSON对象转换为字符串并设置为请求的JSON实体
        request.setJsonEntity(entity.toJSONString());
        // 执行请求并获取响应
        Response response = getClient(esIndexInfo.getClusterName()).getLowLevelClient().performRequest(request);
        // 将响应体转换为JSON对象
        JSONObject tokens = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        // 获取分词结果数组
        JSONArray arrays = tokens.getJSONArray("tokens");
        // 遍历分词结果数组，将每个分词添加到列表中
        for (int i = 0; i < arrays.size(); i++) {
            // 注意：这里假设每个分词结果已经是JSON字符串，但通常tokens数组中的元素已经是JSONObject
            // 这里直接转换为JSONObject可能不必要，但为了符合原始代码的逻辑，保持转换
            JSONObject obj = JSON.parseObject(arrays.getString(i)); // 可能需要调整，如果arrays.getString(i)不适用
            list.add(obj.getString("token")); // 添加分词结果到列表
        }
        // 返回分词后的词元列表
        return list;
    }
}
