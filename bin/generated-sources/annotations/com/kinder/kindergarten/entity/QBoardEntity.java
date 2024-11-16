package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoardEntity is a Querydsl query type for BoardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardEntity extends EntityPathBase<BoardEntity> {

    private static final long serialVersionUID = -1862576826L;

    public static final QBoardEntity boardEntity = new QBoardEntity("boardEntity");

    public final QTimeEntity _super = new QTimeEntity(this);

    public final StringPath boardContents = createString("boardContents");

    public final StringPath boardId = createString("boardId");

    public final StringPath boardTitle = createString("boardTitle");

    public final EnumPath<com.kinder.kindergarten.constant.BoardType> boardType = createEnum("boardType", com.kinder.kindergarten.constant.BoardType.class);

    public final StringPath boardWriter = createString("boardWriter");

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QBoardEntity(String variable) {
        super(BoardEntity.class, forVariable(variable));
    }

    public QBoardEntity(Path<? extends BoardEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardEntity(PathMetadata metadata) {
        super(BoardEntity.class, metadata);
    }

}

