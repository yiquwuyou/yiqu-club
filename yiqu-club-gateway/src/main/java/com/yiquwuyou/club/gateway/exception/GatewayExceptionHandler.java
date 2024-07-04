package com.yiquwuyou.club.gateway.exception;

import cn.dev33.satoken.exception.SaTokenException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiquwuyou.club.gateway.entity.Result;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理
 * 该类用于处理网关中发生的所有异常，并统一返回格式化的JSON响应。
 * @author: yiquwuyou
 * @date: 2023/10/28
 */
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    // 创建一个ObjectMapper实例，用于JSON的序列化和反序列化
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 处理异常的方法
     *
     * @param serverWebExchange 表示当前的HTTP请求-响应交互
     * @param throwable 表示捕获到的异常
     * @return 返回Mono<Void>，表示这是一个异步的、无返回值的操作
     */
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        // 获取HTTP请求和响应对象
        ServerHttpRequest request = serverWebExchange.getRequest();
        ServerHttpResponse response = serverWebExchange.getResponse();
        // 初始化响应码和消息
        Integer code = 200;
        String message = "";
        // 判断异常类型，进行相应处理
        if (throwable instanceof SaTokenException) {
            // 如果是SaToken异常，则认为是用户无权限
            code = 401;
            message = "用户无权限";
        } else {
            // 其他异常，认为是系统繁忙
            code = 500;
            message = "系统繁忙";
        }
        // 创建一个Result对象，封装响应码和消息
        Result result = Result.fail(code, message);
        // 设置响应的Content-Type为application/json
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 使用Mono.fromSupplier创建一个Mono序列，用于异步地写入响应体
        return response.writeWith(Mono.fromSupplier(() -> {
            // 获取DataBufferFactory，用于创建DataBuffer
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            // 将Result对象序列化为字节数组
            byte[] bytes = null;
            try {
                bytes = objectMapper.writeValueAsBytes(result);
            } catch (JsonProcessingException e) {
                // 如果序列化失败，则打印堆栈跟踪
                e.printStackTrace();
            }
            // 使用DataBufferFactory将字节数组包装成DataBuffer，以便写入响应体
            return dataBufferFactory.wrap(bytes);
        }));
    }
}