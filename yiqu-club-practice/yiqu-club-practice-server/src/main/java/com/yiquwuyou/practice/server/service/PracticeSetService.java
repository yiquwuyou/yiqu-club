package com.yiquwuyou.practice.server.service;

import com.yiquwuyou.practice.api.req.GetPracticeSubjectsReq;
import com.yiquwuyou.practice.api.vo.PracticeSetVO;
import com.yiquwuyou.practice.api.vo.PracticeSubjectListVO;
import com.yiquwuyou.practice.api.vo.PracticeSubjectVO;
import com.yiquwuyou.practice.api.vo.SpecialPracticeVO;
import com.yiquwuyou.practice.server.entity.dto.PracticeSubjectDTO;

import java.util.List;

public interface PracticeSetService {

    /**
     * 获取专项练习内容
     * 即获取有单选、多选、判断题的标签位置信息
     * 返回值为大分类包含着若干个小分类，小分类包含着若干个标签
     * 只要有上述三种题型任意至少一种题型的标签，都会获取并返回
     */
    List<SpecialPracticeVO> getSpecialPracticeContent();

    /**
     * 开始练习
     */
    PracticeSetVO addPractice(PracticeSubjectDTO dto);

    /**
     * 获取练习题
     */
    PracticeSubjectListVO getSubjects(GetPracticeSubjectsReq req);

    /**
     * 获取题目
     */
    PracticeSubjectVO getPracticeSubject(PracticeSubjectDTO dto);

}
