package com.yiquwuyou.subject.domain.convert;

import com.yiquwuyou.subject.domain.entity.SubjectLabelBO;
import com.yiquwuyou.subject.infra.basic.entity.SubjectLabel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-12T10:17:46+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_192 (Oracle Corporation)"
)
public class SubjectLabelConverterImpl implements SubjectLabelConverter {

    @Override
    public SubjectLabel convertBoToLabel(SubjectLabelBO subjectLabelBO) {
        if ( subjectLabelBO == null ) {
            return null;
        }

        SubjectLabel subjectLabel = new SubjectLabel();

        subjectLabel.setId( subjectLabelBO.getId() );
        subjectLabel.setLabelName( subjectLabelBO.getLabelName() );
        subjectLabel.setCategoryId( subjectLabelBO.getCategoryId() );
        subjectLabel.setSortNum( subjectLabelBO.getSortNum() );
        subjectLabel.setCreatedBy( subjectLabelBO.getCreatedBy() );
        subjectLabel.setCreatedTime( subjectLabelBO.getCreatedTime() );
        subjectLabel.setUpdateBy( subjectLabelBO.getUpdateBy() );
        subjectLabel.setUpdateTime( subjectLabelBO.getUpdateTime() );
        subjectLabel.setIsDeleted( subjectLabelBO.getIsDeleted() );

        return subjectLabel;
    }

    @Override
    public List<SubjectLabelBO> convertLabelToBoList(List<SubjectLabel> subjectLabelList) {
        if ( subjectLabelList == null ) {
            return null;
        }

        List<SubjectLabelBO> list = new ArrayList<SubjectLabelBO>( subjectLabelList.size() );
        for ( SubjectLabel subjectLabel : subjectLabelList ) {
            list.add( subjectLabelToSubjectLabelBO( subjectLabel ) );
        }

        return list;
    }

    protected SubjectLabelBO subjectLabelToSubjectLabelBO(SubjectLabel subjectLabel) {
        if ( subjectLabel == null ) {
            return null;
        }

        SubjectLabelBO subjectLabelBO = new SubjectLabelBO();

        subjectLabelBO.setId( subjectLabel.getId() );
        subjectLabelBO.setLabelName( subjectLabel.getLabelName() );
        subjectLabelBO.setCategoryId( subjectLabel.getCategoryId() );
        subjectLabelBO.setSortNum( subjectLabel.getSortNum() );
        subjectLabelBO.setCreatedBy( subjectLabel.getCreatedBy() );
        subjectLabelBO.setCreatedTime( subjectLabel.getCreatedTime() );
        subjectLabelBO.setUpdateBy( subjectLabel.getUpdateBy() );
        subjectLabelBO.setUpdateTime( subjectLabel.getUpdateTime() );
        subjectLabelBO.setIsDeleted( subjectLabel.getIsDeleted() );

        return subjectLabelBO;
    }
}
