package com.yiquwuyou.practice.server.service;

import com.yiquwuyou.practice.api.req.SubmitPracticeDetailReq;

public interface PracticeDetailService {

    /**
     * 提交练题情况
     */
    Boolean submit(SubmitPracticeDetailReq req);


}