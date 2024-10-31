package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClassRoom is a Querydsl query type for ClassRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassRoom extends EntityPathBase<ClassRoom> {

    private static final long serialVersionUID = 1813975184L;

    public static final QClassRoom classRoom = new QClassRoom("classRoom");

    public final ListPath<Children, QChildren> children = this.<Children, QChildren>createList("children", Children.class, QChildren.class, PathInits.DIRECT2);

    public final StringPath class_Room_description = createString("class_Room_description");

    public final NumberPath<Long> class_Room_id = createNumber("class_Room_id", Long.class);

    public final StringPath class_Room_name = createString("class_Room_name");

    public final StringPath employee_name = createString("employee_name");

    public QClassRoom(String variable) {
        super(ClassRoom.class, forVariable(variable));
    }

    public QClassRoom(Path<? extends ClassRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QClassRoom(PathMetadata metadata) {
        super(ClassRoom.class, metadata);
    }

}

