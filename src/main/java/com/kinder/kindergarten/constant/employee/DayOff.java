package com.kinder.kindergarten.constant.employee;

public enum DayOff {
    반차(0.5), // 반차: 0.5일
    연차(1.0); // 연차: 1일

    private final double days;

    DayOff(double days) {
        this.days = days;
    }

    public double getDays() {
        return days;
    }
}
