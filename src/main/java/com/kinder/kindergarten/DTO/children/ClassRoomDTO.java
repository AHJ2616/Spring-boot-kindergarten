package com.kinder.kindergarten.DTO.children;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClassRoomDTO {

    // 반 정보를 전달하기 위한 DTO, 반 정보 처리

    private Long classRoomId;   // ClassRoom ID

    @NotBlank(message = "개설할 반 이름을 입력해 주세요")
    private String classRoomName; // 반 이름

    @NotNull(message = "해당 반의 정원 수를 입력해 주세요")
    @Positive(message = "정원은 양수이여야 합니다.")
    private int maxChildren;    // 반의 정원

    private String classRoomDescription;  // 반 설명

    @NotBlank(message = "배정된 담당 교사를 입력해 주세요")
    private String employeeName;  // 담임 교사 이름

    private Integer currentStudents = 0;  // 현재 등록된 원아 수 (기본값 0)

}
