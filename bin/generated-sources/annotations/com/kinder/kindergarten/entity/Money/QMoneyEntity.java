package com.kinder.kindergarten.entity.Money;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMoneyEntity is a Querydsl query type for MoneyEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMoneyEntity extends EntityPathBase<MoneyEntity> {

    private static final long serialVersionUID = 1418740114L;

    public static final QMoneyEntity moneyEntity = new QMoneyEntity("moneyEntity");

    public final com.kinder.kindergarten.entity.QTimeEntity _super = new com.kinder.kindergarten.entity.QTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modiDate = _super.modiDate;

    public final StringPath moneyCompany = createString("moneyCompany");

    public final StringPath moneyContent = createString("moneyContent");

    public final NumberPath<Integer> moneyHowMuch = createNumber("moneyHowMuch", Integer.class);

    public final StringPath moneyId = createString("moneyId");

    public final StringPath moneyName = createString("moneyName");

    public final EnumPath<com.kinder.kindergarten.constant.money.MoneyStatus> moneyStatus = createEnum("moneyStatus", com.kinder.kindergarten.constant.money.MoneyStatus.class);

    public final DatePath<java.time.LocalDate> moneyUseDate = createDate("moneyUseDate", java.time.LocalDate.class);

    public final StringPath moneyWho = createString("moneyWho");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regiDate = _super.regiDate;

    public QMoneyEntity(String variable) {
        super(MoneyEntity.class, forVariable(variable));
    }

    public QMoneyEntity(Path<? extends MoneyEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMoneyEntity(PathMetadata metadata) {
        super(MoneyEntity.class, metadata);
    }

}

