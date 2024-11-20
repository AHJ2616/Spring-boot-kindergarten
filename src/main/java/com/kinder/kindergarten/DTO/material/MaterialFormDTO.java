package com.kinder.kindergarten.DTO.material;

import com.kinder.kindergarten.DTO.money.MoneyFileDTO;
import com.kinder.kindergarten.constant.material.MaterialStatus;
import com.kinder.kindergarten.entity.material.MaterialEntity;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class MaterialFormDTO {

    private String materialId; // 자재 코드

    @NotNull(message = "자재 이름은 필수 입력 값입니다.")
    private String materialName; // 자재 이름
    
    @NotNull(message = "자재 설명은 필수 입력 값입니다.")
    private String materialDetail; // 자재 설명

    @NotNull(message = "자재 분류는 필수 입력 값입니다.")
    private String materialCategory; // 자재 분류

    private Integer materialPrice; // 자재 가격

    private Integer materialEa; // 자재 재고

    private String materialRemark; // 비고란

    private LocalDate materialRegdate; // 자재 입고일
    
    private String materialWriter; // 글작성자

    private String materialWriterEmail; // 글작성자 이메일

    private MaterialStatus materialStatus;

    private List<MaterialFileDTO> materialImgDTOList = new ArrayList<>(); // 상품 저장 후 수정할 때 상품 이미지 정보 저장

    //저장후, 수정시 파일 정보를 저장하는 리스트
    private List<MaterialFileDTO> MaterialFileList = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public MaterialEntity createMaterial(){ // Entity와 DTO 객체간 데이터 복사하여 반환
        return modelMapper.map(this,MaterialEntity.class);
    }

    public static MaterialFormDTO of(MaterialEntity materialEntity){
        return modelMapper.map(materialEntity, MaterialFormDTO.class);
    }


}
