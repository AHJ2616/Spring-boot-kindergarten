package com.kinder.kindergarten.entity.parent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParent is a Querydsl query type for Parent
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParent extends EntityPathBase<Parent> {

    private static final long serialVersionUID = -1873230261L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParent parent = new QParent("parent");

    public final DatePath<java.time.LocalDate> childrenBirthDate = createDate("childrenBirthDate", java.time.LocalDate.class);

    public final StringPath childrenEmergencyPhone = createString("childrenEmergencyPhone");

    public final StringPath childrenName = createString("childrenName");

    public final StringPath childrenNotes = createString("childrenNotes");

    public final QClassRoom classRoom;

    public final StringPath parentAddress = createString("parentAddress");

    public final DatePath<java.time.LocalDate> parentBirthDate = createDate("parentBirthDate", java.time.LocalDate.class);

    public final DatePath<java.time.LocalDate> parentCreateDate = createDate("parentCreateDate", java.time.LocalDate.class);

    public final StringPath parentEmail = createString("parentEmail");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath parentName = createString("parentName");

    public final StringPath parentPassword = createString("parentPassword");

    public final StringPath parentPhone = createString("parentPhone");

    public QParent(String variable) {
        this(Parent.class, forVariable(variable), INITS);
    }

    public QParent(Path<? extends Parent> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParent(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParent(PathMetadata metadata, PathInits inits) {
        this(Parent.class, metadata, inits);
    }

    public QParent(Class<? extends Parent> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.classRoom = inits.isInitialized("classRoom") ? new QClassRoom(forProperty("classRoom")) : null;
    }

}

