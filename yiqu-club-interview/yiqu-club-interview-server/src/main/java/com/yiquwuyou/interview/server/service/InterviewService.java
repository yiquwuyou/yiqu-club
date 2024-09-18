package com.yiquwuyou.interview.server.service;

import com.yiquwuyou.interview.api.req.InterviewReq;
import com.yiquwuyou.interview.api.req.InterviewSubmitReq;
import com.yiquwuyou.interview.api.req.StartReq;
import com.yiquwuyou.interview.api.vo.InterviewQuestionVO;
import com.yiquwuyou.interview.api.vo.InterviewResultVO;
import com.yiquwuyou.interview.api.vo.InterviewVO;

public interface InterviewService {

    InterviewVO analyse(InterviewReq req);

    InterviewQuestionVO start(StartReq req);

    InterviewResultVO submit(InterviewSubmitReq req);
}
