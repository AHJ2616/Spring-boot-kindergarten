package com.kinder.kindergarten.repository.money;

import com.kinder.kindergarten.DTO.money.MoneySearchDTO;
import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import com.kinder.kindergarten.entity.money.QMoneyEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class MoneyRepositoryCustomImpl implements MoneyRepositoryCustom{

    private JPAQueryFactory queryFactory; // 동적 쿼리생성

    public MoneyRepositoryCustomImpl(EntityManager em){ this.queryFactory = new JPAQueryFactory(em); }

    // 돈 조건에 따른 검색
    private BooleanExpression searchMoneyStatusEq(MoneyStatus searchMoneyStatus){
        return searchMoneyStatus ==
                null ? null : QMoneyEntity.moneyEntity.moneyStatus.eq(searchMoneyStatus);
    }

    // 날짜 조건 검색
    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            // 등록일 전체
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){    // 최근 하루 동안 등록된 상품
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){    // 최근 일주일
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){    // 최근 한달
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){    // 최근 6개월
            dateTime = dateTime.minusMonths(6);
        }

        return QMoneyEntity.moneyEntity.regiDate.after(dateTime);
    }

    // 특정 조건 검색 'like' 이용
    private BooleanExpression searchByLike(String searchBy, String searchQuery){ // 특정 조건 검색 "like" 이용

        if(StringUtils.equals("moneyName", searchBy)){     // 이름 검색
            return QMoneyEntity.moneyEntity.moneyName.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("moneyId", searchBy)){       //  id 검색
            return QMoneyEntity.moneyEntity.moneyId.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<MoneyEntity> getMoneyPage(MoneySearchDTO moneySearchDTO, Pageable pageable) {
        List<MoneyEntity> content = queryFactory
                .selectFrom(QMoneyEntity.moneyEntity) // 자재 데이터 지정
                .where(regDtsAfter(moneySearchDTO.getSearchDateType()),  //조건 절 ',' 가 and 조건
                        searchMoneyStatusEq(moneySearchDTO.getSearchMoneyStatus()),
                        searchByLike(moneySearchDTO.getSearchBy(),
                                moneySearchDTO.getSearchQuery()))
                .orderBy(QMoneyEntity.moneyEntity.moneyId.desc())
                .offset(pageable.getOffset())   // 한번에 가지고올 시작 인덱스
                .limit(pageable.getPageSize())  // 한번에 가지고올 최대 개수
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QMoneyEntity.moneyEntity)
                .where(regDtsAfter(moneySearchDTO.getSearchDateType()),
                        searchMoneyStatusEq(moneySearchDTO.getSearchMoneyStatus()),
                        searchByLike(moneySearchDTO.getSearchBy(), moneySearchDTO.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);            // 페이징 처리되어 간다.
    }


}
