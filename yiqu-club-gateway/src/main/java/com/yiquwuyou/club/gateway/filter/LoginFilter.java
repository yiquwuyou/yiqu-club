package com.yiquwuyou.club.gateway.filter;

import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 登录拦截器
 * 获取登录用户信息，并将用户信息添加到请求头中，向下传递
 * @author: ChickenWing
 * @date: 2023/11/26
 */
@Component
@Slf4j
public class LoginFilter implements GlobalFilter {

    // 实现GlobalFilter接口的filter方法，该方法会在请求通过网关时被调用。
    // ServerWebExchange封装了HTTP请求和响应，以及与之相关的属性（如会话、请求属性等）
    @Override
    @SneakyThrows
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 每次请求都set一下上下文
        SaReactorSyncHolder.setContext(exchange);
        // 获取当前请求的ServerHttpRequest对象。
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 创建一个ServerHttpRequest.Builder对象，用于修改原始请求。
        ServerHttpRequest.Builder mutate = request.mutate();
        // 获取请求的URL路径。
        String url = request.getURI().getPath();
        // 记录当前请求的URL路径到日志中。
        log.info("LoginFilter.filter.url:{}", url);

        // 判断请求的URL是否为登录URL，如果是，则直接放行，不进行后续校验。
        if (url.equals("/user/doLogin")) {
            return chain.filter(exchange); // 调用chain.filter方法继续执行下一个过滤器或目标服务。
        }

        // 尝试从Sa-Token中获取当前用户的Token信息。
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("LoginFilter.filter.url:{}", new Gson().toJson(tokenInfo));
        // 从Token信息中获取登录ID。
        String loginId = (String) tokenInfo.getLoginId();
        // 将登录ID添加到请求头中，以便后续处理。
        mutate.header("loginId", loginId);
        // 构建修改后的请求，并继续执行过滤器链。
        // 注意：这里通过exchange.mutate().request(mutate.build()).build()来创建一个新的ServerWebExchange对象，
        // 它包含了修改后的请求。这是响应式编程中常见的做法，用于在不修改原始对象的情况下生成新的对象。
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

}
