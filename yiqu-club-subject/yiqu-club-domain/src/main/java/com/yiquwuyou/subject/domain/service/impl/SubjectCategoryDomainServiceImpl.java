package com.yiquwuyou.subject.domain.service.impl;


import com.alibaba.fastjson.JSON;
import com.yiquwuyou.subject.common.enums.IsDeletedFlagEnum;
import com.yiquwuyou.subject.domain.convert.SubjectCategoryConverter;
import com.yiquwuyou.subject.domain.entity.SubjectCategoryBO;
import com.yiquwuyou.subject.domain.service.SubjectCategoryDomainService;
import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import com.yiquwuyou.subject.infra.basic.service.SubjectCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SubjectCategoryDomainServiceImpl implements SubjectCategoryDomainService {

    @Resource
    private SubjectCategoryService subjectCategoryService;

    // 插入
    public void add(SubjectCategoryBO subjectCategoryBO) {
        if(log.isInfoEnabled()) {
            log.info("SubjectCategoryController.add.bo: {}", JSON.toJSONString(subjectCategoryBO));
        }
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategoryService.insert(subjectCategory);
    }

    // 查询岗位大类
    @Override
    public List<SubjectCategoryBO> queryCategory(SubjectCategoryBO subjectCategoryBO) {
        // 转换
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        // 查询
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
        List<SubjectCategoryBO> boList =SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryList);
        if(log.isInfoEnabled()) {
            log.info("SubjectCategoryController.queryPrimaryCategory.boList: {}", JSON.toJSONString(boList));
        }
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
}
