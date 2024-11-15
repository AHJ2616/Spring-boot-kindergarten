let currentApprovalId = null;

function approveRequest(approvalId) {
    if (confirm('승인하시겠습니까?')) {
        processApproval(approvalId, 'APPROVED');
    }
}

function showRejectModal(approvalId) {
    currentApprovalId = approvalId;
    document.getElementById('rejectModal').style.display = 'block';
    document.getElementById('rejectReason').value = '';
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
    processApproval(currentApprovalId, 'REJECTED', reason);
    closeRejectModal();
}

function processApproval(approvalId, status, rejectReason = '') {
    const data = new URLSearchParams();
    data.append('status', status);
    if (rejectReason) {
        data.append('rejectReason', rejectReason);
    }

    fetch(`/approval/process/${approvalId}`, {
        method: 'POST',
        body: data
    })
        .then(response => {
            if (response.ok) {
                alert(status === 'APPROVED' ? '승인되었습니다.' : '반려되었습니다.');
                location.reload();
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