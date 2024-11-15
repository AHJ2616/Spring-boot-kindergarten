let selectedApprovalId = null;

function approveRequest(approvalId) {
    processApproval(approvalId, 'APPROVED');
}

function showRejectModal(approvalId) {
    selectedApprovalId = approvalId;
    new bootstrap.Modal(document.getElementById('rejectModal')).show();
}

function rejectRequest() {
    const reason = document.getElementById('rejectReason').value;
    if (!reason) {
        alert('반려 사유를 입력해주세요.');
        return;
    }
    processApproval(selectedApprovalId, 'REJECTED', reason);
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
                location.reload();
            } else {
                alert('처리 중 오류가 발생했습니다.');
            }
        });
}