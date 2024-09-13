package com.yiquwuyou.auth.application.convert;

import com.yiquwuyou.auth.domain.entity.AuthUserBO;
import com.yiquwuyou.auth.entity.AuthUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户dto转换器
 *
 * @author: yiquwuyou
 * @date: 2023/10/8
 */
@Mapper
public interface AuthUserDTOConverter {

    AuthUserDTOConverter INSTANCE = Mappers.getMapper(AuthUserDTOConverter.class);

    AuthUserBO convertDTOToBO(AuthUserDTO authUserDTO);

    AuthUserDTO convertBOToDTO(AuthUserBO authUserBO);

    List<AuthUserDTO> convertBOToDTO(List<AuthUserBO> authUserBO);

}
