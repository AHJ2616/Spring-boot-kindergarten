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

    public final com.kinder.kindergarten.entity.children.QChildrenBaseEntity _super = new com.kinder.kindergarten.entity.children.QChildrenBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> approvedAt = createDateTime("approvedAt", java.time.LocalDateTime.class);

    public final StringPath approvedBy = createString("approvedBy");

    public final ListPath<com.kinder.kindergarten.entity.children.Children, com.kinder.kindergarten.entity.children.QChildren> children = this.<com.kinder.kindergarten.entity.children.Children, com.kinder.kindergarten.entity.children.QChildren>createList("children", com.kinder.kindergarten.entity.children.Children.class, com.kinder.kindergarten.entity.children.QChildren.class, PathInits.DIRECT2);

    public final StringPath childrenEmergencyPhone = createString("childrenEmergencyPhone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath detailAddress = createString("detailAddress");

    public final BooleanPath isErpRegistered = createBoolean("isErpRegistered");

    public final StringPath parentAddress = createString("parentAddress");

    public final QParentConsent parentConsent;

    public final StringPath parentEmail = createString("parentEmail");

    public final NumberPath<Long> parentId = createNumber("parentId", Long.class);

    public final StringPath parentName = createString("parentName");

    public final StringPath parentPassword = createString("parentPassword");

    public final StringPath parentPhone = createString("parentPhone");

    public final EnumPath<com.kinder.kindergarten.constant.parent.ParentType> parentType = createEnum("parentType", com.kinder.kindergarten.constant.parent.ParentType.class);

    public final EnumPath<com.kinder.kindergarten.constant.parent.RegistrationStatus> registrationStatus = createEnum("registrationStatus", com.kinder.kindergarten.constant.parent.RegistrationStatus.class);

    public final StringPath rejectReason = createString("rejectReason");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedDate = _super.updatedDate;

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
        this.parentConsent = inits.isInitialized("parentConsent") ? new QParentConsent(forProperty("parentConsent"), inits.get("parentConsent")) : null;
    }

}

