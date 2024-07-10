package com.yiquwuyou.practice.server.dao;

import com.yiquwuyou.practice.server.entity.dto.PracticeSubjectDTO;
import com.yiquwuyou.practice.server.entity.po.SubjectPO;

import java.util.List;

public interface SubjectDao {


    /**
     * 获取练习面试题目
     */
    List<SubjectPO> getPracticeSubject(PracticeSubjectDTO dto);


}