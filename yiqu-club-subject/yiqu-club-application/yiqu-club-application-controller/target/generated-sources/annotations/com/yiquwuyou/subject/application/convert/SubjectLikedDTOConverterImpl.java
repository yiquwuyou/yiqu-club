package com.yiquwuyou.subject.application.convert;

import com.yiquwuyou.subject.application.dto.SubjectLikedDTO;
import com.yiquwuyou.subject.domain.entity.SubjectLikedBO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-09T23:36:54+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_192 (Oracle Corporation)"
)
public class SubjectLikedDTOConverterImpl implements SubjectLikedDTOConverter {

    @Override
    public SubjectLikedBO convertDTOToBO(SubjectLikedDTO subjectLikedDTO) {
        if ( subjectLikedDTO == null ) {
            return null;
        }

        SubjectLikedBO subjectLikedBO = new SubjectLikedBO();

        subjectLikedBO.setPageNo( subjectLikedDTO.getPageNo() );
        subjectLikedBO.setPageSize( subjectLikedDTO.getPageSize() );
        subjectLikedBO.setId( subjectLikedDTO.getId() );
        subjectLikedBO.setSubjectId( subjectLikedDTO.getSubjectId() );
        subjectLikedBO.setLikeUserId( subjectLikedDTO.getLikeUserId() );
        subjectLikedBO.setStatus( subjectLikedDTO.getStatus() );
        subjectLikedBO.setCreatedBy( subjectLikedDTO.getCreatedBy() );
        subjectLikedBO.setCreatedTime( subjectLikedDTO.getCreatedTime() );
        subjectLikedBO.setUpdateBy( subjectLikedDTO.getUpdateBy() );
        subjectLikedBO.setUpdateTime( subjectLikedDTO.getUpdateTime() );
        subjectLikedBO.setIsDeleted( subjectLikedDTO.getIsDeleted() );

        return subjectLikedBO;
    }
}
