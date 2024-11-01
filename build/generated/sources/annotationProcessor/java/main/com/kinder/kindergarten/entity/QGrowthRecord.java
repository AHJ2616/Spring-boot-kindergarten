package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGrowthRecord is a Querydsl query type for GrowthRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrowthRecord extends EntityPathBase<GrowthRecord> {

    private static final long serialVersionUID = 1038027195L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGrowthRecord growthRecord = new QGrowthRecord("growthRecord");

    public final QRecordDateEntity _super = new QRecordDateEntity(this);

    public final QChildren children;

    //inherited
    public final DatePath<java.time.LocalDate> date = _super.date;

    public final StringPath growth_activity = createString("growth_activity");

    public final NumberPath<Long> growth_id = createNumber("growth_id", Long.class);

    public final StringPath growth_notes = createString("growth_notes");

    //inherited
    public final DatePath<java.time.LocalDate> recodeDate = _super.recodeDate;

    public QGrowthRecord(String variable) {
        this(GrowthRecord.class, forVariable(variable), INITS);
    }

    public QGrowthRecord(Path<? extends GrowthRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGrowthRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGrowthRecord(PathMetadata metadata, PathInits inits) {
        this(GrowthRecord.class, metadata, inits);
    }

    public QGrowthRecord(Class<? extends GrowthRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.children = inits.isInitialized("children") ? new QChildren(forProperty("children"), inits.get("children")) : null;
    }

}

