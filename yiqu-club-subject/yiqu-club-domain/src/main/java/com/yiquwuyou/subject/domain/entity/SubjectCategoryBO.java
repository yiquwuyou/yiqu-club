package com.yiquwuyou.subject.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目分类(SubjectCategory)实体类
 *
 * @author makejava
 * @since 2024-05-15 09:00:48
 */
@Data
public class SubjectCategoryBO implements Serializable {
    private static final long serialVersionUID = -33816459551058651L;
/**
     * 主键
     */
    private Long id;
/**
     * 分类名称
     */
    private String categoryName;
/**
     * 分类类型
     */
    private Integer categoryType;
/**
     * 图标连接
     */
    private String imageUrl;
/**
     * 父级id
     */
    private Long parentId;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 该分类下的所有标签
     */
    private List<SubjectLabelBO> labelBOList;
}

