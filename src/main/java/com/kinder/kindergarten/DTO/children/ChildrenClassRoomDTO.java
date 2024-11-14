package com.kinder.kindergarten.DTO.children;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChildrenClassRoomDTO {

    // ERP에서 원아가 반 배정하기 위한 DTO

    private Long classRoomId;   // ClassRoom ID

    private String classRoomName;  // 반 이름

    private Long childrenId ; // 원아의 고유 ID

    private String childrenName; //원아 이름

    private LocalDateTime assignDate ;  // 반 배정 일자

    public ChildrenClassRoomDTO(Long childrenId, Long classRoomId) {
        // 반 배정을 위한 생성자

        this.childrenId = childrenId;
        this.classRoomId = classRoomId;
        this.assignDate = LocalDateTime.now();
    }


}
