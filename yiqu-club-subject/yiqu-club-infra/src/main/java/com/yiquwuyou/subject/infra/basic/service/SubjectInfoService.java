package com.yiquwuyou.subject.infra.basic.service;

import com.yiquwuyou.subject.infra.basic.entity.SubjectInfo;

import java.util.List;


/**
 * 题目信息表(SubjectInfo)表服务接口
 *
 * @author makejava
 * @since 2024-05-17 23:46:38
 */
public interface SubjectInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SubjectInfo queryById(Long id);

    /**
     * 新增数据
     *
     * @param subjectInfo 实例对象
     * @return 实例对象
     */
    SubjectInfo insert(SubjectInfo subjectInfo);

    /**
     * 修改数据
     *
     * @param subjectInfo 实例对象
     * @return 实例对象
     */
    SubjectInfo update(SubjectInfo subjectInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    // 查询当前分类和标签下的题目数量
    int countByCondition(SubjectInfo subjectInfo, Long categoryId, Long labelId);

    // 分页查询该分类和标签下的题目
    List<SubjectInfo> queryPage(SubjectInfo subjectInfo, Long categoryId, Long labelId, int start, Integer pageSize);

    /**
     * 获取题目贡献榜/排行榜
     */
    List<SubjectInfo> getContributeCount();

    Long querySubjectIdCursor(Long subjectId, Long categoryId, Long labelId, int cursor);
}
