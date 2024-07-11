package com.yiquwuyou.practice.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.yiquwuyou.practice.api.enums.CompleteStatusEnum;
import com.yiquwuyou.practice.api.enums.IsDeletedFlagEnum;
import com.yiquwuyou.practice.api.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.practice.api.req.GetScoreDetailReq;
import com.yiquwuyou.practice.api.req.SubmitPracticeDetailReq;
import com.yiquwuyou.practice.api.req.SubmitSubjectDetailReq;
import com.yiquwuyou.practice.api.vo.ScoreDetailVO;
import com.yiquwuyou.practice.server.dao.*;
import com.yiquwuyou.practice.server.entity.dto.SubjectDTO;
import com.yiquwuyou.practice.server.entity.dto.SubjectDetailDTO;
import com.yiquwuyou.practice.server.entity.dto.SubjectOptionDTO;
import com.yiquwuyou.practice.server.entity.po.*;
import com.yiquwuyou.practice.server.service.PracticeDetailService;
import com.yiquwuyou.practice.server.util.DateUtils;
import com.yiquwuyou.practice.server.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PracticeDetailServiceImpl implements PracticeDetailService {

    @Resource
    private PracticeDetailDao practiceDetailDao;

    @Resource
    private PracticeSetDao practiceSetDao;

    @Resource
    private PracticeSetDetailDao practiceSetDetailDao;

    @Resource
    private PracticeDao practiceDao;

    @Resource
    private SubjectDao subjectDao;

    @Resource
    private SubjectRadioDao subjectRadioDao;

    @Resource
    private SubjectMultipleDao subjectMultipleDao;

    @Resource
    private SubjectJudgeDao subjectJudgeDao;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Boolean submit(SubmitPracticeDetailReq req) {
        PracticePO practicePO = new PracticePO();
        Long practiceId = req.getPracticeId();
        Long setId = req.getSetId();
        practicePO.setSetId(setId);
        // 处理前端传来的用时时间 格式为hhmmss 如013215就是1小时32分15秒
        String timeUse = req.getTimeUse();
        String hour = timeUse.substring(0, 2);
        String minute = timeUse.substring(2, 4);
        String second = timeUse.substring(4, 6);
        practicePO.setTimeUse(hour + ":" + minute + ":" + second);
        // 交卷时间进行了格式化，格式为yyyy-MM-dd HH:mm:ss
        practicePO.setSubmitTime(DateUtils.parseStrToDate(req.getSubmitTime()));
        // 设置完成状态为已完成
        practicePO.setCompleteStatus(CompleteStatusEnum.COMPLETE.getCode());
        // 设置删除状态为未删除
        practicePO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        practicePO.setCreatedBy(LoginUtil.getLoginId());
        practicePO.setCreatedTime(new Date());
        //计算正确率
        Integer correctCount = practiceDetailDao.selectCorrectCount(practiceId);
        List<PracticeSetDetailPO> practiceSetDetailPOS = practiceSetDetailDao.selectBySetId(setId);
        Integer totalCount = practiceSetDetailPOS.size();
        BigDecimal correctRate = new BigDecimal(correctCount).divide(new BigDecimal(totalCount), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100.00"));
        practicePO.setCorrectRate(correctRate);
        // 查询数据库中是否有该练习记录，如果没有则插入，如果有则更新
        PracticePO po = practiceDao.selectById(practiceId);
        if (Objects.isNull(po)) {
            practiceDao.insert(practicePO);
        } else {
            practicePO.setId(practiceId);
            practiceDao.update(practicePO);
        }
        // 更新套题热度+1
        practiceSetDao.updateHeat(setId);
        //补充剩余题目的记录
        List<PracticeDetailPO> practiceDetailPOList = practiceDetailDao.selectByPracticeId(practiceId);
        List<PracticeSetDetailPO> minusList = practiceSetDetailPOS.stream()
                .filter(item -> !practiceDetailPOList.stream()
                        .map(e -> e.getSubjectId())
                        .collect(Collectors.toList())
                        .contains(item.getSubjectId()))
                .collect(Collectors.toList());
        if (log.isInfoEnabled()) {
            log.info("题目差集{}", JSON.toJSONString(minusList));
        }
        if (CollectionUtils.isNotEmpty(minusList)) {
            minusList.forEach(e -> {
                PracticeDetailPO practiceDetailPO = new PracticeDetailPO();
                practiceDetailPO.setPracticeId(practiceId);
                practiceDetailPO.setSubjectType(e.getSubjectType());
                practiceDetailPO.setSubjectId(e.getSubjectId());
                practiceDetailPO.setAnswerStatus(0);
                practiceDetailPO.setAnswerContent("");
                practiceDetailPO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
                practiceDetailPO.setCreatedTime(new Date());
                practiceDetailPO.setCreatedBy(LoginUtil.getLoginId());
                practiceDetailDao.insertSingle(practiceDetailPO);
            });
        }
        return true;
    }

    /**
     * 提交题目
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitSubject(SubmitSubjectDetailReq req) {
        String timeUse = req.getTimeUse();
        if (timeUse.equals("0")) {
            timeUse = "000000";
        }
        String hour = timeUse.substring(0, 2);
        String minute = timeUse.substring(2, 4);
        String second = timeUse.substring(4, 6);
        PracticePO practicePO = new PracticePO();
        practicePO.setId(req.getPracticeId());
        practicePO.setTimeUse(hour + ":" + minute + ":" + second);
        practicePO.setSubmitTime(new Date());
        practiceDao.update(practicePO);

        PracticeDetailPO practiceDetailPO = new PracticeDetailPO();
        practiceDetailPO.setPracticeId(req.getPracticeId());
        practiceDetailPO.setSubjectId(req.getSubjectId());
        practiceDetailPO.setSubjectType(req.getSubjectType());
        String answerContent = "";
        //排序答案
        if (CollectionUtils.isNotEmpty(req.getAnswerContents())) {
            List<Integer> answerContents = req.getAnswerContents();
            Collections.sort(answerContents);
            answerContent = StringUtils.join(answerContents, ",");
        }
        practiceDetailPO.setAnswerContent(answerContent);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setSubjectId(req.getSubjectId());
        subjectDTO.setSubjectType(req.getSubjectType());
        //获取正确答案，并判断答案是否正确
        SubjectDetailDTO subjectDetail = getSubjectDetail(subjectDTO);
        StringBuffer correctAnswer = new StringBuffer();
        if (req.getSubjectType().equals(SubjectInfoTypeEnum.JUDGE.getCode())) {
            Integer isCorrect = subjectDetail.getIsCorrect();
            correctAnswer.append(isCorrect);
        } else {
            subjectDetail.getOptionList().forEach(e -> {
                if (Objects.equals(e.getIsCorrect(), 1)) {
                    correctAnswer.append(e.getOptionType()).append(",");
                }
            });
            if (correctAnswer.length() > 0) {
                correctAnswer.deleteCharAt(correctAnswer.length() - 1);
            }
        }
        if (Objects.equals(correctAnswer.toString(), answerContent)) {
            practiceDetailPO.setAnswerStatus(1);
        } else {
            practiceDetailPO.setAnswerStatus(0);
        }
        practiceDetailPO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        practiceDetailPO.setCreatedBy(LoginUtil.getLoginId());
        practiceDetailPO.setCreatedTime(new Date());
        PracticeDetailPO existDetail = practiceDetailDao.selectDetail(req.getPracticeId(), req.getSubjectId(), LoginUtil.getLoginId());
        if (Objects.isNull(existDetail)) {
            practiceDetailDao.insertSingle(practiceDetailPO);
        } else {
            practiceDetailPO.setId(existDetail.getId());
            practiceDetailDao.update(practiceDetailPO);
        }
        return true;
    }


    public SubjectDetailDTO getSubjectDetail(SubjectDTO dto) {
        SubjectDetailDTO subjectDetailDTO = new SubjectDetailDTO();
        SubjectPO subjectPO = subjectDao.selectById(dto.getSubjectId());
        if (dto.getSubjectType() == SubjectInfoTypeEnum.RADIO.getCode()) {
            List<SubjectOptionDTO> optionList = new LinkedList<>();
            List<SubjectRadioPO> radioSubjectPOS = subjectRadioDao.selectBySubjectId(subjectPO.getId());
            radioSubjectPOS.forEach(e -> {
                SubjectOptionDTO subjectOptionDTO = new SubjectOptionDTO();
                subjectOptionDTO.setOptionContent(e.getOptionContent());
                subjectOptionDTO.setOptionType(e.getOptionType());
                subjectOptionDTO.setIsCorrect(e.getIsCorrect());
                optionList.add(subjectOptionDTO);
            });
            subjectDetailDTO.setOptionList(optionList);
        }
        if (dto.getSubjectType() == SubjectInfoTypeEnum.MULTIPLE.getCode()) {
            List<SubjectOptionDTO> optionList = new LinkedList<>();
            List<SubjectMultiplePO> multipleSubjectPOS = subjectMultipleDao.selectBySubjectId(subjectPO.getId());
            multipleSubjectPOS.forEach(e -> {
                SubjectOptionDTO subjectOptionDTO = new SubjectOptionDTO();
                subjectOptionDTO.setOptionContent(e.getOptionContent());
                subjectOptionDTO.setOptionType(e.getOptionType());
                subjectOptionDTO.setIsCorrect(e.getIsCorrect());
                optionList.add(subjectOptionDTO);
            });
            subjectDetailDTO.setOptionList(optionList);
        }
        if (dto.getSubjectType() == SubjectInfoTypeEnum.JUDGE.getCode()) {
            SubjectJudgePO judgeSubjectPO = subjectJudgeDao.selectBySubjectId(subjectPO.getId());
            subjectDetailDTO.setIsCorrect(judgeSubjectPO.getIsCorrect());
        }
        subjectDetailDTO.setSubjectParse(subjectPO.getSubjectParse());
        subjectDetailDTO.setSubjectName(subjectPO.getSubjectName());
        return subjectDetailDTO;
    }

    /**
     * 每题得分详情
     * @param req
     * @return
     */
    @Override
    public List<ScoreDetailVO> getScoreDetail(GetScoreDetailReq req) {
        Long practiceId = req.getPracticeId();
        List<ScoreDetailVO> list = new LinkedList<>();
        List<PracticeDetailPO> practiceDetailPOList = practiceDetailDao.selectByPracticeId(practiceId);
        if (CollectionUtils.isEmpty(practiceDetailPOList)) {
            return Collections.emptyList();
        }
        practiceDetailPOList.forEach(po -> {
            ScoreDetailVO scoreDetailVO = new ScoreDetailVO();
            scoreDetailVO.setSubjectId(po.getSubjectId());
            scoreDetailVO.setSubjectType(po.getSubjectType());
            scoreDetailVO.setIsCorrect(po.getAnswerStatus());
            list.add(scoreDetailVO);
        });
        return list;
    }

}