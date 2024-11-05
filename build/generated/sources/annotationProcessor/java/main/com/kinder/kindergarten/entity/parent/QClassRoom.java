package com.kinder.kindergarten.entity.parent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QClassRoom is a Querydsl query type for ClassRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassRoom extends EntityPathBase<ClassRoom> {

    private static final long serialVersionUID = 1322349874L;

    public static final QClassRoom classRoom = new QClassRoom("classRoom");

    public final StringPath classRoomDescription = createString("classRoomDescription");

    public final NumberPath<Long> classRoomId = createNumber("classRoomId", Long.class);

    public final StringPath classRoomName = createString("classRoomName");

    public final StringPath employeeName = createString("employeeName");

    public final NumberPath<Integer> maxChildren = createNumber("maxChildren", Integer.class);

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

