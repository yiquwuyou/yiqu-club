package com.yiquwuyou.auth.domain.convert;

import com.yiquwuyou.auth.domain.entity.AuthPermissionBO;

import com.yiquwuyou.auth.infra.basic.entity.AuthPermission;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 权限bo转换器
 * 
 * @author: yiquwuyou
 * @date: 2023/10/8
 */
@Mapper
public interface AuthPermissionBOConverter {

    AuthPermissionBOConverter INSTANCE = Mappers.getMapper(AuthPermissionBOConverter.class);

    AuthPermission convertBOToEntity(AuthPermissionBO authPermissionBO);

}
