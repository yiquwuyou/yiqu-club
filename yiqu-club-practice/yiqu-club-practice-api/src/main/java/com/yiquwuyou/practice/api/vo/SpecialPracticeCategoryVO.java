package com.yiquwuyou.practice.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 存放小分类信息
 */
@Data
public class SpecialPracticeCategoryVO implements Serializable {

    /**
     * 小分类名称
     */
    private String categoryName;

    /**
     * 小分类ID
     */
    private Long categoryId;

    /**
     * 标签列表
     */
    private List<SpecialPracticeLabelVO> labelList;

}
