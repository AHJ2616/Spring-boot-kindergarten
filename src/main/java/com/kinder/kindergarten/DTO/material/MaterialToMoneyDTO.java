package com.kinder.kindergarten.DTO.material;

import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class MaterialToMoneyDTO {

    private String moneyId;

    private LocalDate moneyUseDate; // 돈 사용 일자 (수입 지출 일어난 날짜)

    private String moneyContent; // 금액 설명

    private String moneyWho; // 돈 사용자

    private String moneyCompany; // 돈 어느 업체에 사용했는지

    private String moneyName; // 돈 내역 (월급, 식대, 등록, 환불 등)

    private Integer moneyHowMuch; // 돈 내역 (월급, 식대, 등록, 환불 등)

    private MoneyStatus moneyStatus; // 수입 지출 상태

    private String moneyApproval; // 돈 승인 여부

    private static ModelMapper modelMapper = new ModelMapper();

    public MoneyEntity createMoney() {
        return modelMapper.map(this,MoneyEntity.class);
    }

}
