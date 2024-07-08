package com.yiquwuyou.subject.application.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yiquwuyou.subject.application.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * mvc的全局处理
 * 
 * @author: yiquwuyou
 * @date: 2023/10/7
 */
@Configuration
public class GlobalConfig extends WebMvcConfigurationSupport {

    // 配置自定义的HTTP消息转换器
    // 转换器用于在HTTP请求和响应中处理不同的媒体类型（如JSON、XML等）。
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 调用父类的配置方法，以确保Spring MVC的默认转换器被添加到列表中
        super.configureMessageConverters(converters);
        // 向转换器列表中添加我们自定义的MappingJackson2HttpMessageConverter
        converters.add(mappingJackson2HttpMessageConverter());
    }

    // 配置拦截器
    // 将自定义拦截器往里放/注册
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**");
    }

    // 创建自定义义的转换器，用于配置Jackson的序列化行为
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        // 创建一个ObjectMapper实例，用于配置Jackson的序列化行为
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置ObjectMapper，当遇到空的JavaBean（即没有任何属性或所有属性都是null的Bean）时，不抛出异常
        // 这允许空的Bean被序列化为空的JSON对象（{}）
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 配置ObjectMapper，在序列化时忽略所有null值，使生成的JSON更加紧凑
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 使用配置好的ObjectMapper创建MappingJackson2HttpMessageConverter实例
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        // 返回配置好的转换器实例
        return converter;
    }


}