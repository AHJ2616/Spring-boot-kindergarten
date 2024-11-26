package com.kinder.kindergarten.constant.employee;

public enum Position {
    사원(12, 1), // 사원 : 12일 연차, 직급 레벨 1
    대리(15, 2), // 대리 : 15일 연차, 직급 레벨 2
    과장(18, 3), // 과장 : 18일 연차, 직급 레벨 3
    부장(20, 4), // 부장 : 20일 연차, 직급 레벨 4
    임원(23, 5), // 임원 : 23일 연차, 직급 레벨 5
    대표(55, 6); // 대표 : 55일 연차, 직급 레벨 6

    private final int annualLeave;
    private final int positionLevel;

    Position(int annualLeave, int positionLevel) {
        this.annualLeave = annualLeave;
        this.positionLevel = positionLevel;
    }

    public int getAnnualLeave() {
        return annualLeave;
    }
    public int getPositionLevel() {
        return positionLevel;
    }
}
