package com.kinder.kindergarten.entity.parent;

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

    private static final long serialVersionUID = -255105362L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHealthRecord healthRecord = new QHealthRecord("healthRecord");

    public final QParent children;

    public final StringPath healthAllergies = createString("healthAllergies");

    public final DatePath<java.time.LocalDate> healthDate = createDate("healthDate", java.time.LocalDate.class);

    public final NumberPath<Double> healthHeight = createNumber("healthHeight", Double.class);

    public final StringPath healthMealStatus = createString("healthMealStatus");

    public final StringPath healthMedicalHistory = createString("healthMedicalHistory");

    public final StringPath healthNotes = createString("healthNotes");

    public final NumberPath<Long> healthRecordId = createNumber("healthRecordId", Long.class);

    public final StringPath healthSleepStatus = createString("healthSleepStatus");

    public final StringPath healthStatus = createString("healthStatus");

    public final NumberPath<Double> healthTemperature = createNumber("healthTemperature", Double.class);

    public final NumberPath<Double> healthWeight = createNumber("healthWeight", Double.class);

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
        this.children = inits.isInitialized("children") ? new QParent(forProperty("children"), inits.get("children")) : null;
    }

}

