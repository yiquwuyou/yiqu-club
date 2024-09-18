package com.yiquwuyou.interview.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiquwuyou.interview.api.common.PageResult;
import com.yiquwuyou.interview.api.req.InterviewHistoryReq;
import com.yiquwuyou.interview.api.req.InterviewSubmitReq;
import com.yiquwuyou.interview.api.vo.InterviewHistoryVO;
import com.yiquwuyou.interview.api.vo.InterviewResultVO;
import com.yiquwuyou.interview.server.entity.po.InterviewHistory;

/**
 * 面试汇总记录表(InterviewHistory)表服务接口
 *
 * @author makejava
 * @since 2024-05-23 22:56:03
 */
public interface InterviewHistoryService extends IService<InterviewHistory> {

    void logInterview(InterviewSubmitReq req, InterviewResultVO submit);


    PageResult<InterviewHistoryVO> getHistory(InterviewHistoryReq req);

}
