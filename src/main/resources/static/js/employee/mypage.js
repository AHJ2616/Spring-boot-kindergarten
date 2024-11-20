const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

function toggleEditMode() {
    const editables = document.querySelectorAll('.info-value.editable');
    editables.forEach(el => {
        el.style.backgroundColor = '#fff8dc';
    });
}

function editField(field, element) {

    // 수정 가능한 필드인지 확인
    if (!element.classList.contains('editable')) {
        return;
    }

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

// 정보수정
function handleEditFormSubmit(e) {
    e.preventDefault();  // 기본 폼 제출을 막음
    const field = document.getElementById('editField').value;
    const value = document.getElementById('editInput').value;

    // AJAX 요청으로 서버에 업데이트 요청
    fetch(`/employee/update/${field}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken,// CSRF 토큰을 헤더에 추가
        },
        body: JSON.stringify({
            value: value

        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // 성공 시 화면 업데이트
                const element = document.querySelector(`[data-field="${field}"]`);
                if (element) {
                    element.textContent = value;  // 새로 입력된 값을 화면에 반영
                }
                closeModal();  // 모달을 닫음
                alert('수정이 완료되었습니다.');
            } else {
                alert('업데이트 실패');
            }
        })
        .catch(error => {
            alert('업데이트 중 오류가 발생했습니다.');
        });
}

// 출퇴근 기능
function checkIn() {
    fetch('/attendance/check-in', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken  // CSRF 토큰을 헤더에 추가
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                if (data.attendance && data.attendance.message === "이미 출근 처리되었습니다.") {
                    // 이미 출근한 경우
                    alert("이미 출근되었습니다.");
                } else {
                    // 출근 성공
                    alert(data.message);
                    location.reload();  // 페이지 새로고침
                }
            } else {
                alert(data.message);  // 실패 메시지
            }
        })
        .catch(error => {
            alert('출근 처리 중 오류가 발생했습니다.' + error);
            console.log(error);
        });
}

function checkOut() {
    fetch('/attendance/check-out', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken  // CSRF 토큰을 헤더에 추가
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);  // 퇴근 성공 메시지
                location.reload();  // 페이지 새로고침
            } else {
                alert(data.message);  // 실패 메시지
            }
        })
        .catch(error => {
            alert('퇴근 처리 중 오류가 발생했습니다.' + error);
            console.log(error);
        });
}

function toggleCertificateForm() {
    const newCertificateRow = document.getElementById('newCertificatesRow');
    newCertificateRow.style.display = newCertificateRow.style.display === 'none' ? 'table-row' : 'none';
}

function toggleEducationForm() {
    const newEducationRow = document.getElementById('newEducationRow');
    newEducationRow.style.display = newEducationRow.style.display === 'none' ? 'table-row' : 'none';
}

// 파일 선택 버튼 클릭 시 해당 파일 input 열기
function selectFile(inputId) {
    var inputElement = document.getElementById(inputId);
    if (inputElement) {
        inputElement.click();  // 클릭하여 파일 선택 다이얼로그 열기
    } else {
        console.error("파일 입력 요소를 찾을 수 없습니다: " + inputId);
    }
}

// 파일명이 변경될 때 파일 이름 업데이트 (선택된 파일 이름을 표시)
function updateFileName() {
    var input = event.target;
    var fileName = input.files.length > 0 ? input.files[0].name : '파일 선택 안 됨';
    var button = input.previousElementSibling;
    button.innerText = fileName;  // 파일 이름을 표시하는 버튼에 업데이트
}

// 폼 제출 시 파일 포함 여부를 확인하고 제출
function submitForm(formId) {
    var form = document.getElementById(formId);
    if (form) {
        form.submit();  // 폼을 서버로 전송
    } else {
        console.error("폼을 찾을 수 없습니다: " + formId);
    }
}

 function uploadProfileImage(input) {
     if (input.files && input.files[0]) {
         const formData = new FormData();
         formData.append('file', input.files[0]);

         const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
         const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");


         fetch('/employee/update-profile-image', {
             method: 'POST',
             headers:{
                 [csrfHeader]: csrfToken
             },
             body: formData
         })
             .then(response => response.json())
             .then(data => {
                 if (data.success) {
                     location.reload();
                 } else {
                     alert(data.message);
                 }
             })
             .catch(error => {
                 alert('이미지 업로드 중 오류가 발생했습니다.');
             });
     }
 }
