package com.yiquwuyou.subject.application.convert;

import com.yiquwuyou.subject.application.dto.SubjectLikedDTO;
import com.yiquwuyou.subject.domain.entity.SubjectLikedBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 题目点赞表 dto转换器
 *
 * @author yiquwuyou
 * @since 2024-07-09 16:57:53
 */
@Mapper
public interface SubjectLikedDTOConverter {

    SubjectLikedDTOConverter INSTANCE = Mappers.getMapper(SubjectLikedDTOConverter.class);

    SubjectLikedBO convertDTOToBO(SubjectLikedDTO subjectLikedDTO);

}
