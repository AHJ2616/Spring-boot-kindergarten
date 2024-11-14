let currentApprovalId = null;

function approveRequest(approvalId) {
    if (confirm('승인하시겠습니까?')) {
        processApproval(approvalId, 'APPROVED');
    }
}

function showRejectModal(approvalId) {
    currentApprovalId = approvalId;
    document.getElementById('rejectModal').style.display = 'block';
    document.getElementById('rejectReason').value = '';  // 반려 사유 초기화
}

function closeRejectModal() {
    document.getElementById('rejectModal').style.display = 'none';
}

function submitReject() {
    const reason = document.getElementById('rejectReason').value;
    if (!reason.trim()) {
        alert('반려 사유를 입력해주세요.');
        return;
    }
    processApproval(currentApprovalId, 'REJECTED', reason);  // 반려 사유와 함께 처리
    closeRejectModal();
}

// 특정 아이템 DOM 요소를 업데이트하는 함수 예시
function updateApprovalStatus(approvalId, status) {
    const itemElement = document.querySelector(`#approval-item-${approvalId}`);
    if (itemElement) {
        itemElement.querySelector('.status').innerText = status === 'APPROVED' ? '승인됨' : '반려됨';
        itemElement.classList.add(status.toLowerCase());
    }
}

// 승인 및 반려 요청을 처리하는 함수
function processApproval(approvalId, status, rejectReason = '') {
    const data = new URLSearchParams();

    // CSRF 토큰을 메타 태그에서 읽어오기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    data.append('status', status);
    if (rejectReason) {
        data.append('rejectReason', rejectReason);
    }

    fetch(`/approval/process/${approvalId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded', // 데이터가 URL 인코딩 형식임을 명시
            [csrfHeader]: csrfToken, // CSRF 토큰을 헤더에 추가
        },
        body: data // request body에 데이터를 추가
    })
        .then(response => {
            if (response.ok) {
                alert(status === 'APPROVED' ? '승인되었습니다.' : '반려되었습니다.');
                updateApprovalStatus(approvalId, status); // 상태 업데이트
                closeRejectModal(); // 모달 창 닫기
            } else {
                alert('처리 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('처리 중 오류가 발생했습니다.');
        });
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeRejectModal();
    }
});

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target === document.getElementById('rejectModal')) {
        closeRejectModal();
    }
}