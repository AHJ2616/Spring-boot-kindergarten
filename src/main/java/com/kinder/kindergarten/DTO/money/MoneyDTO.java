package com.kinder.kindergarten.DTO.money;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MoneyDTO {

    private String moneyId;

    private LocalDate moneyUseDate; // 돈 사용 일자 (수입 지출 일어난 날짜)

    private String moneyContent; // 금액 설명

    private String moneyWho; // 돈 사용자

    private String moneyCompany; // 돈 어느 업체에 사용했는지

    private String moneyName; // 돈 내역 (월급, 식대, 등록, 환불 등)

    private Integer moneyHowMuch; // 돈 내역 (월급, 식대, 등록, 환불 등)

    private String moneyStatus; // 수입 지출 상태

    private String moneyApproval; // 돈 승인 여부

    // TimeEntity 대응
    private String regiDate;

    private String modiDate;

    private List<MoneyFileDTO> MoneyFileList = new ArrayList<>();


}
