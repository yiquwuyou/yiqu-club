package com.yiquwuyou.subject.application.convert;

import com.yiquwuyou.subject.application.dto.SubjectLabelDTO;
import com.yiquwuyou.subject.domain.entity.SubjectLabelBO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-09T23:36:54+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_192 (Oracle Corporation)"
)
public class SubjectLabelDTOConverterImpl implements SubjectLabelDTOConverter {

    @Override
    public SubjectLabelBO convertDtoToLabelBO(SubjectLabelDTO subjectLabelDTO) {
        if ( subjectLabelDTO == null ) {
            return null;
        }

        SubjectLabelBO subjectLabelBO = new SubjectLabelBO();

        subjectLabelBO.setId( subjectLabelDTO.getId() );
        subjectLabelBO.setLabelName( subjectLabelDTO.getLabelName() );
        subjectLabelBO.setCategoryId( subjectLabelDTO.getCategoryId() );
        subjectLabelBO.setSortNum( subjectLabelDTO.getSortNum() );
        subjectLabelBO.setCreatedBy( subjectLabelDTO.getCreatedBy() );
        subjectLabelBO.setCreatedTime( subjectLabelDTO.getCreatedTime() );
        subjectLabelBO.setUpdateBy( subjectLabelDTO.getUpdateBy() );
        subjectLabelBO.setUpdateTime( subjectLabelDTO.getUpdateTime() );
        subjectLabelBO.setIsDeleted( subjectLabelDTO.getIsDeleted() );

        return subjectLabelBO;
    }

    @Override
    public List<SubjectLabelDTO> convertBOToLabelDTOList(List<SubjectLabelBO> boList) {
        if ( boList == null ) {
            return null;
        }

        List<SubjectLabelDTO> list = new ArrayList<SubjectLabelDTO>( boList.size() );
        for ( SubjectLabelBO subjectLabelBO : boList ) {
            list.add( subjectLabelBOToSubjectLabelDTO( subjectLabelBO ) );
        }

        return list;
    }

    protected SubjectLabelDTO subjectLabelBOToSubjectLabelDTO(SubjectLabelBO subjectLabelBO) {
        if ( subjectLabelBO == null ) {
            return null;
        }

        SubjectLabelDTO subjectLabelDTO = new SubjectLabelDTO();

        subjectLabelDTO.setId( subjectLabelBO.getId() );
        subjectLabelDTO.setLabelName( subjectLabelBO.getLabelName() );
        subjectLabelDTO.setCategoryId( subjectLabelBO.getCategoryId() );
        subjectLabelDTO.setSortNum( subjectLabelBO.getSortNum() );
        subjectLabelDTO.setCreatedBy( subjectLabelBO.getCreatedBy() );
        subjectLabelDTO.setCreatedTime( subjectLabelBO.getCreatedTime() );
        subjectLabelDTO.setUpdateBy( subjectLabelBO.getUpdateBy() );
        subjectLabelDTO.setUpdateTime( subjectLabelBO.getUpdateTime() );
        subjectLabelDTO.setIsDeleted( subjectLabelBO.getIsDeleted() );

        return subjectLabelDTO;
    }
}
