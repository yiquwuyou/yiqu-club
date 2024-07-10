package com.yiquwuyou.practice.server.entity.po;

import lombok.Data;

/**
 * 小分类实体类
 */
@Data
public class PrimaryCategoryPO {

    // 分类id
    private Long id;

    // 分类名称
    private String categoryName;

    // 分类类型
    private Integer categoryType;

    // 父级id
    private Long parentId;

}
