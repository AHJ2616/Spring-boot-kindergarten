package com.kinder.kindergarten.DTO.material;

import com.kinder.kindergarten.constant.material.MaterialStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MaterialFormDTO {

    private String id; // 자재 코드

    @NotNull(message = "자재 이름은 필수 입력 값입니다.")
    private String material_name; // 자재 이름
    
    @NotNull(message = "자재 설명은 필수 입력 값입니다.")
    private String material_detail; // 자재 설명

    @NotNull(message = "자재 분류는 필수 입력 값입니다.")
    private String material_category; // 자재 분류

    private Integer material_price; // 자재 가격

    private Integer material_ea; // 자재 재고

    private String material_remark; // 비고란

    private LocalDate material_regdate; // 자재 입고일


    private MaterialStatus materialStatus;

    private List<MaterialImgDTO> materialImgDTOList = new ArrayList<>(); // 상품 저장 후 수정할 때 상품 이미지 정보 저장

    private List<String> materialImgIds = new ArrayList<>(); // 상품의 이미지 아이디들 저장

    private static ModelMapper modelMapper = new ModelMapper();

    public MaterialEntity createMaterial(){ // Entity와 DTO 객체간 데이터 복사하여 반환
        return modelMapper.map(this,MaterialEntity.class);
    }

    public static MaterialFormDTO of(MaterialEntity materialEntity){
        return modelMapper.map(materialEntity, MaterialFormDTO.class);
    }


}
