function toggleEditMode() {
    const editables = document.querySelectorAll('.info-value.editable');
    editables.forEach(el => {
        el.style.backgroundColor = '#fff8dc';
    });
}

function editField(field, element) {
    const modal = document.getElementById('editModal');
    const input = document.getElementById('editInput');
    const fieldInput = document.getElementById('editField');

    input.value = element.textContent;
    fieldInput.value = field;
    modal.style.display = 'block';
}

function closeModal() {
    document.getElementById('editModal').style.display = 'none';
}

document.getElementById('editForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const field = document.getElementById('editField').value;
    const value = document.getElementById('editInput').value;

    // AJAX 요청으로 서버에 업데이트 요청
    fetch(`/employee/update/${field}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ value: value })
    })
        .then(response => response.json())
        .then(data => {
            if(data.success) {
                // 성공시 화면 업데이트
                document.querySelector(`[data-field="${field}"]`).textContent = value;
                closeModal();
            }
        });
});

// 메모 저장
function saveMemo() {
    const memo = document.getElementById('memoArea').value;
    fetch('/employee/save-memo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ memo: memo })
    })
        .then(response => response.json())
        .then(data => {
            if(data.success) {
                alert('메모가 저장되었습니다.');
            }
        });
}

// 출퇴근 기능
function checkIn() {
    fetch('/attendance/check-in', {
        method: 'POST'
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert(data.message);
            }
        })
        .catch(error => alert('출근 처리 중 오류가 발생했습니다.'));
}

function checkOut() {
    fetch('/attendance/check-out', {
        method: 'POST'
    })
        .then(response => response.json())
        .then(data => {
            alert('퇴근 처리되었습니다.');
            location.reload();
        })
        .catch(error => alert('퇴근 처리 중 오류가 발생했습니다.'));
}

// 휴가 신청 모달
function openLeaveModal() {
    document.getElementById('leaveModal').style.display = 'block';
}

function closeLeaveModal() {
    document.getElementById('leaveModal').style.display = 'none';
}

function toggleEndDate() {
    const leaveType = document.getElementById('leaveType').value;
    const endDateInput = document.getElementById('leaveEnd');
    if (leaveType === '반차') {
        endDateInput.style.display = 'none';
    } else {
        endDateInput.style.display = 'block';
    }
}

document.getElementById('leaveForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const leaveData = {
        startDate: document.getElementById('leaveStart').value,
        endDate: document.getElementById('leaveEnd').value,
        leaveType: document.getElementById('leaveType').value
    };

    fetch('/leave/request', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(leaveData)
    })
        .then(response => response.json())
        .then(data => {
            if(data.success) {
                alert('휴가 신청이 완료되었습니다.');
                closeLeaveModal();
                location.reload();
            } else {
                alert(data.message);
            }
        });
});

// 페이지 로드 시 저장된 메모 불러오기
window.addEventListener('load', function() {
    fetch('/employee/get-memo')
        .then(response => response.json())
        .then(data => {
            if(data.memo) {
                document.getElementById('memoArea').value = data.memo;
            }
        });
});