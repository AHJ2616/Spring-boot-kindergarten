package com.kinder.kindergarten.DTO.children;

import com.kinder.kindergarten.entity.children.Children;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassRoomDTO {

    // 반 정보를 전달하기 위한 DTO, 반 정보 처리(반 개설용)

    private Long classRoomId;   // ClassRoom ID

    @NotBlank(message = "개설할 반 이름을 입력해 주세요")
    private String classRoomName; // 반 이름

    @NotNull(message = "해당 반의 정원 수를 입력해 주세요")
    @Positive(message = "정원은 양수이여야 합니다.")
    private int maxChildren;    // 반의 정원

    private String classRoomDescription;  // 반 설명

    @NotBlank(message = "배정된 담당 교사를 입력해 주세요")
    private String employeeName;  // 담임 교사 이름

    @Builder.Default
    private Integer currentStudents = 0;  // 현재 등록된 원아 수 (기본값 0)

    @Builder.Default
    @OneToMany(mappedBy = "assignedClass")
    private List<Children> children = new ArrayList<>();

    // 모든 필드를 포함하는 생성자

  /*  public ClassRoomDTO(Long classRoomId, String classRoomName, int maxChildren,
                        String classRoomDescription, String employeeName, Integer currentStudents) {
        this.classRoomId = classRoomId;
        this.classRoomName = classRoomName;
        this.maxChildren = maxChildren;
        this.classRoomDescription = classRoomDescription;
        this.employeeName = employeeName;
        this.currentStudents = currentStudents != null ? currentStudents : 0;

    }

   */
}
