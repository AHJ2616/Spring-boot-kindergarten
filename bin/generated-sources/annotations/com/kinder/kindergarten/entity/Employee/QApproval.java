package com.kinder.kindergarten.entity.Employee;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApproval is a Querydsl query type for Approval
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApproval extends EntityPathBase<Approval> {

    private static final long serialVersionUID = 136116928L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApproval approval = new QApproval("approval");

    public final StringPath content = createString("content");

    public final QEmployee employee;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> requestDate = createDateTime("requestDate", java.time.LocalDateTime.class);

    public final StringPath status = createString("status");

    public final StringPath title = createString("title");

    public QApproval(String variable) {
        this(Approval.class, forVariable(variable), INITS);
    }

    public QApproval(Path<? extends Approval> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApproval(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApproval(PathMetadata metadata, PathInits inits) {
        this(Approval.class, metadata, inits);
    }

    public QApproval(Class<? extends Approval> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee")) : null;
    }

}

