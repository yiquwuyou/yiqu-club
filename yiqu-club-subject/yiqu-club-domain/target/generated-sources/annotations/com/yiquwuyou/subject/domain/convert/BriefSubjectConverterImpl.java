package com.yiquwuyou.subject.domain.convert;

import com.yiquwuyou.subject.domain.entity.SubjectInfoBO;
import com.yiquwuyou.subject.infra.basic.entity.SubjectBrief;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-18T20:49:55+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_192 (Oracle Corporation)"
)
public class BriefSubjectConverterImpl implements BriefSubjectConverter {

    @Override
    public SubjectBrief convertBoToEntity(SubjectInfoBO subjectInfoBO) {
        if ( subjectInfoBO == null ) {
            return null;
        }

        SubjectBrief subjectBrief = new SubjectBrief();

        subjectBrief.setId( subjectInfoBO.getId() );
        subjectBrief.setSubjectAnswer( subjectInfoBO.getSubjectAnswer() );
        subjectBrief.setCreatedBy( subjectInfoBO.getCreatedBy() );

        return subjectBrief;
    }
}
