package com.kinder.kindergarten.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardEntity is a Querydsl query type for BoardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardEntity extends EntityPathBase<BoardEntity> {

    private static final long serialVersionUID = 1277026430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardEntity boardEntity = new QBoardEntity("boardEntity");

    public final com.kinder.kindergarten.entity.QTimeEntity _super = new com.kinder.kindergarten.entity.QTimeEntity(this);

    public final StringPath boardContents = createString("boardContents");

    public final ListPath<BoardFileEntity, QBoardFileEntity> boardFiles = this.<BoardFileEntity, QBoardFileEntity>createList("boardFiles", BoardFileEntity.class, QBoardFileEntity.class, PathInits.DIRECT2);

    public final StringPath boardId = createString("boardId");

    public final StringPath boardTitle = createString("boardTitle");

    public final EnumPath<com.kinder.kindergarten.constant.board.BoardType> boardType = createEnum("boardType", com.kinder.kindergarten.constant.board.BoardType.class);

    public final com.kinder.kindergarten.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QBoardEntity(String variable) {
        this(BoardEntity.class, forVariable(variable), INITS);
    }

    public QBoardEntity(Path<? extends BoardEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardEntity(PathMetadata metadata, PathInits inits) {
        this(BoardEntity.class, metadata, inits);
    }

    public QBoardEntity(Class<? extends BoardEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.kinder.kindergarten.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

