package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildren is a Querydsl query type for Children
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildren extends EntityPathBase<Children> {

    private static final long serialVersionUID = 1025620034L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildren children = new QChildren("children");

    public final QClassRoom assignedClass;

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final StringPath children_allergies = createString("children_allergies");

    public final StringPath children_emergencyPhone = createString("children_emergencyPhone");

    public final NumberPath<Long> children_id = createNumber("children_id", Long.class);

    public final StringPath children_medicalHistory = createString("children_medicalHistory");

    public final StringPath children_name = createString("children_name");

    public final StringPath children_notes = createString("children_notes");

    public final QParent parent;

    public final StringPath parent_name = createString("parent_name");

    public final StringPath parent_phone = createString("parent_phone");

    public QChildren(String variable) {
        this(Children.class, forVariable(variable), INITS);
    }

    public QChildren(Path<? extends Children> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChildren(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChildren(PathMetadata metadata, PathInits inits) {
        this(Children.class, metadata, inits);
    }

    public QChildren(Class<? extends Children> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.assignedClass = inits.isInitialized("assignedClass") ? new QClassRoom(forProperty("assignedClass")) : null;
        this.parent = inits.isInitialized("parent") ? new QParent(forProperty("parent")) : null;
    }

}

