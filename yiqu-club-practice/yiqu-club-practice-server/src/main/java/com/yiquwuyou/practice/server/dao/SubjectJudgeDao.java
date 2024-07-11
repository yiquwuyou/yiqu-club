package com.yiquwuyou.practice.server.dao;

import com.yiquwuyou.practice.server.entity.po.SubjectJudgePO;

public interface SubjectJudgeDao {


    SubjectJudgePO selectBySubjectId(Long repeatSubjectId);


}