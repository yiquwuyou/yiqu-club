package com.yiquwuyou.interview.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiquwuyou.interview.api.common.PageInfo;
import com.yiquwuyou.interview.api.common.PageResult;
import com.yiquwuyou.interview.api.enums.IsDeletedFlagEnum;
import com.yiquwuyou.interview.api.req.InterviewSubmitReq;
import com.yiquwuyou.interview.api.vo.InterviewResultVO;
import com.yiquwuyou.interview.server.dao.InterviewHistoryDao;
import com.yiquwuyou.interview.server.dao.InterviewQuestionHistoryDao;
import com.yiquwuyou.interview.server.entity.po.InterviewHistory;
import com.yiquwuyou.interview.server.entity.po.InterviewQuestionHistory;
import com.yiquwuyou.interview.server.service.InterviewHistoryService;
import com.yiquwuyou.interview.server.util.LoginUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 面试汇总记录表(InterviewHistory)表服务实现类
 *
 * @author makejava
 * @since 2024-05-23 22:56:03
 */
@Service("interviewHistoryService")
public class InterviewHistoryServiceImpl extends ServiceImpl<InterviewHistoryDao, InterviewHistory> implements InterviewHistoryService {
    @Resource
    private InterviewHistoryDao interviewHistoryDao;
    @Resource
    private InterviewQuestionHistoryDao interviewQuestionHistoryDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logInterview(InterviewSubmitReq req, InterviewResultVO submit) {

        InterviewHistory history = new InterviewHistory();
        history.setAvgScore(submit.getAvgScore());
        String keyWords = req.getQuestionList().stream().map(InterviewSubmitReq.Submit::getLabelName).distinct().collect(Collectors.joining("、"));
        history.setKeyWords(keyWords);
        history.setTip(submit.getTips());
        history.setInterviewUrl(req.getInterviewUrl());
        history.setCreatedBy(LoginUtil.getLoginId());
        history.setCreatedTime(new Date());
        history.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        interviewHistoryDao.insert(history);
        List<InterviewQuestionHistory> histories = req.getQuestionList().stream().map(item -> {
            InterviewQuestionHistory questionHistory = new InterviewQuestionHistory();
            questionHistory.setInterviewId(history.getId());
            questionHistory.setScore(item.getUserScore());
            questionHistory.setKeyWords(item.getLabelName());
            questionHistory.setQuestion(item.getSubjectName());
            questionHistory.setAnswer(item.getSubjectAnswer());
            questionHistory.setUserAnswer(item.getUserAnswer());
            questionHistory.setCreatedBy(history.getCreatedBy());
            questionHistory.setCreatedTime(history.getCreatedTime());
            questionHistory.setIsDeleted(history.getIsDeleted());
            return questionHistory;
        }).collect(Collectors.toList());
        interviewQuestionHistoryDao.insertBatch(histories);

    }

}
