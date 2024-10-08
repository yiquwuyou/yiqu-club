package com.yiquwuyou.subject.domain.service;


import com.yiquwuyou.subject.common.entity.PageResult;
import com.yiquwuyou.subject.domain.entity.SubjectLikedBO;

/**
 * 题目点赞表 领域service
 *
 * @author yiquwuyou
 * @since 2024-07-09 16:57:53
 */
public interface SubjectLikedDomainService {

    /**
     * 添加 题目点赞表 信息
     */
    void add(SubjectLikedBO subjectLikedBO);

    /**
     * 更新 题目点赞表 信息
     */
    Boolean update(SubjectLikedBO subjectLikedBO);

    /**
     * 删除 题目点赞表 信息
     */
    Boolean delete(SubjectLikedBO subjectLikedBO);

    /**
     * 获取当前是否被点赞过
     */
    Boolean isLiked(String subjectId, String userId);

    /**
     * 获取点赞数量
     */
    Integer getLikedCount(String subjectId);

    /**
     * 同步点赞数据
     */
    void syncLiked();


    /**
     * 获取 我的点赞
     * @param subjectLikedBO
     * @return
     */
    PageResult<SubjectLikedBO> getSubjectLikedPage(SubjectLikedBO subjectLikedBO);

    void syncLikedByMsg(SubjectLikedBO subjectLikedBO);
}
