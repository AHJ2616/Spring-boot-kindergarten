package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecordDateEntity is a Querydsl query type for RecordDateEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QRecordDateEntity extends EntityPathBase<RecordDateEntity> {

    private static final long serialVersionUID = 1325597605L;

    public static final QRecordDateEntity recordDateEntity = new QRecordDateEntity("recordDateEntity");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> recodeDate = createDate("recodeDate", java.time.LocalDate.class);

    public QRecordDateEntity(String variable) {
        super(RecordDateEntity.class, forVariable(variable));
    }

    public QRecordDateEntity(Path<? extends RecordDateEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecordDateEntity(PathMetadata metadata) {
        super(RecordDateEntity.class, metadata);
    }

}

