package com.yiquwuyou.subject.application.convert;

import com.yiquwuyou.subject.application.dto.SubjectCategoryDTO;
import com.yiquwuyou.subject.domain.entity.SubjectCategoryBO;
import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import com.yiquwuyou.subject.infra.basic.mapper.SubjectCategoryDao;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

// mappper是mapstruct的mapper，别引错了
// 对象间的转换
@Mapper
public interface SubjectCategoryDTOConverter {
    SubjectCategoryDTOConverter INSTANCE = Mappers.getMapper(SubjectCategoryDTOConverter.class);

    SubjectCategoryBO convertBoToCategory(SubjectCategoryDTO subjectCategoryDTO);

    List<SubjectCategoryDTO> convertBoToCategoryDTOList(List<SubjectCategoryBO> subjectCategoryBOS);
}
