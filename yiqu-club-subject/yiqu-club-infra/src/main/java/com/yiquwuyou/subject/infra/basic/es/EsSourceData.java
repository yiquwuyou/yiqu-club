package com.yiquwuyou.subject.infra.basic.es;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 封装的返回类
 */
@Data
public class EsSourceData implements Serializable {

    private String docId;

    private Map<String, Object> data;

}
