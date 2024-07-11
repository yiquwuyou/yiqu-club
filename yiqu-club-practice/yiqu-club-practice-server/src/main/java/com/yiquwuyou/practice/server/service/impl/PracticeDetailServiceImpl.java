package com.yiquwuyou.practice.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.yiquwuyou.practice.api.enums.CompleteStatusEnum;
import com.yiquwuyou.practice.api.enums.IsDeletedFlagEnum;
import com.yiquwuyou.practice.api.req.SubmitPracticeDetailReq;
import com.yiquwuyou.practice.server.dao.PracticeDao;
import com.yiquwuyou.practice.server.dao.PracticeDetailDao;
import com.yiquwuyou.practice.server.dao.PracticeSetDao;
import com.yiquwuyou.practice.server.dao.PracticeSetDetailDao;
import com.yiquwuyou.practice.server.entity.po.PracticeDetailPO;
import com.yiquwuyou.practice.server.entity.po.PracticePO;
import com.yiquwuyou.practice.server.entity.po.PracticeSetDetailPO;
import com.yiquwuyou.practice.server.service.PracticeDetailService;
import com.yiquwuyou.practice.server.util.DateUtils;
import com.yiquwuyou.practice.server.util.LoginUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

}