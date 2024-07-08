package com.yiquwuyou.subject.application.controller;

import com.yiquwuyou.subject.infra.basic.service.SubjectEsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 刷题分类controller
 *
 * @author: ChickenWing
 * @date: 2023/10/1
 */
@RestController
@RequestMapping("/subject/category")
@Slf4j
public class TestFeignController {



    @Resource
    private SubjectEsService subjectEsService;




}