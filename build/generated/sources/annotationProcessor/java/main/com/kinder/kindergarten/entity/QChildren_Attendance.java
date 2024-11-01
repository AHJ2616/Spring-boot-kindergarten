package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildren_Attendance is a Querydsl query type for Children_Attendance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildren_Attendance extends EntityPathBase<Children_Attendance> {

    private static final long serialVersionUID = -944767194L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildren_Attendance children_Attendance = new QChildren_Attendance("children_Attendance");

    public final QRecordDateEntity _super = new QRecordDateEntity(this);

    public final NumberPath<Long> cdAttend_id = createNumber("cdAttend_id", Long.class);

    public final StringPath cdAttend_status = createString("cdAttend_status");

    public final QChildren children;

    //inherited
    public final DatePath<java.time.LocalDate> date = _super.date;

    //inherited
    public final DatePath<java.time.LocalDate> recodeDate = _super.recodeDate;

    public QChildren_Attendance(String variable) {
        this(Children_Attendance.class, forVariable(variable), INITS);
    }

    public QChildren_Attendance(Path<? extends Children_Attendance> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChildren_Attendance(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChildren_Attendance(PathMetadata metadata, PathInits inits) {
        this(Children_Attendance.class, metadata, inits);
    }

    public QChildren_Attendance(Class<? extends Children_Attendance> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.children = inits.isInitialized("children") ? new QChildren(forProperty("children"), inits.get("children")) : null;
    }

}

