package com.yiquwuyou.auth.domain.service;


import cn.dev33.satoken.stp.SaTokenInfo;
import com.yiquwuyou.auth.domain.entity.AuthUserBO;

/**
 * 用户领域service
 * 
 * @author: ChickenWing
 * @date: 2023/11/1
 */
public interface AuthUserDomainService {

    /**
     * 注册
     */
    Boolean register(AuthUserBO authUserBO);

    /**
     * 更新用户信息
     */
    Boolean update(AuthUserBO authUserBO);

    /**
     * 更新用户信息
     */
    Boolean delete(AuthUserBO authUserBO);

    SaTokenInfo doLogin(String validCode);

    AuthUserBO getUserInfo(AuthUserBO authUserBO);
}
