package com.yiquwuyou.subject.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页返回实体
 *
 * @author: yiquwuyou
 * @date: 2023/10/5
 */
@Data
public class PageResult<T> implements Serializable {

    // 当前页数
    private Integer pageNo = 1;

    // 每页数据的数量
    private Integer pageSize = 20;

    // 总共的数量
    private Integer total = 0;

    // 总页数
    private Integer totalPages = 0;

    // 查询出来的数据
    private List<T> result = Collections.emptyList();

    // 数据开始的位置（要计算）
    private Integer start = 1;

    // 数据结束的位置（要计算）
    private Integer end = 0;

    public void setRecords(List<T> result) {
        this.result = result;
        if (result != null && result.size() > 0) {
            setTotal(result.size());
        }
    }

    // 设置总数的时候，计算总页数，开始位置，结束位置
    public void setTotal(Integer total) {
        this.total = total;
        if (this.pageSize > 0) {
            this.totalPages = (total / this.pageSize) + (total % this.pageSize == 0 ? 0 : 1);
        } else {
            this.totalPages = 0;
        }
        // 计算开始位置和结束位置
        this.start = (this.pageSize > 0 ? (this.pageNo - 1) * this.pageSize : 0) + 1;
        this.end = (this.start - 1 + this.pageSize * (this.pageNo > 0 ? 1 : 0));
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

}
