package com.yiquwuyou.practice.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 专项练习的信息
 * 大分类->小分类->标签
 */
@Data
public class SpecialPracticeVO implements Serializable {

    /**
     * 大分类名称
     */
    private String primaryCategoryName;

    /**
     * 大分类ID
     */
    private Long primaryCategoryId;

    /**
     * 大分类下的小分类列表
     */
    private List<SpecialPracticeCategoryVO> categoryList;

}
