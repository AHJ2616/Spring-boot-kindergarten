package com.kinder.kindergarten.DTO.material;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter @Setter
public class MaterialDTO {

    private String id; // 자재 코드

    private String material_name; // 자재 이름

    private String material_detail; // 자재 설명

    private String material_category; // 자재 분류

    private Integer material_price; // 자재 가격

    private Integer material_ea; // 자재 재고

    private String material_remark; // 비고란

    private String material_status; // 자재 상태

    private LocalDate material_regdate; // 자재 입고일

    // TimeEntity 대응
    private String regiDate;

    private String modiDate;


}
