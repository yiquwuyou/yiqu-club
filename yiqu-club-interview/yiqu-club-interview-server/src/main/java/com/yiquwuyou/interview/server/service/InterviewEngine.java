package com.yiquwuyou.interview.server.service;

import com.yiquwuyou.interview.api.enums.EngineEnum;
import com.yiquwuyou.interview.api.req.StartReq;
import com.yiquwuyou.interview.api.vo.InterviewQuestionVO;
import com.yiquwuyou.interview.api.vo.InterviewVO;

import java.util.List;

/**
 * <p>
 * 面试引擎
 * </p>
 *
 * @author ChickenWing
 * @since 2024/05/16
 */
public interface InterviewEngine {

    /**
     * 引擎类型
     */
    EngineEnum engineType();

    /**
     * 通过简历关键字获取面试关键字
     */
    InterviewVO analyse(List<String> KeyWords);

    /**
     * 通过面试关键字获取面试题
     */
    InterviewQuestionVO start(StartReq req);

}
