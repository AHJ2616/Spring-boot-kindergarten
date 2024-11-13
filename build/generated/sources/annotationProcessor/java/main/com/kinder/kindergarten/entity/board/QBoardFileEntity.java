package com.kinder.kindergarten.entity.board;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardFileEntity is a Querydsl query type for BoardFileEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardFileEntity extends EntityPathBase<BoardFileEntity> {

    private static final long serialVersionUID = -53088358L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardFileEntity boardFileEntity = new QBoardFileEntity("boardFileEntity");

    public final com.kinder.kindergarten.entity.QTimeEntity _super = new com.kinder.kindergarten.entity.QTimeEntity(this);

    public final QBoardEntity boardEntity;

    public final StringPath fileId = createString("fileId");

    public final StringPath filePath = createString("filePath");

    public final StringPath isZip = createString("isZip");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    public final StringPath modifiedName = createString("modifiedName");

    public final StringPath originalName = createString("originalName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public QBoardFileEntity(String variable) {
        this(BoardFileEntity.class, forVariable(variable), INITS);
    }

    public QBoardFileEntity(Path<? extends BoardFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardFileEntity(PathMetadata metadata, PathInits inits) {
        this(BoardFileEntity.class, metadata, inits);
    }

    public QBoardFileEntity(Class<? extends BoardFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.boardEntity = inits.isInitialized("boardEntity") ? new QBoardEntity(forProperty("boardEntity"), inits.get("boardEntity")) : null;
    }

}

