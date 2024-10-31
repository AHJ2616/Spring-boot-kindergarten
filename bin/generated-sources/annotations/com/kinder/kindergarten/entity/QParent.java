package com.kinder.kindergarten.entity;

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

    private static final long serialVersionUID = 308942765L;

    public static final QParent parent = new QParent("parent");

    public final ListPath<Children, QChildren> children = this.<Children, QChildren>createList("children", Children.class, QChildren.class, PathInits.DIRECT2);

    public final EnumPath<com.kinder.kindergarten.constant.Children_Role> children_role = createEnum("children_role", com.kinder.kindergarten.constant.Children_Role.class);

    public final StringPath parent_address = createString("parent_address");

    public final StringPath parent_birthDate = createString("parent_birthDate");

    public final DatePath<java.time.LocalDate> parent_createDate = createDate("parent_createDate", java.time.LocalDate.class);

    public final StringPath parent_email = createString("parent_email");

    public final NumberPath<Long> parent_id = createNumber("parent_id", Long.class);

    public final StringPath parent_name = createString("parent_name");

    public final StringPath parent_phone = createString("parent_phone");

    public final StringPath parent_pw = createString("parent_pw");

    public QParent(String variable) {
        super(Parent.class, forVariable(variable));
    }

    public QParent(Path<? extends Parent> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParent(PathMetadata metadata) {
        super(Parent.class, metadata);
    }

}

