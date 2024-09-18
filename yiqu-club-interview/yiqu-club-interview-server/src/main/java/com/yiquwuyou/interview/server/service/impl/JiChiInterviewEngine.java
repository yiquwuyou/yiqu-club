package com.yiquwuyou.interview.server.service.impl;

import com.yiquwuyou.interview.api.enums.EngineEnum;
import com.yiquwuyou.interview.api.req.StartReq;
import com.yiquwuyou.interview.api.vo.InterviewQuestionVO;
import com.yiquwuyou.interview.api.vo.InterviewVO;
import com.yiquwuyou.interview.server.dao.SubjectDao;
import com.yiquwuyou.interview.server.entity.po.SubjectCategory;
import com.yiquwuyou.interview.server.entity.po.SubjectInfo;
import com.yiquwuyou.interview.server.entity.po.SubjectLabel;
import com.yiquwuyou.interview.server.service.InterviewEngine;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JiChiInterviewEngine implements InterviewEngine {


    private List<SubjectLabel> labels;
    private Map<Long, SubjectCategory> categoryMap;

    @PostConstruct
    public void init() {
        labels = subjectDao.listAllLabel();
        categoryMap = subjectDao.listAllCategory().stream().collect(Collectors.toMap(SubjectCategory::getId, Function.identity()));
    }

    @Resource
    private SubjectDao subjectDao;

    @Override
    public EngineEnum engineType() {
        return EngineEnum.JI_CHI;
    }

    @Override
    public InterviewVO analyse(List<String> KeyWords) {

        if (CollectionUtils.isEmpty(KeyWords)) {
            return new InterviewVO();
        }
        List<InterviewVO.Interview> views = this.labels.stream().filter(item -> KeyWords.contains(item.getLabelName())).map(item -> {
            InterviewVO.Interview interview = new InterviewVO.Interview();
            SubjectCategory subjectCategory = categoryMap.get(item.getCategoryId());
            if (Objects.nonNull(subjectCategory)) {
                interview.setKeyWord(String.format("%s-%s", subjectCategory.getCategoryName(), item.getLabelName()));
            } else {
                interview.setKeyWord(item.getLabelName());
            }
            interview.setCategoryId(item.getCategoryId());
            interview.setLabelId(item.getId());
            return interview;
        }).collect(Collectors.toList());

        InterviewVO vo = new InterviewVO();
        vo.setQuestionList(views);
        return vo;

    }

    @Override
    public InterviewQuestionVO start(StartReq req) {

        List<Long> ids = req.getQuestionList().stream().map(StartReq.Key::getLabelId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return new InterviewQuestionVO();
        }
        List<SubjectInfo> subjectInfos = subjectDao.listSubjectByLabelIds(ids);
        List<InterviewQuestionVO.Interview> views = subjectInfos.stream().map(item -> {
            InterviewQuestionVO.Interview view = new InterviewQuestionVO.Interview();
            view.setSubjectName(item.getSubjectName());
            view.setSubjectAnswer(item.getSubjectAnswer());
            view.setLabelName(item.getLabelName());
            view.setKeyWord(String.format("%s-%s", item.getCategoryName(), item.getLabelName()));
            return view;
        }).collect(Collectors.toList());
        if (views.size() > 8) {
            Collections.shuffle(views);
            views = views.subList(0, 8);
        }
        InterviewQuestionVO vo = new InterviewQuestionVO();
        vo.setQuestionList(views);
        return vo;

    }

}
