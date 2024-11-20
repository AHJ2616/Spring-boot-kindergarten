package com.kinder.kindergarten.DTO.material;

import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@Builder
public class MaterialDTO {

    private String materialId; // 자재 코드

    private String materialName; // 자재 이름

    private String materialDetail; // 자재 설명

    private String materialCategory; // 자재 분류

    private Integer materialPrice; // 자재 가격

    private Integer materialEa; // 자재 재고

    private String materialRemark; // 비고란

    private String materialStatus; // 자재 상태

    private LocalDate materialRegdate; // 자재 입고일

    private String materialWriter; // 글작성자

    private String materialWriterEmail; // 글작성자 이메일

    // TimeEntity 대응
    private String regiDate;

    private String modiDate;


}
