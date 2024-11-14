package com.kinder.kindergarten.DTO.money;

import com.kinder.kindergarten.constant.money.MoneyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneySearchDTO {

    private String searchDateType;              // 현재 시간과 비교하여 상품 데이터를 조회함

    private MoneyStatus searchMoneyStatus;      // 지출상태를 기준으로 상품 데이터를 조회

    private String searchBy;                    // 어떤 유형으로 조회할 지 선택

    private String searchQuery = "";            // 조회할 검색어 저장할 변수


}
