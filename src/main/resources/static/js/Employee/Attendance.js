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