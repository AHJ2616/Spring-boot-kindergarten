package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTimeEntity is a Querydsl query type for TimeEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QTimeEntity extends EntityPathBase<TimeEntity> {

    private static final long serialVersionUID = 1088088723L;

    public static final QTimeEntity timeEntity = new QTimeEntity("timeEntity");

    public final DateTimePath<java.time.LocalDateTime> modiDate = createDateTime("modiDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> regiDate = createDateTime("regiDate", java.time.LocalDateTime.class);

    public QTimeEntity(String variable) {
        super(TimeEntity.class, forVariable(variable));
    }

    public QTimeEntity(Path<? extends TimeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTimeEntity(PathMetadata metadata) {
        super(TimeEntity.class, metadata);
    }

}

