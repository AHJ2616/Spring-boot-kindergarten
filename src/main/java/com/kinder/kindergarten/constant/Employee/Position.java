package com.kinder.kindergarten.constant.employee;

public enum Position {
    사원(12), // 사원 : 12일
    대리(15), // 대리 : 15일
    과장(18), // 과장 : 18일
    부장(20), // 부장 : 20일
    임원(23); // 임원 : 23일

    private final int annualLeave;

    Position(int annualLeave) {
        this.annualLeave = annualLeave;
    }

    public int getAnnualLeave() {
        return annualLeave;
    }
}
