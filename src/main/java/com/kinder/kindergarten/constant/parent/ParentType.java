package com.kinder.kindergarten.constant.parent;

public enum ParentType {

    // 학부모 테이블의 자녀와의 관계 대한 Eunm

    FATHER("아버지"),
    MOTHER("어머니"),
    GRANDFATHER("할아버지"),
    GRANDMOTHER("할머니"),
    UNCLE("삼촌/외삼촌"),
    AUNT("이모/고모"),
    GUARDIAN("기타보호자");  // 법적 보호자 등

    private final String displayName;

    ParentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
