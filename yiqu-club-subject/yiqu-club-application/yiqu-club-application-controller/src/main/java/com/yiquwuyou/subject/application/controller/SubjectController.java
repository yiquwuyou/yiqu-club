package com.yiquwuyou.subject.application.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.yiquwuyou.subject.application.convert.SubjectAnswerDTOConverter;
import com.yiquwuyou.subject.application.convert.SubjectInfoDTOConverter;
import com.yiquwuyou.subject.application.dto.SubjectInfoDTO;
import com.yiquwuyou.subject.common.entity.PageResult;
import com.yiquwuyou.subject.common.entity.Result;
import com.yiquwuyou.subject.domain.entity.SubjectAnswerBO;
import com.yiquwuyou.subject.domain.entity.SubjectInfoBO;
import com.yiquwuyou.subject.domain.service.SubjectInfoDomainService;
import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import com.yiquwuyou.subject.infra.basic.entity.SubjectInfoEs;
import com.yiquwuyou.subject.infra.basic.service.SubjectCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 刷题controller
 *
 * @author: yiquwuyou
 * @date: 2023/10/1
 */
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectController {

    @Resource
    private SubjectInfoDomainService subjectInfoDomainService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 新增题目
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody SubjectInfoDTO subjectInfoDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectController.add.dto:{}", JSON.toJSONString(subjectInfoDTO));
            }
            Preconditions.checkArgument(!StringUtils.isBlank(subjectInfoDTO.getSubjectName()),
                    "题目名称不能为空");
            Preconditions.checkNotNull(subjectInfoDTO.getSubjectDifficult(), "题目难度不能为空");
            Preconditions.checkNotNull(subjectInfoDTO.getSubjectType(), "题目类型不能为空");
            Preconditions.checkNotNull(subjectInfoDTO.getSubjectScore(), "题目分数不能为空");
            Preconditions.checkArgument(!CollectionUtils.isEmpty(subjectInfoDTO.getCategoryIds())
                    , "分类id不能为空");
            Preconditions.checkArgument(!CollectionUtils.isEmpty(subjectInfoDTO.getLabelIds())
                    , "标签id不能为空");

            SubjectInfoBO subjectInfoBO = SubjectInfoDTOConverter.INSTANCE.convertDTOToBO(subjectInfoDTO);
            List<SubjectAnswerBO> subjectAnswerBOS =
                    SubjectAnswerDTOConverter.INSTANCE.convertListDTOToBO(subjectInfoDTO.getOptionList());
            subjectInfoBO.setOptionList(subjectAnswerBOS);
            subjectInfoDomainService.add(subjectInfoBO);
            return Result.ok(true);
        } catch (Exception e) {
            log.error("SubjectCategoryController.add.error:{}", e.getMessage(), e);
            return Result.fail("新增题目失败");
        }
    }
    /**
     * 查询题目列表
     */
    @PostMapping("/getSubjectPage")
    public Result<PageResult<SubjectInfoDTO>> getSubjectPage(@RequestBody SubjectInfoDTO subjectInfoDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectController.getSubjectPage.dto:{}", JSON.toJSONString(subjectInfoDTO));
            }
            Preconditions.checkNotNull(subjectInfoDTO.getCategoryId(), "分类id不能为空");
            Preconditions.checkNotNull(subjectInfoDTO.getLabelId(), "标签id不能为空");
            SubjectInfoBO subjectInfoBO = SubjectInfoDTOConverter.INSTANCE.convertDTOToBO(subjectInfoDTO);
            PageResult<SubjectInfoBO> boPageResult = subjectInfoDomainService.getSubjectPage(subjectInfoBO);
            return Result.ok(boPageResult);
        } catch (Exception e) {
            log.error("SubjectCategoryController.add.error:{}", e.getMessage(), e);
            return Result.fail("新增题目失败");
        }
    }

    /**
     * 查询题目信息
     */
    @PostMapping("/querySubjectInfo")
    public Result<SubjectInfoDTO> querySubjectInfo(@RequestBody SubjectInfoDTO subjectInfoDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectController.querySubjectInfo.dto:{}", JSON.toJSONString(subjectInfoDTO));
            }
            Preconditions.checkNotNull(subjectInfoDTO.getId(), "题目id不能为空");
            SubjectInfoBO subjectInfoBO = SubjectInfoDTOConverter.INSTANCE.convertDTOToBO(subjectInfoDTO);
            SubjectInfoBO boResult = subjectInfoDomainService.querySubjectInfo(subjectInfoBO);
            SubjectInfoDTO dto = SubjectInfoDTOConverter.INSTANCE.convertBOToDTO(boResult);
            return Result.ok(dto);
        } catch (Exception e) {
            log.error("SubjectCategoryController.add.error:{}", e.getMessage(), e);
            return Result.fail("查询题目详情失败");
        }
    }

    /**
     * 全文检索
     */
    @PostMapping("/getSubjectPageBySearch")
    public Result<PageResult<SubjectInfoEs>> getSubjectPageBySearch(@RequestBody SubjectInfoDTO subjectInfoDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectController.getSubjectPageBySearch.dto:{}", JSON.toJSONString(subjectInfoDTO));
            }
            Preconditions.checkArgument(StringUtils.isNotBlank(subjectInfoDTO.getKeyWord()), "关键词不能为空");
            SubjectInfoBO subjectInfoBO = SubjectInfoDTOConverter.INSTANCE.convertDTOToBO(subjectInfoDTO);
            subjectInfoBO.setPageNo(subjectInfoDTO.getPageNo());
            subjectInfoBO.setPageSize(subjectInfoDTO.getPageSize());
            PageResult<SubjectInfoEs> boPageResult = subjectInfoDomainService.getSubjectPageBySearch(subjectInfoBO);
            return Result.ok(boPageResult);
        } catch (Exception e) {
            log.error("SubjectCategoryController.getSubjectPageBySearch.error:{}", e.getMessage(), e);
            return Result.fail("全文检索失败");
        }
    }

    /**
     * 获取题目贡献榜
     */
    @PostMapping("/getContributeList")
    public Result<List<SubjectInfoDTO>> getContributeList() {
        try {
            List<SubjectInfoBO> boList = subjectInfoDomainService.getContributeList();
            List<SubjectInfoDTO> dtoList = SubjectInfoDTOConverter.INSTANCE.convertBOToDTOList(boList);
            return Result.ok(dtoList);
        } catch (Exception e) {
            log.error("SubjectCategoryController.getContributeList.error:{}", e.getMessage(), e);
            return Result.fail("获取贡献榜失败");
        }
    }

    @RequestMapping("/pushMessage")
    public Result<Boolean> get(@RequestParam("id") int id) {
        rocketMQTemplate.convertAndSend("first-topic","你好,Java旅途" + id);
        return Result.ok(true);
    }

}

