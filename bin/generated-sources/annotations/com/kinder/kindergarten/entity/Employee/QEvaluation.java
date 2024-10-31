package com.kinder.kindergarten.entity.Employee;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEvaluation is a Querydsl query type for Evaluation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvaluation extends EntityPathBase<Evaluation> {

    private static final long serialVersionUID = 1964011641L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEvaluation evaluation = new QEvaluation("evaluation");

    public final StringPath content = createString("content");

    public final QEmployee employee;

    public final DatePath<java.time.LocalDate> evaluationDate = createDate("evaluationDate", java.time.LocalDate.class);

    public final StringPath evaluator = createString("evaluator");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public QEvaluation(String variable) {
        this(Evaluation.class, forVariable(variable), INITS);
    }

    public QEvaluation(Path<? extends Evaluation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEvaluation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEvaluation(PathMetadata metadata, PathInits inits) {
        this(Evaluation.class, metadata, inits);
    }

    public QEvaluation(Class<? extends Evaluation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee")) : null;
    }

}

