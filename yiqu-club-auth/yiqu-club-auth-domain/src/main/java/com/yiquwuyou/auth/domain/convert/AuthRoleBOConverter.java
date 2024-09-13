package com.yiquwuyou.auth.domain.convert;

import com.yiquwuyou.auth.domain.entity.AuthRoleBO;
import com.yiquwuyou.auth.domain.entity.AuthUserBO;
import com.yiquwuyou.auth.infra.basic.entity.AuthRole;
import com.yiquwuyou.auth.infra.basic.entity.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 角色bo转换器
 *
 * @author: yiquwuyou
 * @date: 2023/10/8
 */
@Mapper
public interface AuthRoleBOConverter {

    AuthRoleBOConverter INSTANCE = Mappers.getMapper(AuthRoleBOConverter.class);

    AuthRole convertBOToEntity(AuthRoleBO authRoleBO);

    List<AuthUserBO> convertEntityToBO(List<AuthUser> authUserList);
}
