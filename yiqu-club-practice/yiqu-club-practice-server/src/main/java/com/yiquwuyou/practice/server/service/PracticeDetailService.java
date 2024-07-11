package com.yiquwuyou.practice.server.service;

import com.yiquwuyou.practice.api.req.GetScoreDetailReq;
import com.yiquwuyou.practice.api.req.SubmitPracticeDetailReq;
import com.yiquwuyou.practice.api.req.SubmitSubjectDetailReq;
import com.yiquwuyou.practice.api.vo.ScoreDetailVO;

import java.util.List;

public interface PracticeDetailService {

    /**
     * 练习提交题目
     */
    Boolean submitSubject(SubmitSubjectDetailReq req);

    /**
     * 提交练题情况
     */
    Boolean submit(SubmitPracticeDetailReq req);

    /**
     * 每题得分详情
     */
    List<ScoreDetailVO> getScoreDetail(GetScoreDetailReq req);


}