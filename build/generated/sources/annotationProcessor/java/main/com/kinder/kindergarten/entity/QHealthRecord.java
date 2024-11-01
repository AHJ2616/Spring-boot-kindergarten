package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHealthRecord is a Querydsl query type for HealthRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHealthRecord extends EntityPathBase<HealthRecord> {

    private static final long serialVersionUID = -83974512L;

    public static final QHealthRecord healthRecord = new QHealthRecord("healthRecord");

    public final QRecordDateEntity _super = new QRecordDateEntity(this);

    public final NumberPath<Children> children = createNumber("children", Children.class);

    //inherited
    public final DatePath<java.time.LocalDate> date = _super.date;

    public final NumberPath<Long> health_id = createNumber("health_id", Long.class);

    public final StringPath health_mealStatus = createString("health_mealStatus");

    public final StringPath health_sleepStatus = createString("health_sleepStatus");

    public final NumberPath<Double> health_temperature = createNumber("health_temperature", Double.class);

    //inherited
    public final DatePath<java.time.LocalDate> recodeDate = _super.recodeDate;

    public QHealthRecord(String variable) {
        super(HealthRecord.class, forVariable(variable));
    }

    public QHealthRecord(Path<? extends HealthRecord> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHealthRecord(PathMetadata metadata) {
        super(HealthRecord.class, metadata);
    }

}

