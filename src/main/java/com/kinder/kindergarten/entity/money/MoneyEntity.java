package com.kinder.kindergarten.entity.money;

import com.kinder.kindergarten.DTO.money.MoneyFormDTO;
import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "money")
@Getter
@Setter
@ToString
public class MoneyEntity extends TimeEntity {

    @Id
    @Column(name = "moneyId")
    private String moneyId;

    @Column(nullable = false)
    private LocalDate moneyUseDate; // 돈 사용 일자 (수입 지출 일어난 날짜)

    @Lob
    @Column(nullable = false)
    private String moneyContent; // 금액 설명

    @Column(nullable = false)
    private String moneyWho; // 돈 사용자

    @Column(nullable = false)
    private String moneyCompany; // 돈 어느 업체에 사용했는지

    @Column(nullable = false)
    private String moneyName; // 돈 내역 (월급, 식대, 등록, 환불 등)

    @Column(nullable = false)
    private Integer moneyHowMuch; // 돈 내역 (월급, 식대, 등록, 환불 등)

    @Enumerated(EnumType.STRING) // 수입 지출
    private MoneyStatus moneyStatus; // 수입 지출 상태

    // update 회계
    public void updateMoney(MoneyFormDTO moneyFormDTO){

        this.moneyUseDate = moneyFormDTO.getMoneyUseDate();
        this.moneyContent = moneyFormDTO.getMoneyContent();
        this.moneyWho = moneyFormDTO.getMoneyWho();
        this.moneyCompany = moneyFormDTO.getMoneyCompany();
        this.moneyName = moneyFormDTO.getMoneyName();
        this.moneyHowMuch = moneyFormDTO.getMoneyHowMuch();
        this.moneyStatus = moneyFormDTO.getMoneyStatus();
        
    }

    
}
