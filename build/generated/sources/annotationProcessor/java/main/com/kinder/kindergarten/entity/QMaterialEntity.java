package com.kinder.kindergarten.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMaterialEntity is a Querydsl query type for MaterialEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMaterialEntity extends EntityPathBase<MaterialEntity> {

    private static final long serialVersionUID = 1712773101L;

    public static final QMaterialEntity materialEntity = new QMaterialEntity("materialEntity");

    public final QTimeEntity _super = new QTimeEntity(this);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath material_category = createString("material_category");

    public final StringPath material_detail = createString("material_detail");

    public final NumberPath<Integer> material_ea = createNumber("material_ea", Integer.class);

    public final StringPath material_name = createString("material_name");

    public final NumberPath<Integer> material_price = createNumber("material_price", Integer.class);

    public final DatePath<java.time.LocalDate> material_regdate = createDate("material_regdate", java.time.LocalDate.class);

    public final StringPath material_remark = createString("material_remark");

    public final EnumPath<com.kinder.kindergarten.constant.MaterialStatus> materialStatus = createEnum("materialStatus", com.kinder.kindergarten.constant.MaterialStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public QMaterialEntity(String variable) {
        super(MaterialEntity.class, forVariable(variable));
    }

    public QMaterialEntity(Path<? extends MaterialEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMaterialEntity(PathMetadata metadata) {
        super(MaterialEntity.class, metadata);
    }

}

