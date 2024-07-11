package com.yiquwuyou.practice.server.service.impl;

import com.yiquwuyou.practice.api.enums.IsDeletedFlagEnum;
import com.yiquwuyou.practice.api.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.practice.api.req.GetPracticeSubjectsReq;
import com.yiquwuyou.practice.api.vo.*;
import com.yiquwuyou.practice.server.dao.*;
import com.yiquwuyou.practice.server.entity.dto.CategoryDTO;
import com.yiquwuyou.practice.server.entity.dto.PracticeSubjectDTO;
import com.yiquwuyou.practice.server.entity.po.*;
import com.yiquwuyou.practice.server.service.PracticeSetService;
import com.yiquwuyou.practice.server.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private PracticeSetDetailDao practiceSetDetailDao;

    @Resource
    private PracticeSetDao practiceSetDao;

    @Resource
    private SubjectDao subjectDao;

    @Resource
    private SubjectRadioDao subjectRadioDao;

    @Resource
    private SubjectMultipleDao subjectMultipleDao;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PracticeSetVO addPractice(PracticeSubjectDTO dto) {
        PracticeSetVO setVO = new PracticeSetVO();
        List<PracticeSubjectDetailVO> practiceList = getPracticeList(dto);
        if (CollectionUtils.isEmpty(practiceList)) {
            return setVO;
        }
        PracticeSetPO practiceSetPO = new PracticeSetPO();
        practiceSetPO.setSetType(1);
        List<String> assembleIds = dto.getAssembleIds();
        Set<Long> categoryIdSet = new HashSet<>();
        assembleIds.forEach(assembleId -> {
            Long categoryId = Long.valueOf(assembleId.split("-")[0]);
            categoryIdSet.add(categoryId);
        });
        StringBuffer setName = new StringBuffer();
        int i = 1;
        for (Long categoryId : categoryIdSet) {
            if (i > 2) {
                break;
            }
            CategoryPO categoryPO = subjectCategoryDao.selectById(categoryId);
            setName.append(categoryPO.getCategoryName());
            setName.append("、");
            i = i + 1;
        }
        setName.deleteCharAt(setName.length() - 1);
        if (i == 2) {
            setName.append("专项练习");
        } else {
            setName.append("等专项练习");
        }
        practiceSetPO.setSetName(setName.toString());
        String labelId = assembleIds.get(0).split("-")[1];
        SubjectLabelPO labelPO = subjectLabelDao.queryById(Long.valueOf(labelId));
        practiceSetPO.setPrimaryCategoryId(labelPO.getCategoryId());
        practiceSetPO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        practiceSetPO.setCreatedBy(LoginUtil.getLoginId());
        practiceSetPO.setCreatedTime(new Date());
        practiceSetDao.add(practiceSetPO);
        Long practiceSetId = practiceSetPO.getId();

        //思考，这里哪里不符合规范，配合听视频的延伸
        practiceList.forEach(e -> {
            PracticeSetDetailPO detailPO = new PracticeSetDetailPO();
            detailPO.setSetId(practiceSetId);
            detailPO.setSubjectId(e.getSubjectId());
            detailPO.setSubjectType(e.getSubjectType());
            detailPO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
            detailPO.setCreatedBy(LoginUtil.getLoginId());
            detailPO.setCreatedTime(new Date());
            practiceSetDetailDao.add(detailPO);
        });
        setVO.setSetId(practiceSetId);
        return setVO;
    }

    @Override
    public PracticeSubjectListVO getSubjects(GetPracticeSubjectsReq req) {
        Long setId = req.getSetId();
        PracticeSubjectListVO vo = new PracticeSubjectListVO();
        List<PracticeSubjectDetailVO> practiceSubjectListVOS = new LinkedList<>();
        List<PracticeSetDetailPO> practiceSetDetailPOS = practiceSetDetailDao.selectBySetId(setId);
        if (CollectionUtils.isEmpty(practiceSetDetailPOS)) {
            return vo;
        }
        practiceSetDetailPOS.forEach(e -> {
            PracticeSubjectDetailVO practiceSubjectListVO = new PracticeSubjectDetailVO();
            practiceSubjectListVO.setSubjectId(e.getSubjectId());
            practiceSubjectListVO.setSubjectType(e.getSubjectType());
            practiceSubjectListVOS.add(practiceSubjectListVO);
        });
        vo.setSubjectList(practiceSubjectListVOS);
        PracticeSetPO practiceSetPO = practiceSetDao.selectById(setId);
        vo.setTitle(practiceSetPO.getSetName());
        return vo;
    }

    /**
     * 获取套卷题目信息
     */
    private List<PracticeSubjectDetailVO> getPracticeList(PracticeSubjectDTO dto) {
        List<PracticeSubjectDetailVO> practiceSubjectListVOS = new LinkedList<>();
        //避免重复
        List<Long> excludeSubjectIds = new LinkedList<>();

        //设置题目数量，之后优化到nacos动态配置
        Integer radioSubjectCount = 10;
        Integer multipleSubjectCount = 6;
        Integer judgeSubjectCount = 4;
        Integer totalSubjectCount = 20;
        //查询单选
        dto.setSubjectCount(radioSubjectCount);
        dto.setSubjectType(SubjectInfoTypeEnum.RADIO.getCode());
        assembleList(dto, practiceSubjectListVOS, excludeSubjectIds);
        //查询多选
        dto.setSubjectCount(multipleSubjectCount);
        dto.setSubjectType(SubjectInfoTypeEnum.MULTIPLE.getCode());
        assembleList(dto, practiceSubjectListVOS, excludeSubjectIds);
        //查询判断
        dto.setSubjectCount(judgeSubjectCount);
        dto.setSubjectType(SubjectInfoTypeEnum.JUDGE.getCode());
        assembleList(dto, practiceSubjectListVOS, excludeSubjectIds);
        //补充题目
        if (practiceSubjectListVOS.size() == totalSubjectCount) {
            return practiceSubjectListVOS;
        }
        Integer remainCount = totalSubjectCount - practiceSubjectListVOS.size();
        dto.setSubjectCount(remainCount);
        dto.setSubjectType(1);
        assembleList(dto, practiceSubjectListVOS, excludeSubjectIds);
        return practiceSubjectListVOS;
    }

    private List<PracticeSubjectDetailVO> assembleList(PracticeSubjectDTO dto, List<PracticeSubjectDetailVO> list, List<Long> excludeSubjectIds) {
        dto.setExcludeSubjectIds(excludeSubjectIds);
        List<SubjectPO> subjectPOList = subjectDao.getPracticeSubject(dto);
        if (CollectionUtils.isEmpty(subjectPOList)) {
            return list;
        }
        subjectPOList.forEach(e -> {
            PracticeSubjectDetailVO vo = new PracticeSubjectDetailVO();
            vo.setSubjectId(e.getId());
            vo.setSubjectType(e.getSubjectType());
            excludeSubjectIds.add(e.getId());
            list.add(vo);
        });
        return list;
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

    @Override
    public PracticeSubjectVO getPracticeSubject(PracticeSubjectDTO dto) {
        PracticeSubjectVO practiceSubjectVO = new PracticeSubjectVO();
        SubjectPO subjectPO = subjectDao.selectById(dto.getSubjectId());
        practiceSubjectVO.setSubjectName(subjectPO.getSubjectName());
        practiceSubjectVO.setSubjectType(subjectPO.getSubjectType());
        if (dto.getSubjectType() == SubjectInfoTypeEnum.RADIO.getCode()) {
            List<PracticeSubjectOptionVO> optionList = new LinkedList<>();
            List<SubjectRadioPO> radioSubjectPOS = subjectRadioDao.selectBySubjectId(subjectPO.getId());
            radioSubjectPOS.forEach(e -> {
                PracticeSubjectOptionVO practiceSubjectOptionVO = new PracticeSubjectOptionVO();
                practiceSubjectOptionVO.setOptionContent(e.getOptionContent());
                practiceSubjectOptionVO.setOptionType(e.getOptionType());
                optionList.add(practiceSubjectOptionVO);
            });
            practiceSubjectVO.setOptionList(optionList);
        }
        if (dto.getSubjectType() == SubjectInfoTypeEnum.MULTIPLE.getCode()) {
            List<PracticeSubjectOptionVO> optionList = new LinkedList<>();
            List<SubjectMultiplePO> multipleSubjectPOS = subjectMultipleDao.selectBySubjectId(subjectPO.getId());
            multipleSubjectPOS.forEach(e -> {
                PracticeSubjectOptionVO practiceSubjectOptionVO = new PracticeSubjectOptionVO();
                practiceSubjectOptionVO.setOptionContent(e.getOptionContent());
                practiceSubjectOptionVO.setOptionType(e.getOptionType());
                optionList.add(practiceSubjectOptionVO);
            });
            practiceSubjectVO.setOptionList(optionList);
        }
        return practiceSubjectVO;
    }

}
