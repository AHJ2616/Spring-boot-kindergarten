package com.kinder.kindergarten.DTO.money;

import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MoneyFormDTO {

    private String moneyId;

    @NotNull(message = "사용자 일자는 필수 입력값 입니다.")
    private LocalDate moneyUseDate; // 돈 사용 일자 (수입 지출 일어난 날짜)

    @NotNull(message = "설명은 필수 입력 값입니다.")
    private String moneyContent; // 금액 설명

    @NotNull(message = "사용자입력은 필수 입력 값입니다.")
    private String moneyWho; // 돈 사용자

    @NotNull(message = "지출처는 필수 입력 값입니다.")
    private String moneyCompany; // 돈 어느 업체에 사용했는지

    @NotNull(message = "내역는 필수 입력 값입니다.")
    private String moneyName; // 돈 내역 (월급, 식대, 등록, 환불 등)

    @NotNull(message = "금액는 필수 입력 값입니다.")
    private Integer moneyHowMuch; // 돈 내역 (월급, 식대, 등록, 환불 등)
    
    private MoneyStatus moneyStatus; // 수입 지출 상태

    private String moneyApproval; // 돈 승인 여부

    //저장후, 수정시 파일 정보를 저장하는 리스트
    private List<MoneyFileDTO> MoneyFileList = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public MoneyEntity createMoney(){ // Entity와 DTO 객체간 데이터 복사하여 반환
        return modelMapper.map(this,MoneyEntity.class);
    } // MoneyService/saveMoney - 연관

    public static MoneyFormDTO of(MoneyEntity moneyEntity){ // MoneyService/ getMoneyDtl - 연관
        return modelMapper.map(moneyEntity, MoneyFormDTO.class);
    }

    
}
