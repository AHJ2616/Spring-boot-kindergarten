package com.kinder.kindergarten.entity.parent;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClassChangeHistory is a Querydsl query type for ClassChangeHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassChangeHistory extends EntityPathBase<ClassChangeHistory> {

    private static final long serialVersionUID = -244260787L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClassChangeHistory classChangeHistory = new QClassChangeHistory("classChangeHistory");

    public final DatePath<java.time.LocalDate> changeDate = createDate("changeDate", java.time.LocalDate.class);

    public final QParent children;

    public final NumberPath<Long> historyId = createNumber("historyId", Long.class);

    public final QClassRoom newClass;

    public final QClassRoom previousClass;

    public QClassChangeHistory(String variable) {
        this(ClassChangeHistory.class, forVariable(variable), INITS);
    }

    public QClassChangeHistory(Path<? extends ClassChangeHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClassChangeHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClassChangeHistory(PathMetadata metadata, PathInits inits) {
        this(ClassChangeHistory.class, metadata, inits);
    }

    public QClassChangeHistory(Class<? extends ClassChangeHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.children = inits.isInitialized("children") ? new QParent(forProperty("children"), inits.get("children")) : null;
        this.newClass = inits.isInitialized("newClass") ? new QClassRoom(forProperty("newClass")) : null;
        this.previousClass = inits.isInitialized("previousClass") ? new QClassRoom(forProperty("previousClass")) : null;
    }

}

