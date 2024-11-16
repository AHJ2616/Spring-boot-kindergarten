package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLeave is a Querydsl query type for Leave
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLeave extends EntityPathBase<Leave> {

    private static final long serialVersionUID = -963456172L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLeave leave = new QLeave("leave");

    public final QEmployee employee;

    public final DatePath<java.time.LocalDate> end = createDate("end", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> start = createDate("start", java.time.LocalDate.class);

    public final StringPath status = createString("status");

    public final NumberPath<Double> total = createNumber("total", Double.class);

    public final EnumPath<com.kinder.kindergarten.constant.employee.DayOff> type = createEnum("type", com.kinder.kindergarten.constant.employee.DayOff.class);

    public QLeave(String variable) {
        this(Leave.class, forVariable(variable), INITS);
    }

    public QLeave(Path<? extends Leave> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLeave(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLeave(PathMetadata metadata, PathInits inits) {
        this(Leave.class, metadata, inits);
    }

    public QLeave(Class<? extends Leave> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee")) : null;
    }

}

