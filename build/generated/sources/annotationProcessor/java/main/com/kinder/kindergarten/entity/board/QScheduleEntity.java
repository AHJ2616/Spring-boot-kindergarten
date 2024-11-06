package com.kinder.kindergarten.entity.board;

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

    private static final long serialVersionUID = -428524763L;

    public static final QScheduleEntity scheduleEntity = new QScheduleEntity("scheduleEntity");

    public final BooleanPath allDay = createBoolean("allDay");

    public final StringPath backgroundColor = createString("backgroundColor");

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> end = createDateTime("end", java.time.LocalDateTime.class);

    public final StringPath id = createString("id");

    public final DateTimePath<java.time.LocalDateTime> start = createDateTime("start", java.time.LocalDateTime.class);

    public final StringPath textColor = createString("textColor");

    public final StringPath title = createString("title");

    public final StringPath type = createString("type");

    public final StringPath username = createString("username");

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

