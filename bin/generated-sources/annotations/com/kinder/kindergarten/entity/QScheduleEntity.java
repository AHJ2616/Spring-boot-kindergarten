package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleEntity is a Querydsl query type for ScheduleEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScheduleEntity extends EntityPathBase<ScheduleEntity> {

    private static final long serialVersionUID = -846319267L;

    public static final QScheduleEntity scheduleEntity = new QScheduleEntity("scheduleEntity");

    public final StringPath end = createString("end");

    public final StringPath location = createString("location");

    public final StringPath scheduleContent = createString("scheduleContent");

    public final StringPath scheduleId = createString("scheduleId");

    public final StringPath scheduleTitle = createString("scheduleTitle");

    public final StringPath start = createString("start");

    public QScheduleEntity(String variable) {
        super(ScheduleEntity.class, forVariable(variable));
    }

    public QScheduleEntity(Path<? extends ScheduleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleEntity(PathMetadata metadata) {
        super(ScheduleEntity.class, metadata);
    }

}

