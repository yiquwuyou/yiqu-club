package com.yiquwuyou.practice.server.entity.po;

import lombok.Data;

@Data
public class LabelCountPO {

    // 标签id
    private Long labelId;

    // 该标签下的题目数量
    private Integer count;

    // 标签名称
    private String labelName;

}
