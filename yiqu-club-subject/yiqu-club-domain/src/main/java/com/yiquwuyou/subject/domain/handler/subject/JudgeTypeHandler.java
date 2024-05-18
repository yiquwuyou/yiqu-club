package com.yiquwuyou.subject.domain.handler.subject;

import com.yiquwuyou.subject.common.enums.IsDeletedFlagEnum;
import com.yiquwuyou.subject.common.enums.SubjectInfoTypeEnum;
import com.yiquwuyou.subject.domain.entity.SubjectAnswerBO;
import com.yiquwuyou.subject.domain.entity.SubjectInfoBO;
import com.yiquwuyou.subject.domain.entity.SubjectOptionBO;
import com.yiquwuyou.subject.infra.basic.entity.SubjectJudge;
import com.yiquwuyou.subject.infra.basic.service.SubjectJudgeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 判断题目的策略类
 *
 * @author: ChickenWing
 * @date: 2023/10/5
 */
@Component
public class JudgeTypeHandler implements SubjectTypeHandler{

    @Resource
    private SubjectJudgeService subjectJudgeService;

    @Override
    public SubjectInfoTypeEnum getHandlerType() {
        return SubjectInfoTypeEnum.JUDGE;
    }

    @Override
    public void add(SubjectInfoBO subjectInfoBO) {
        //判断题目的插入
        SubjectJudge subjectJudge = new SubjectJudge();
        SubjectAnswerBO subjectAnswerBO = subjectInfoBO.getOptionList().get(0);
        subjectJudge.setSubjectId(subjectInfoBO.getId());
        subjectJudge.setIsCorrect(subjectAnswerBO.getIsCorrect());
        subjectJudge.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        subjectJudgeService.insert(subjectJudge);
    }

    @Override
    public SubjectOptionBO query(int subjectId) {
        return null;
    }
}