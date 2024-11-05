package com.kinder.kindergarten.entity.parent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildrenAttendance is a Querydsl query type for ChildrenAttendance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildrenAttendance extends EntityPathBase<ChildrenAttendance> {

    private static final long serialVersionUID = -9224599L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildrenAttendance childrenAttendance = new QChildrenAttendance("childrenAttendance");

    public final DatePath<java.time.LocalDate> attendanceDate = createDate("attendanceDate", java.time.LocalDate.class);

    public final NumberPath<Long> attendanceId = createNumber("attendanceId", Long.class);

    public final StringPath attendanceStatus = createString("attendanceStatus");

    public final DateTimePath<java.time.LocalDateTime> checkInTime = createDateTime("checkInTime", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> checkOutTime = createDateTime("checkOutTime", java.time.LocalDateTime.class);

    public final QParent children;

    public QChildrenAttendance(String variable) {
        this(ChildrenAttendance.class, forVariable(variable), INITS);
    }

    public QChildrenAttendance(Path<? extends ChildrenAttendance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChildrenAttendance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChildrenAttendance(PathMetadata metadata, PathInits inits) {
        this(ChildrenAttendance.class, metadata, inits);
    }

    public QChildrenAttendance(Class<? extends ChildrenAttendance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.children = inits.isInitialized("children") ? new QParent(forProperty("children"), inits.get("children")) : null;
    }

}

