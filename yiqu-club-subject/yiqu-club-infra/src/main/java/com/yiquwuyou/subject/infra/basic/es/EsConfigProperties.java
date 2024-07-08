package com.yiquwuyou.subject.infra.basic.es;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * es配置类，管理集群配置信息
 *
 * @date: 2023/12/17
 */
@Component
@ConfigurationProperties(prefix = "es.cluster")
public class EsConfigProperties {

    private List<EsClusterConfig> esConfigs = new ArrayList<>();

    public List<EsClusterConfig> getEsConfigs() {
        return esConfigs;
    }

    public void setEsConfigs(List<EsClusterConfig> esConfigs) {
        this.esConfigs = esConfigs;
    }
}
