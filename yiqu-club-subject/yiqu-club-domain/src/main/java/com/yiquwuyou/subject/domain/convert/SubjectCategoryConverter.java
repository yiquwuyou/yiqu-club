package com.yiquwuyou.subject.domain.convert;

import com.yiquwuyou.subject.domain.entity.SubjectCategoryBO;
import com.yiquwuyou.subject.infra.basic.entity.SubjectCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

// mappper是mapstruct的mapper，别引错了
// 对象间的转换
@Mapper
public interface SubjectCategoryConverter {
    SubjectCategoryConverter INSTANCE = Mappers.getMapper(SubjectCategoryConverter.class);

    SubjectCategory convertBoToCategory(SubjectCategoryBO subjectCategoryBO);

    List<SubjectCategoryBO> convertBoToCategory(List<SubjectCategory> categoryList);
}
