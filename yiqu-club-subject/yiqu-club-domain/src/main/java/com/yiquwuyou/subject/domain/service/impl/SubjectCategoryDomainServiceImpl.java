package com.yiquwuyou.subject.domain.service.impl;


import com.alibaba.fastjson.JSON;
import com.yiquwuyou.subject.common.enums.IsDeletedFlagEnum;
import com.yiquwuyou.subject.domain.convert.SubjectCategoryConverter;
import com.yiquwuyou.subject.domain.entity.SubjectCategoryBO;
import com.yiquwuyou.subject.domain.entity.SubjectLabelBO;
import com.yiquwuyou.subject.domain.service.SubjectCategoryDomainService;
import com.yiquwuyou.subject.domain.util.CacheUtil;
import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import com.yiquwuyou.subject.infra.basic.entity.SubjectLabel;
import com.yiquwuyou.subject.infra.basic.entity.SubjectMapping;
import com.yiquwuyou.subject.infra.basic.service.SubjectCategoryService;
import com.yiquwuyou.subject.infra.basic.service.SubjectLabelService;
import com.yiquwuyou.subject.infra.basic.service.SubjectMappingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubjectCategoryDomainServiceImpl implements SubjectCategoryDomainService {

    @Resource
    private SubjectCategoryService subjectCategoryService;

    @Resource
    private SubjectMappingService subjectMappingService;


    @Resource
    private SubjectLabelService subjectLabelService;

    @Resource
    private ThreadPoolExecutor labelThreadPool;

    @Resource
    private CacheUtil cacheUtil;

    // 插入
    public void add(SubjectCategoryBO subjectCategoryBO) {
        if(log.isInfoEnabled()) {
            log.info("SubjectCategoryController.add.bo: {}", JSON.toJSONString(subjectCategoryBO));
        }
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        subjectCategoryService.insert(subjectCategory);
    }

    // 查询岗位大类
    @Override
    public List<SubjectCategoryBO> queryCategory(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
        List<SubjectCategoryBO> boList = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryList);
        if (log.isInfoEnabled()) {
            log.info("SubjectCategoryController.queryPrimaryCategory.boList:{}",
                    JSON.toJSONString(boList));
        }
        boList.forEach(bo -> {
            Integer subjectCount = subjectCategoryService.querySubjectCount(bo.getId());
            bo.setCount(subjectCount);
        });
        return boList;
    }

    @Override
    public Boolean update(SubjectCategoryBO subjectCategoryBO) {
        // 转换
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        int count = subjectCategoryService.update(subjectCategory);
        return count > 0;
    }

    public Boolean delete(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        int count = subjectCategoryService.update(subjectCategory);
        return count > 0;
    }

    /**
     * 查询分类及标签
     * 先在本地缓存里找，找不到再去数据库查
     * cacheUtil是一个工具类，用于缓存数据
     * @param subjectCategoryBO
     * @return
     */
    @SneakyThrows
    @Override
    public List<SubjectCategoryBO> queryCategoryAndLabel(SubjectCategoryBO subjectCategoryBO) {
        Long id = subjectCategoryBO.getId();
        String cacheKey = "categoryAndLabel." + subjectCategoryBO.getId();
        List<SubjectCategoryBO> subjectCategoryBOS = cacheUtil.getResult(cacheKey,
                SubjectCategoryBO.class, (key) -> getSubjectCategoryBOS(id));
        return subjectCategoryBOS;
    }

    // 代码思路：大类->分类->标签
    // 1. 首先根据传入的大类ID查询当前大类下所有的分类（产过来的参数虽然是SubjectCategoryBO实体类，但实际上只有大类ID） -> 得到分类列表
    // 2. 然后使用Stream和CompletableFuture来异步处理每个分类的标签查询 -> 得到分类ID和标签业务对象列表的映射关系
    private List<SubjectCategoryBO> getSubjectCategoryBOS(Long categoryId) {
        // 查询当前大类下所有分类（根据大类ID查询）
        SubjectCategory subjectCategory = new SubjectCategory();
        subjectCategory.setParentId(categoryId); // 设置父ID
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode()); // 设置未删除状态
        // 查询分类列表 -> 得到分类列表
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory); // 执行查询

        if (log.isInfoEnabled()) {
            log.info("SubjectCategoryController.queryCategoryAndLabel.subjectCategoryList:{}",
                    JSON.toJSONString(subjectCategoryList)); // 记录查询到的分类列表
        }

        // 将分类列表转换为业务对象列表,SubjectCategoryBO中字段值是不全的，没有标签列表，所以需要再次查询
        List<SubjectCategoryBO> categoryBOList = SubjectCategoryConverter.INSTANCE.convertBoToCategory(subjectCategoryList); // 转换为业务对象列表

        // 使用HashMap来存储每个分类ID对应的标签列表
        Map<Long, List<SubjectLabelBO>> map = new HashMap<>();

        // 使用Stream和CompletableFuture来异步处理每个分类的标签查询
        // 根据所有分类列表的id，异步查询每个分类的标签列表 -> 这个时候得到的标签列表就是完整的
        List<CompletableFuture<Map<Long, List<SubjectLabelBO>>>> completableFutureList = categoryBOList.stream().map(category ->
                CompletableFuture.supplyAsync(() -> getLabelBOList(category), labelThreadPool) // 异步执行getLabelBOList方法
        ).collect(Collectors.toList());

        // 等待所有异步任务完成，并将结果合并到map中
        completableFutureList.forEach(future -> {
            try {
                Map<Long, List<SubjectLabelBO>> resultMap = future.get(); // 获取异步任务的结果
                if (!MapUtils.isEmpty(resultMap)) {
                    // 将结果合并到map中
                    map.putAll(resultMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 为每个分类设置其标签列表
        categoryBOList.forEach(categoryBO -> {
            // 所有的标签列表都在map中，是以 分类ID -> 标签列表 的键值对形式存储的
            // 现在就是根据分类ID来获取对应的标签列表，并赋值给分类对象
            if (!CollectionUtils.isEmpty(map.get(categoryBO.getId()))) {
                categoryBO.setLabelBOList(map.get(categoryBO.getId()));
            }// 使用getOrDefault确保即使找不到标签列表也返回一个空列表
        });

        return categoryBOList; // 返回处理后的业务对象列表
    }

    // 定义一个私有方法，用于根据分类获取对应的标签业务对象列表，并返回一个映射关系
    private Map<Long, List<SubjectLabelBO>> getLabelBOList(SubjectCategoryBO category) {
        if (log.isInfoEnabled()) {
            log.info("getLabelBOList:{}", JSON.toJSONString(category)); // 将对象转换为JSON字符串进行日志记录
        }

        // 创建一个用于存储映射关系的Map，键为分类ID，值为对应的标签业务对象列表
        Map<Long, List<SubjectLabelBO>> labelMap = new HashMap<>();

        // 创建一个科目映射对象，并设置其分类ID为传入的分类对象的ID
        SubjectMapping subjectMapping = new SubjectMapping();
        subjectMapping.setCategoryId(category.getId());

        // 调用服务层方法，根据科目映射对象查询对应的标签ID列表
        List<SubjectMapping> mappingList = subjectMappingService.queryLabelId(subjectMapping);

        // 如果查询结果为空，则直接返回null
        if (CollectionUtils.isEmpty(mappingList)) {
            return null;
        }

        // 使用Stream API从mappingList中提取出所有的标签ID，并收集到一个新的列表中
        List<Long> labelIdList = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());

        // 调用服务层方法，根据标签ID列表批量查询标签信息
        List<SubjectLabel> labelList = subjectLabelService.batchQueryById(labelIdList);

        // 创建一个链表，用于存储转换后的标签业务对象
        List<SubjectLabelBO> labelBOList = new LinkedList<>();

        // 遍历标签列表，将每个标签信息转换为标签业务对象，并添加到链表中
        labelList.forEach(label -> {
            SubjectLabelBO subjectLabelBO = new SubjectLabelBO(); // 创建标签业务对象
            subjectLabelBO.setId(label.getId()); // 设置ID
            subjectLabelBO.setLabelName(label.getLabelName()); // 设置标签名称
            subjectLabelBO.setCategoryId(label.getCategoryId()); // 设置分类ID
            subjectLabelBO.setSortNum(label.getSortNum()); // 设置排序号
            labelBOList.add(subjectLabelBO); // 将转换后的业务对象添加到列表中
        });

        // 将分类ID和对应的标签业务对象列表添加到映射关系中
        labelMap.put(category.getId(), labelBOList);

        // 返回映射关系
        return labelMap;
    }


}
