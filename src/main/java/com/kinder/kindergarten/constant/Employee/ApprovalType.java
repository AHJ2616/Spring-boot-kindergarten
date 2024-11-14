package com.kinder.kindergarten.constant.employee;

public enum ApprovalType {
    LEAVE("휴가"),
    EXPENSE("지출"),
    GENERAL("일반");

    private final String type;

    ApprovalType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
