package com.kinder.kindergarten.constant.material;

public enum MaterialOrderStatus {
    ORDERED("주문완료"),
    CANCELED("주문취소"),
    COMPLETED("입고완료");

    private final String displayName;

    MaterialOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
