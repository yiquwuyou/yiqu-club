package com.yiquwuyou.auth.domain.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.yiquwuyou.auth.common.enums.AuthUserStatusEnum;
import com.yiquwuyou.auth.common.enums.IsDeletedFlagEnum;
import com.yiquwuyou.auth.domain.constants.AuthConstant;
import com.yiquwuyou.auth.domain.convert.AuthUserBOConverter;
import com.yiquwuyou.auth.domain.entity.AuthUserBO;
import com.yiquwuyou.auth.domain.service.AuthUserDomainService;
import com.yiquwuyou.auth.infra.basic.entity.AuthRole;
import com.yiquwuyou.auth.infra.basic.entity.AuthUser;
import com.yiquwuyou.auth.infra.basic.entity.AuthUserRole;
import com.yiquwuyou.auth.infra.basic.service.AuthRoleService;
import com.yiquwuyou.auth.infra.basic.service.AuthUserRoleService;
import com.yiquwuyou.auth.infra.basic.service.AuthUserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class AuthUserDomainServiceImpl implements AuthUserDomainService {

    @Resource
    private AuthUserService authUserService;

    @Resource
    private AuthUserRoleService authUserRoleService;

    @Resource
    private AuthRoleService authRoleService;

    private String salt = "chicken";


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Boolean register(AuthUserBO authUserBO) {

        // 往 user 表中注册
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        authUser.setPassword(SaSecureUtil.md5BySalt(authUser.getPassword(), salt));
        authUser.setStatus(AuthUserStatusEnum.OPEN.getCode());
        authUser.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        // 插入完后，authUser 的 id 会被赋值
        Integer count = authUserService.insert(authUser);

        // 往 user_role 表中注册，建立一个初步的角色的关联
        AuthRole authRole = new AuthRole();
        authRole.setRoleKey(AuthConstant.NORMAL_USER);
        AuthRole roleResult = authRoleService.queryByCondition(authRole);
        Long roleId = roleResult.getId();
        Long userId = authUser.getId();
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setUserId(userId);
        authUserRole.setRoleId(roleId);
        authUserRole.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        authUserRoleService.insert(authUserRole);
        return count > 0;
    }

    @Override
    public Boolean update(AuthUserBO authUserBO) {
        AuthUser authUser = AuthUserBOConverter.INSTANCE.convertBOToEntity(authUserBO);
        Integer count = authUserService.update(authUser);
        //有任何的更新，都要与缓存进行同步的修改
        return count > 0;
    }

    @Override
    public Boolean delete(AuthUserBO authUserBO) {
        AuthUser authUser = new AuthUser();
        authUser.setId(authUserBO.getId());
        authUser.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        Integer count = authUserService.update(authUser);
        //有任何的更新，都要与缓存进行同步的修改
        return count > 0;
    }
}
