package com.kinder.kindergarten.constant.parent;

import lombok.Getter;

@Getter
public enum RegistrationStatus {

    // 학부모 동의서 페이지 3단계에서 학부모의 정보를 입력한 후 데이터를 구분하기 위한 Enum 클래스
    // -> 학부모가 입력한 정보가 ERP 승인 대기 화면으로 전달, 직원이 검토해서 승인해서 Parent Entity로 들어가는 형식

    PENDING("승인대기"),      // 학부모가 정보 입력 후 승인 대기 상태
    APPROVED("승인완료"),     // 직원이 승인한 상태
    REJECTED("반려"),        // 직원이 반려한 상태
    CANCELED("취소");        // 학부모가 취소한 상태

    private final String description;

    RegistrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
