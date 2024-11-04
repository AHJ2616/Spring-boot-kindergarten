package com.kinder.kindergarten.entity.children;


import com.kinder.kindergarten.entity.parent.Parent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity // 테이블 담당한다.
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "children")
public class Children {

    // 원아의 기본 정보를 관리하는 테이블
    // assignedClassId를 통해 반 배정 테이블과 연결

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long childrenId ; // 원아의 고유 ID

    @Column(nullable = false)
    private String childrenName; //원아 이름

    @Column(nullable = false)
    private LocalDate childrenBirthDate ; // 원아 생년월일

    @Column(nullable = false)
    private String childrenGender;  // 원아의 성별

    private String bloodType ;  // 원아의 혈액형

    @Column(nullable = false)
    private String	parentName; //학부모 이름

    private String	childrenAllergies;  //알레르기 정보

    private String	childrenMedicalHistory; //병력 정보

    @Lob
    private String	childrenNotes; // 기타 사항

    @Column(nullable = false)
    @Builder.Default
    private LocalDate childrenCreateDate = LocalDate.now();    // 생성 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classRoomId")
    private ClassRoom assignedClass;    // 원아 반

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    private Parent parent; // 부모 정보 참조
}
