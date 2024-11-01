package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHealthRecord is a Querydsl query type for HealthRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthRecord extends EntityPathBase<HealthRecord> {

    private static final long serialVersionUID = -83974512L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthRecord healthRecord = new QHealthRecord("healthRecord");

    public final QRecordDateEntity _super = new QRecordDateEntity(this);

    public final QChildren children;

    //inherited
    public final DatePath<java.time.LocalDate> date = _super.date;

    public final NumberPath<Long> health_id = createNumber("health_id", Long.class);

    public final StringPath health_mealStatus = createString("health_mealStatus");

    public final StringPath health_sleepStatus = createString("health_sleepStatus");

    public final NumberPath<Double> health_temperature = createNumber("health_temperature", Double.class);

    //inherited
    public final DatePath<java.time.LocalDate> recodeDate = _super.recodeDate;

    public QHealthRecord(String variable) {
        this(HealthRecord.class, forVariable(variable), INITS);
    }

    public QHealthRecord(Path<? extends HealthRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHealthRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHealthRecord(PathMetadata metadata, PathInits inits) {
        this(HealthRecord.class, metadata, inits);
    }

    public QHealthRecord(Class<? extends HealthRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.children = inits.isInitialized("children") ? new QChildren(forProperty("children"), inits.get("children")) : null;
    }

}

