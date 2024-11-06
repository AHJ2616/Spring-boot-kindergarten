function toggleEndDateField() {
    const leaveType = document.getElementById('leaveType').value;
    const endDateField = document.getElementById('endDateField');
    const durationDisplay = document.getElementById('durationDisplay');
    const totalLeaveField = document.getElementById('le_total'); // le_total 필드

    if (leaveType === '반차') {
        endDateField.style.display = 'none'; // 반차 선택 시 종료일 필드 숨김
        totalLeaveField.value = 0.5; // le_total 값을 0.5로 설정
        const startDate = document.getElementById('startDate').value;
        if (startDate) {
            // 시작일이 있을 경우 종료일에 동일한 값 설정
            endDateField.value = startDate;
        }
        durationDisplay.textContent = '사용휴가: 0.5일'; // 반차일 경우 0.5일 표시
    } else {
        endDateField.style.display = 'block'; // 그 외 선택 시 종료일 필드 보임
        totalLeaveField.value = ''; // le_total 초기화
        calculateDuration(); // 종료일 필드가 보이면 기간 계산
    }
}

function calculateDuration() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const leaveType = document.getElementById('leaveType').value;
    const durationDisplay = document.getElementById('durationDisplay');
    const totalLeaveField = document.getElementById('le_total'); // le_total 필드

    if (leaveType === '반차') {
        durationDisplay.textContent = '사용휴가: 0.5일'; // 반차일 경우 0.5일 표시
    } else if (startDate && endDate) {
        const start = new Date(startDate);
        const end = new Date(endDate);

        if (end < start) {
            durationDisplay.textContent = '종료일은 시작일 이후여야 합니다.';
        } else {
            const daysDiff = Math.ceil((end - start) / (1000 * 3600 * 24)) + 1; // 포함일 계산
            durationDisplay.textContent = `사용휴가: ${daysDiff}일`;
            totalLeaveField.value = daysDiff; // le_total 값을 계산된 일수로 설정
        }
    } else {
        durationDisplay.textContent = '사용휴가: 0일'; // 초기화
        totalLeaveField.value = ''; // le_total 초기화
    }
}