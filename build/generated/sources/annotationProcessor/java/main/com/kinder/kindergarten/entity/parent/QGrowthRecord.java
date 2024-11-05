package com.kinder.kindergarten.entity.parent;

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

    private static final long serialVersionUID = 866896345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGrowthRecord growthRecord = new QGrowthRecord("growthRecord");

    public final QParent children;

    public final StringPath growth_activity = createString("growth_activity");

    public final DatePath<java.time.LocalDate> growth_date = createDate("growth_date", java.time.LocalDate.class);

    public final StringPath growth_notes = createString("growth_notes");

    public final NumberPath<Long> growthId = createNumber("growthId", Long.class);

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
        this.children = inits.isInitialized("children") ? new QParent(forProperty("children"), inits.get("children")) : null;
    }

}

