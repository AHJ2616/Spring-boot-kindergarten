package com.kinder.kindergarten.entity.Money;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMoneyFileEntity is a Querydsl query type for MoneyFileEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMoneyFileEntity extends EntityPathBase<MoneyFileEntity> {

    private static final long serialVersionUID = -733370706L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMoneyFileEntity moneyFileEntity = new QMoneyFileEntity("moneyFileEntity");

    public final com.kinder.kindergarten.entity.QTimeEntity _super = new com.kinder.kindergarten.entity.QTimeEntity(this);

    public final StringPath fileId = createString("fileId");

    public final StringPath filePath = createString("filePath");

    public final StringPath mainFile = createString("mainFile");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    public final StringPath modifiedName = createString("modifiedName");

    public final QMoneyEntity moneyEntity;

    public final StringPath originalName = createString("originalName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public QMoneyFileEntity(String variable) {
        this(MoneyFileEntity.class, forVariable(variable), INITS);
    }

    public QMoneyFileEntity(Path<? extends MoneyFileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMoneyFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMoneyFileEntity(PathMetadata metadata, PathInits inits) {
        this(MoneyFileEntity.class, metadata, inits);
    }

    public QMoneyFileEntity(Class<? extends MoneyFileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.moneyEntity = inits.isInitialized("moneyEntity") ? new QMoneyEntity(forProperty("moneyEntity")) : null;
    }

}

