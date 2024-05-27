package com.yiquwuyou.subject.application.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yiquwuyou.subject.application.convert.SubjectCategoryDTOConverter;
import com.yiquwuyou.subject.application.convert.SubjectLabelDTOConverter;
import com.yiquwuyou.subject.application.dto.SubjectCategoryDTO;
import com.yiquwuyou.subject.application.dto.SubjectLabelDTO;
import com.yiquwuyou.subject.common.entity.Result;
import com.yiquwuyou.subject.domain.entity.SubjectCategoryBO;
import com.yiquwuyou.subject.domain.service.SubjectCategoryDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 刷题分类Controller
 */
@RestController
@RequestMapping("/subject/category")
@Slf4j
public class SubjectCategoryController {

    @Resource
    private SubjectCategoryDomainService subjectCategoryDomainService;

    /**
     * 新增分类
     * @param subjectCategoryDTO
     * @return
     */
    @PostMapping ("/add")
    public Result<Boolean> add(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            if(log.isInfoEnabled()) {
                log.info("SubjectCategoryController.add.dto: {}", JSON.toJSONString(subjectCategoryDTO));
            }
            // 参数校验
            Preconditions.checkNotNull(subjectCategoryDTO.getCategoryType(), "分类类型不能为空");
            log.info("subjectCategoryDTO.getCategoryName(), {}, {}", subjectCategoryDTO.getCategoryName(),StringUtils.isEmpty(subjectCategoryDTO.getCategoryName()));
            Preconditions.checkArgument(!StringUtils.isBlank(subjectCategoryDTO.getCategoryName()), "分类名称不能为空");
            Preconditions.checkNotNull(subjectCategoryDTO.getParentId(), "分类父级id不能为空");
            // 插入
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.convertBoToCategory(subjectCategoryDTO);
            subjectCategoryDomainService.add (subjectCategoryBO);
            return Result.ok(true);
        } catch (Exception e) {
            log.error("SubjectCategoryController.add.error: {}", e.getMessage(), e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询岗位大类
     */
    @PostMapping("/queryPrimaryCategory")
    public Result<List<SubjectCategoryDTO>> queryPrimaryCategory(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.
                    convertDtoToCategoryBO(subjectCategoryDTO);
            List<SubjectCategoryBO> subjectCategoryBOList = subjectCategoryDomainService.queryCategory(subjectCategoryBO);
            List<SubjectCategoryDTO> subjectCategoryDTOList = SubjectCategoryDTOConverter.INSTANCE.
                    convertBoToCategoryDTOList(subjectCategoryBOList);
            return Result.ok(subjectCategoryDTOList);
        } catch (Exception e) {
            log.error("SubjectCategoryController.queryPrimaryCategory.error:{}", e.getMessage(), e);
            return Result.fail("查询失败");
        }

    }
    /**
     * 查询大类下分类
     * @param subjectCategoryDTO
     * @return
     */
    @PostMapping ("/queryCategoryByPrimary")
    public Result<List<SubjectCategoryDTO>> queryCategoryByPrimary(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            if(log.isInfoEnabled()) {
                log.info("SubjectCategoryController.queryCategoryByPrimary.dto: {}", JSON.toJSONString(subjectCategoryDTO));
            }
            Preconditions.checkNotNull(subjectCategoryDTO.getParentId(), "分类id不能为空");
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.convertBoToCategory(subjectCategoryDTO);
            List<SubjectCategoryBO> subjectCategoryBOList = subjectCategoryDomainService.queryCategory(subjectCategoryBO);
            List<SubjectCategoryDTO> subjectCategoryDTOList = SubjectCategoryDTOConverter.INSTANCE.convertBoToCategoryDTOList(subjectCategoryBOList);
            return Result.ok(subjectCategoryDTOList);
        } catch (Exception e) {
            log.error("SubjectCategoryController.queryPrimaryCategory.error: {}", e.getMessage(), e);
            return Result.fail("查询失败");
        }
    }

    /**
     * 更新分类
     */
    @PostMapping ("/update")
    public Result<Boolean> update(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            if(log.isInfoEnabled()) {
                log.info("SubjectCategoryController.update.dto: {}", JSON.toJSONString(subjectCategoryDTO));
            }
            Preconditions.checkNotNull(subjectCategoryDTO.getId(), "id不能为空");
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.convertBoToCategory(subjectCategoryDTO);
            Boolean result = subjectCategoryDomainService.update(subjectCategoryBO);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("SubjectCategoryController.update.error: {}", e.getMessage(), e);
            return Result.fail("更新分类失败");
        }
    }

    /**
     * 删除分类
     */
    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectCategoryController.delete.dto:{}", JSON.toJSONString(subjectCategoryDTO));
            }
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.
                    convertBoToCategory(subjectCategoryDTO);
            Boolean result = subjectCategoryDomainService.delete(subjectCategoryBO);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("SubjectCategoryController.delete.error:{}", e.getMessage(), e);
            return Result.fail("删除分类失败");
        }

    }

    /**
     * 查询分类及标签一次性
     */
    @PostMapping("/queryCategoryAndLabel")
    public Result<List<SubjectCategoryDTO>> queryCategoryAndLabel(@RequestBody SubjectCategoryDTO subjectCategoryDTO) {
        try {
            if (log.isInfoEnabled()) {
                log.info("SubjectCategoryController.queryCategoryAndLabel.dto:{}"
                        , JSON.toJSONString(subjectCategoryDTO));
            }
            Preconditions.checkNotNull(subjectCategoryDTO.getId(), "分类id不能为空");
            SubjectCategoryBO subjectCategoryBO = SubjectCategoryDTOConverter.INSTANCE.
                    convertDtoToCategoryBO(subjectCategoryDTO);
            List<SubjectCategoryBO> subjectCategoryBOList = subjectCategoryDomainService.queryCategoryAndLabel(subjectCategoryBO);
            List<SubjectCategoryDTO> dtoList = new LinkedList<>();
            subjectCategoryBOList.forEach(bo -> {
                SubjectCategoryDTO dto = SubjectCategoryDTOConverter.INSTANCE.convertBoToCategoryDTO(bo);
                List<SubjectLabelDTO> labelDTOList = SubjectLabelDTOConverter.INSTANCE.convertBOToLabelDTOList(bo.getLabelBOList());
                dto.setLabelDTOList(labelDTOList);
                dtoList.add(dto);
            });
            return Result.ok(dtoList);
        } catch (Exception e) {
            log.error("SubjectCategoryController.queryPrimaryCategory.error:{}", e.getMessage(), e);
            return Result.fail("查询失败");
        }
    }
}
