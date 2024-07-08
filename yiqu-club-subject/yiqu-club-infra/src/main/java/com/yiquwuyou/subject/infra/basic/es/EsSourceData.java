package com.yiquwuyou.subject.infra.basic.es;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 封装的返回类
 */
@Data
public class EsSourceData implements Serializable {

    // 文档的唯一标识符
    private String docId;

    // 文档的数据
    private Map<String, Object> data;

}
