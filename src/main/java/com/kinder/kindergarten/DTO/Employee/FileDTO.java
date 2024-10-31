package com.kinder.kindergarten.DTO.Employee;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private Long fi_id;         // 파일 기본키
    private String fi_name;     // 파일 이름
    private String fi_original; // 파일 원본이름
    private String fi_path;     // 파일 경로
}
