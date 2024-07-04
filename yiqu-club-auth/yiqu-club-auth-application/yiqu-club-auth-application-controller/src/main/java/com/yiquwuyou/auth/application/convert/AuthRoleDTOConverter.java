package com.yiquwuyou.auth.application.convert;

import com.yiquwuyou.auth.application.dto.AuthRoleDTO;
import com.yiquwuyou.auth.domain.entity.AuthRoleBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 角色dto转换器
 * 
 * @author: yiquwuyou
 * @date: 2023/10/8
 */
@Mapper
public interface AuthRoleDTOConverter {

    AuthRoleDTOConverter INSTANCE = Mappers.getMapper(AuthRoleDTOConverter.class);

    AuthRoleBO convertDTOToBO(AuthRoleDTO authRoleDTO);

}
