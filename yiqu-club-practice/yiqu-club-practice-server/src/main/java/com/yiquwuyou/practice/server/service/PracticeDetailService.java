package com.yiquwuyou.practice.server.service;

import com.yiquwuyou.practice.api.req.SubmitPracticeDetailReq;
import com.yiquwuyou.practice.api.req.SubmitSubjectDetailReq;

public interface PracticeDetailService {

    /**
     * 练习提交题目
     */
    Boolean submitSubject(SubmitSubjectDetailReq req);

    /**
     * 提交练题情况
     */
    Boolean submit(SubmitPracticeDetailReq req);


}