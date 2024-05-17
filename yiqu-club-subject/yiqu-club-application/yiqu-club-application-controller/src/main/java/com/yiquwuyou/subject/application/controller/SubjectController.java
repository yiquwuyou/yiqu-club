package com.yiquwuyou.subject.application.controller;

import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import com.yiquwuyou.subject.infra.basic.service.SubjectCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 刷题controller
 */
@RestController
public class SubjectController {

    @Resource
    private SubjectCategoryService subjectCategoryService;
    @GetMapping("/ceshi")
    public String ceshi() {
        SubjectCategory subjectCategory = subjectCategoryService.queryById(1L);
        return subjectCategory.getCategoryName();
    }
}
