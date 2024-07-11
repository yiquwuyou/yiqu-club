package com.yiquwuyou.practice.server.dao;

import com.yiquwuyou.practice.server.entity.po.PracticeSetPO;

public interface PracticeSetDao {

    /**
     * 新增套题
     */
    int add(PracticeSetPO po);

    PracticeSetPO selectById(Long setId);

    int updateHeat(Long setId);
}