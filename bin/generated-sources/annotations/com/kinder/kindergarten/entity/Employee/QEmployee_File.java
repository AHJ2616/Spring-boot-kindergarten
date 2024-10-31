package com.kinder.kindergarten.entity.Employee;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployee_File is a Querydsl query type for Employee_File
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployee_File extends EntityPathBase<Employee_File> {

    private static final long serialVersionUID = 2054613552L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmployee_File employee_File = new QEmployee_File("employee_File");

    public final QEmployee employee;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath original = createString("original");

    public final StringPath path = createString("path");

    public QEmployee_File(String variable) {
        this(Employee_File.class, forVariable(variable), INITS);
    }

    public QEmployee_File(Path<? extends Employee_File> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmployee_File(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmployee_File(PathMetadata metadata, PathInits inits) {
        this(Employee_File.class, metadata, inits);
    }

    public QEmployee_File(Class<? extends Employee_File> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new QEmployee(forProperty("employee")) : null;
    }

}

