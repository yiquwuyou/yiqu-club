package com.yiquwuyou.interview.server.service;

import com.yiquwuyou.interview.api.req.InterviewReq;
import com.yiquwuyou.interview.api.vo.InterviewVO;

public interface InterviewService {

    InterviewVO analyse(InterviewReq req);

}
