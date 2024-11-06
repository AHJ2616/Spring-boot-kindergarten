package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.DTO.material.MaterialSearchDTO;
import com.kinder.kindergarten.constant.material.MaterialStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.material.QMaterialEntity;
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

public class MaterialRepositoryCustomImpl implements MaterialRepositoryCustom{

    private JPAQueryFactory queryFactory; // 동적쿼리 생성

    public MaterialRepositoryCustomImpl(EntityManager em){ // JPAQueryFactory의 생성자로 EntityManager 객체 넣어줌
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchMaterialStatusEq(MaterialStatus searchMaterialStatus){ // 자재 조건에 따른 검색
        return searchMaterialStatus ==
                null ? null : QMaterialEntity.materialEntity.materialStatus.eq(searchMaterialStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){ // 날짜 조건 검색

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

        return QMaterialEntity.materialEntity.regiDate.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){ // 특정 조건 검색 "like" 이용

        if(StringUtils.equals("material_name", searchBy)){     // 이름
            return QMaterialEntity.materialEntity.material_name.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("id", searchBy)){       // id
            return QMaterialEntity.materialEntity.id.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<MaterialEntity> getMaterialPage(MaterialSearchDTO materialSearchDTO, Pageable pageable) {
        List<MaterialEntity> content = queryFactory
                .selectFrom(QMaterialEntity.materialEntity) // 자재 데이터 지정
                .where(regDtsAfter(materialSearchDTO.getSearchDateType()),  //조건 절 ',' 가 and 조건
                        searchMaterialStatusEq(materialSearchDTO.getSearchMaterialStatus()),
                        searchByLike(materialSearchDTO.getSearchBy(),
                                materialSearchDTO.getSearchQuery()))
                .orderBy(QMaterialEntity.materialEntity.id.desc())
                .offset(pageable.getOffset())   // 한번에 가지고올 시작 인덱스
                .limit(pageable.getPageSize())  // 한번에 가지고올 최대 개수
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QMaterialEntity.materialEntity)
                .where(regDtsAfter(materialSearchDTO.getSearchDateType()),
                        searchMaterialStatusEq(materialSearchDTO.getSearchMaterialStatus()),
                        searchByLike(materialSearchDTO.getSearchBy(), materialSearchDTO.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);            // 페이징 처리되어 간다.
    }




}
