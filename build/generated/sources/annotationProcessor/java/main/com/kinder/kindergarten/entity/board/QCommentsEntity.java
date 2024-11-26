package com.kinder.kindergarten.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommentsEntity is a Querydsl query type for CommentsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentsEntity extends EntityPathBase<CommentsEntity> {

    private static final long serialVersionUID = -832189246L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentsEntity commentsEntity = new QCommentsEntity("commentsEntity");

    public final com.kinder.kindergarten.entity.QTimeEntity _super = new com.kinder.kindergarten.entity.QTimeEntity(this);

    public final QBoardEntity boardId;

    public final StringPath commentsId = createString("commentsId");

    public final StringPath contents = createString("contents");

    public final com.kinder.kindergarten.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public QCommentsEntity(String variable) {
        this(CommentsEntity.class, forVariable(variable), INITS);
    }

    public QCommentsEntity(Path<? extends CommentsEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommentsEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommentsEntity(PathMetadata metadata, PathInits inits) {
        this(CommentsEntity.class, metadata, inits);
    }

    public QCommentsEntity(Class<? extends CommentsEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.boardId = inits.isInitialized("boardId") ? new QBoardEntity(forProperty("boardId"), inits.get("boardId")) : null;
        this.member = inits.isInitialized("member") ? new com.kinder.kindergarten.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

