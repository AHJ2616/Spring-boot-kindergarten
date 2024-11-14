package com.kinder.kindergarten.constant.employee;

public enum ApprovalStatus {
    PENDING("대기"),
    APPROVED("승인"),
    REJECTED("반려");

    private final String status;

    ApprovalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
