package com.yiquwuyou.subject.infra.basic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;


/**
 *  es中subject_index索引的文档实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
// 使用Spring Data Elasticsearch的@Document注解来映射Elasticsearch的文档  
// indexName指定了Elasticsearch中的索引名称，createIndex=false表示在应用程序启动时不会自动创建索引  
@Document(indexName = "subject_index", createIndex = false)
public class SubjectInfoEs {  
  
    // 使用Spring Data Elasticsearch的@Id注解来标识这个字段作为文档的唯一标识符  
    // @Field注解指定了Elasticsearch中该字段的类型为Long  
    @Field(type = FieldType.Long)
    @Id
    private Long id; // 唯一标识符，通常用于数据库主键或Elasticsearch文档的ID  
  
    // @Field注解指定了Elasticsearch中该字段的类型为Text，并使用了"ik_smart"分词器  
    // "ik_smart"是一个中文分词器，用于对中文文本进行分词处理  
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String subjectName; // 科目名称，可能包含中文，需要分词处理以便进行全文检索  
  
    // 与subjectName类似，subjectAnswer也是Text类型，并使用"ik_smart"分词器  
    @Field(type = FieldType.Text, analyzer = "ik_smart")  
    private String subjectAnswer; // 科目答案，同样需要分词处理  
  
    // @Field注解指定了Elasticsearch中该字段的类型为Keyword  
    // Keyword类型用于精确值搜索，不会进行分词处理  
    @Field(type = FieldType.Keyword)  
    private String createUser; // 创建用户，通常用于记录哪个用户创建了这条记录  
  
    // @Field注解指定了Elasticsearch中该字段的类型为Date，并且不索引（index=false）  
    // 不索引意味着这个字段不会被用于搜索，但可以用于排序或过滤  
    @Field(type = FieldType.Date, index = false)  
    private Date createTime; // 创建时间，通常用于记录记录的创建时间，不用于搜索
  
    // 注意：在实际应用中，还需要确保Date类型与Elasticsearch中的日期格式兼容  
    // 可以通过@DateTimeFormat注解或在配置文件中设置相应的日期格式来实现  
}