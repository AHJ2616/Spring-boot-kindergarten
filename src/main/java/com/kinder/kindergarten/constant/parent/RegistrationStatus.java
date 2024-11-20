package com.kinder.kindergarten.constant.parent;

import lombok.Getter;

@Getter
public enum RegistrationStatus {

    // 학부모 동의서 페이지 3단계에서 학부모의 정보를 입력한 후 데이터를 구분하기 위한 Enum 클래스
    // -> 학부모가 입력한 정보가 ERP 승인 대기 화면으로 전달, 직원이 검토해서 승인해서 Parent Entity로 들어가는 형식

    PENDING("대기중"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }
}
