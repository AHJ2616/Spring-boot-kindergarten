package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEmployee extends EntityPathBase<Employee> {

    private static final long serialVersionUID = 559562993L;

    public static final QEmployee employee = new QEmployee("employee");

    public final NumberPath<Double> annualLeave = createNumber("annualLeave", Double.class);

    public final ListPath<Approval, QApproval> approvals = this.<Approval, QApproval>createList("approvals", Approval.class, QApproval.class, PathInits.DIRECT2);

    public final StringPath cleanup = createString("cleanup");

    public final StringPath department = createString("department");

    public final StringPath email = createString("email");

    public final ListPath<Evaluation, QEvaluation> evaluations = this.<Evaluation, QEvaluation>createList("evaluations", Evaluation.class, QEvaluation.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> hireDate = createDate("hireDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final StringPath position = createString("position");

    public final NumberPath<Integer> positionLevel = createNumber("positionLevel", Integer.class);

    public final ListPath<Project, QProject> projects = this.<Project, QProject>createList("projects", Project.class, QProject.class, PathInits.DIRECT2);

    public final EnumPath<com.kinder.kindergarten.constant.employee.Role> role = createEnum("role", com.kinder.kindergarten.constant.employee.Role.class);

    public final NumberPath<java.math.BigDecimal> salary = createNumber("salary", java.math.BigDecimal.class);

    public final StringPath status = createString("status");

    public QEmployee(String variable) {
        super(Employee.class, forVariable(variable));
    }

    public QEmployee(Path<? extends Employee> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEmployee(PathMetadata metadata) {
        super(Employee.class, metadata);
    }

}

