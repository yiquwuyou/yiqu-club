package com.yiquwuyou.auth.application.convert;

import com.yiquwuyou.auth.application.dto.AuthRolePermissionDTO;
import com.yiquwuyou.auth.domain.entity.AuthRolePermissionBO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-12T10:17:26+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_192 (Oracle Corporation)"
)
public class AuthRolePermissionDTOConverterImpl implements AuthRolePermissionDTOConverter {

    @Override
    public AuthRolePermissionBO convertDTOToBO(AuthRolePermissionDTO authRolePermissionDTO) {
        if ( authRolePermissionDTO == null ) {
            return null;
        }

        AuthRolePermissionBO authRolePermissionBO = new AuthRolePermissionBO();

        return authRolePermissionBO;
    }
}
