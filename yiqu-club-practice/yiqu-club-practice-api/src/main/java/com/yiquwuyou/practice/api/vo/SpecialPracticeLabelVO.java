package com.yiquwuyou.practice.api.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 存放标签信息
 */
@Data
public class SpecialPracticeLabelVO implements Serializable {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 分类id-标签ID
     */
    private String assembleId;  // 因为标签是公用的，比如mysql和redis下都有基础这个标签，所以分类id和标签id共同组成该分类下的标签的唯一标识

    /**
     * 标签名称
     */
    private String labelName;

}
