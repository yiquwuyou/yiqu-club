package com.yiquwuyou.practice.server.dao;

import com.yiquwuyou.practice.server.entity.po.SubjectMultiplePO;

import java.util.List;

public interface SubjectMultipleDao {

    /**
     * 查询题目
     */
    List<SubjectMultiplePO> selectBySubjectId(Long subjectId);

}