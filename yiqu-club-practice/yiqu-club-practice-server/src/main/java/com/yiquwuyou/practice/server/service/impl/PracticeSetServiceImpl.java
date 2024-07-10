package com.yiquwuyou.practice.server.service.impl;

import com.yiquwuyou.practice.api.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.practice.api.vo.SpecialPracticeCategoryVO;
import com.yiquwuyou.practice.api.vo.SpecialPracticeLabelVO;
import com.yiquwuyou.practice.api.vo.SpecialPracticeVO;
import com.yiquwuyou.practice.server.dao.SubjectCategoryDao;
import com.yiquwuyou.practice.server.dao.SubjectLabelDao;
import com.yiquwuyou.practice.server.dao.SubjectMappingDao;
import com.yiquwuyou.practice.server.entity.dto.CategoryDTO;
import com.yiquwuyou.practice.server.entity.po.CategoryPO;
import com.yiquwuyou.practice.server.entity.po.LabelCountPO;
import com.yiquwuyou.practice.server.entity.po.PrimaryCategoryPO;
import com.yiquwuyou.practice.server.entity.po.SubjectLabelPO;
import com.yiquwuyou.practice.server.service.PracticeSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 这里假设是单体服务，体验下单体服务的比较冗余的开发
 */
@Service
@Slf4j
public class PracticeSetServiceImpl implements PracticeSetService {

    // 注入SubjectCategoryDao
    @Resource
    private SubjectCategoryDao subjectCategoryDao;

    // 注入SubjectMappingDao
    @Resource
    private SubjectMappingDao subjectMappingDao;

    // 注入SubjectLabelDao
    @Resource
    private SubjectLabelDao subjectLabelDao;

    /**
     * 获取专项练习内容
     * @return 专项练习内容列表
     */
    @Override
    public List<SpecialPracticeVO> getSpecialPracticeContent() {
        // 初始化专项练习内容列表
        List<SpecialPracticeVO> specialPracticeVOList = new LinkedList<>();
        // 初始化题目类型列表
        List<Integer> subjectTypeList = new LinkedList<>();
        // 添加题目类型到列表
        subjectTypeList.add(SubjectInfoTypeEnum.RADIO.getCode());
        subjectTypeList.add(SubjectInfoTypeEnum.MULTIPLE.getCode());
        subjectTypeList.add(SubjectInfoTypeEnum.JUDGE.getCode());
        // 初始化分类数据传输对象
        CategoryDTO categoryDTO = new CategoryDTO();
        // 设置题目类型列表
        categoryDTO.setSubjectTypeList(subjectTypeList);
        // 获取主分类列表->大类
        List<PrimaryCategoryPO> poList = subjectCategoryDao.getPrimaryCategory(categoryDTO);
        // 如果主分类列表为空，则返回空的专项练习内容列表
        if (CollectionUtils.isEmpty(poList)) {
            return specialPracticeVOList;
        }
        // 遍历主分类列表
        poList.forEach(primaryCategoryPO -> {
            // 初始化专项练习视图对象
            SpecialPracticeVO specialPracticeVO = new SpecialPracticeVO();
            // 设置主分类ID
            specialPracticeVO.setPrimaryCategoryId(primaryCategoryPO.getParentId());
            // 根据分类id获取分类信息
            CategoryPO categoryPO = subjectCategoryDao.selectById(primaryCategoryPO.getParentId());
            // 设置主分类名称
            specialPracticeVO.setPrimaryCategoryName(categoryPO.getCategoryName());
            // 初始化分类数据传输对象
            CategoryDTO categoryDTOTemp = new CategoryDTO();
            // 设置分类类型->2是小类
            categoryDTOTemp.setCategoryType(2);
            // 设置父分类ID
            categoryDTOTemp.setParentId(primaryCategoryPO.getParentId());
            // 获取小分类列表
            List<CategoryPO> smallPoList = subjectCategoryDao.selectList(categoryDTOTemp);
            // 如果小分类列表为空，则返回
            if (CollectionUtils.isEmpty(smallPoList)) {
                return;
            }
            // 初始化专项练习分类视图对象列表
            List<SpecialPracticeCategoryVO> categoryList = new LinkedList();
            // 遍历小分类列表
            smallPoList.forEach(smallPo -> {
                // 获取标签视图对象列表
                List<SpecialPracticeLabelVO> labelVOList = getLabelVOList(smallPo.getId(), subjectTypeList);
                // 如果标签视图对象列表为空，则返回
                if (CollectionUtils.isEmpty(labelVOList)) {
                    return;
                }
                // 初始化专项练习分类视图对象
                SpecialPracticeCategoryVO specialPracticeCategoryVO = new SpecialPracticeCategoryVO();
                // 设置分类ID
                specialPracticeCategoryVO.setCategoryId(smallPo.getId());
                // 设置分类名称
                specialPracticeCategoryVO.setCategoryName(smallPo.getCategoryName());
                // 初始化标签列表
                List<SpecialPracticeLabelVO> labelList = new LinkedList<>();
                // 遍历标签视图对象列表
                labelVOList.forEach(labelVo -> {
                    // 初始化专项练习标签视图对象
                    SpecialPracticeLabelVO specialPracticeLabelVO = new SpecialPracticeLabelVO();
                    // 设置标签ID
                    specialPracticeLabelVO.setId(labelVo.getId());
                    // 设置组合ID
                    specialPracticeLabelVO.setAssembleId(labelVo.getAssembleId());
                    // 设置标签名称
                    specialPracticeLabelVO.setLabelName(labelVo.getLabelName());
                    // 将专项练习标签视图对象添加到标签列表
                    labelList.add(specialPracticeLabelVO);
                });
                // 设置标签列表
                specialPracticeCategoryVO.setLabelList(labelList);
                // 将专项练习分类视图对象添加到专项练习分类视图对象列表
                categoryList.add(specialPracticeCategoryVO);
            });
            // 设置专项练习分类视图对象列表
            specialPracticeVO.setCategoryList(categoryList);
            // 将专项练习视图对象添加到专项练习内容列表
            specialPracticeVOList.add(specialPracticeVO);
        });
        // 返回专项练习内容列表
        return specialPracticeVOList;
    }

    /**
     * 获取标签视图对象列表
     * @param categoryId 分类ID
     * @param subjectTypeList 题目类型列表
     * @return 标签视图对象列表
     */
    private List<SpecialPracticeLabelVO> getLabelVOList(Long categoryId, List<Integer> subjectTypeList) {
        // 获取该小分类下的所有符合条件的标签信息
        List<LabelCountPO> countPOList = subjectMappingDao.getLabelSubjectCount(categoryId, subjectTypeList);
        // 如果标签计数持久化对象列表为空，则返回空的标签视图对象列表
        if(CollectionUtils.isEmpty(countPOList)){
            return Collections.emptyList();
        }
        // 初始化标签视图对象列表
        List<SpecialPracticeLabelVO> voList = new LinkedList<>();
        // 遍历标签计数持久化对象列表
        countPOList.forEach(countPo->{
            // 初始化专项练习标签视图对象
            SpecialPracticeLabelVO vo = new SpecialPracticeLabelVO();
            // 设置标签ID
            vo.setId(countPo.getLabelId());
            // 设置组合ID
            vo.setAssembleId(categoryId + "-" + countPo.getLabelId());
            // 获取标签信息（此处主要需要标签名称）
            SubjectLabelPO subjectLabelPO = subjectLabelDao.queryById(countPo.getLabelId());
            // 设置标签名称
            vo.setLabelName(subjectLabelPO.getLabelName());
            // 将专项练习标签视图对象添加到标签视图对象列表
            voList.add(vo);
        });
        // 返回标签视图对象列表
        return voList;
    }

}
