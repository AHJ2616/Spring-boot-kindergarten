package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProjectMember is a Querydsl query type for ProjectMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProjectMember extends EntityPathBase<ProjectMember> {

    private static final long serialVersionUID = -1623924048L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectMember projectMember = new QProjectMember("projectMember");

    public final QEmployee employee;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final DateTimePath<java.time.LocalDateTime> joinDate = createDateTime("joinDate", java.time.LocalDateTime.class);

    public final QProject project;

    public final StringPath role = createString("role");

    public QProjectMember(String variable) {
        this(ProjectMember.class, forVariable(variable), INITS);
    }

    public QProjectMember(Path<? extends ProjectMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProjectMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProjectMember(PathMetadata metadata, PathInits inits) {
        this(ProjectMember.class, metadata, inits);
    }

    public QProjectMember(Class<? extends ProjectMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

