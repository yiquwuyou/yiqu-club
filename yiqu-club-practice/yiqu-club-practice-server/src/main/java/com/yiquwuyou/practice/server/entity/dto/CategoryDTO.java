package com.yiquwuyou.practice.server.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 分类数据传输对象
 */
@Data
public class CategoryDTO {

    /**
     * 题目类型列表
     */
    private List<Integer> subjectTypeList;

    /**
     * 分类类型
     */
    private Integer categoryType;

    /**
     * 父分类ID
     */
    private Long parentId;

}
