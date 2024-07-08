package com.yiquwuyou.subject.common.entity;

import lombok.Data;

/**
 * 分页请求实体
 *
 * @author: yiquwuyou
 * @date: 2023/10/5
 */
@Data
public class PageInfo {

    // 当前页数
    private Integer pageNo = 1;

    // 每页数据的数量
    private Integer pageSize = 20;

    public Integer getPageNo() {
        if (pageNo == null || pageNo < 1) {
            return 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1 || pageSize > Integer.MAX_VALUE) {
            return 20;
        }
        return pageSize;
    }


}
