// function submitForms() {
//     var employeeForm = document.getElementById('employeeForm');
//     var memberForm = document.getElementById('memberForm');
//
//     // 두 폼 모두 제출 나중에 멤버테이블로 각자 저장할때 사용)
//     employeeForm.submit();
//     memberForm.submit();
// }

// 실시간 이메일 유효성 검사 및 중복 검사
function validateEmail() {
    const email = document.getElementById("email").value;
    const emailError = document.getElementById("emailError");
    const emailValid = document.getElementById("emailValid");

    // 이메일 형식 검사
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    if (!emailPattern.test(email)) {
        emailError.style.display = "block";
        emailValid.style.display = "none";
        return;
    } else {
        emailError.style.display = "none";
    }

    // 이메일 중복 검사 Ajax 요청
    fetch('/employee/check-email?email=' + encodeURIComponent(email))
        .then(response => response.json())
        .then(data => {
            if (data.exists) {
                emailValid.style.display = "block";
                emailValid.textContent = "이미 사용 중인 이메일입니다.";
            } else {
                emailValid.style.display = "none";
            }
        })
        .catch(error => {
            console.error('Email check failed', error);
        });
}

// 실시간 전화번호 유효성 검사 및 중복 검사
function validatePhone() {
    const phone1 = document.getElementById('phone1').value;
    const phone2 = document.getElementById('phone2').value;
    const phone3 = document.getElementById('phone3').value;
    const phoneError = document.getElementById('phoneError');
    const phoneValid = document.getElementById('phoneValid');

    // 전화번호가 세 부분 모두 채워졌다면 유효성 검사
    if (phone1.length === 3 && phone2.length === 4 && phone3.length === 4) {
        const phoneNumber = phone1 + phone2 + phone3;
        const phoneRegex = /^\d{3}\d{4}\d{4}$/;

        if (!phoneRegex.test(phoneNumber)) {
            phoneError.style.display = "block";
            phoneValid.style.display = "none";
            return;
        } else {
            phoneError.style.display = "none";
        }

        // 전화번호 중복 검사 Ajax 요청
        fetch('/employee/check-phone?phone=' + encodeURIComponent(phoneNumber))
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    phoneValid.style.display = "block";
                    phoneValid.textContent = "이미 사용 중인 전화번호입니다.";
                } else {
                    phoneValid.style.display = "none";
                }
            })
            .catch(error => {
                console.error('Phone check failed', error);
            });
    } else {
        phoneError.style.display = "none"; // 오류 메시지 숨기기
        phoneValid.style.display = "none"; // 전화번호 중복 메시지 숨기기
    }
}

// 실시간 비밀번호 유효성 검사
function validatePassword() {
    const password = document.getElementById("password").value;
    const passwordError = document.getElementById("passwordError");
    if (password.length < 8 || password.length > 16) {
        passwordError.style.display = "block";
    } else {
        passwordError.style.display = "none";
    }
}

// 자동으로 포커스 이동 (전화번호 입력 필드)
function moveFocus(currentInput, nextInputId) {
    if (currentInput.value.length == currentInput.maxLength) {
        document.getElementById(nextInputId).focus();
    }
}

// 전화번호 결합하고 형식에 맞게 포맷팅 (서버로 전송할 전화번호 생성)
function getPhoneNumber() {
    const phone1 = document.getElementById('phone1').value;
    const phone2 = document.getElementById('phone2').value;
    const phone3 = document.getElementById('phone3').value;

    // 전화번호가 세 부분 모두 입력되었을 때 결합
    if (phone1.length === 3 && phone2.length === 4 && phone3.length === 4) {
        // 000-0000-0000 형식으로 전화번호를 포맷팅
        return phone1 + '-' + phone2 + '-' + phone3;
    }

    return '';  // 전화번호가 완전하지 않으면 빈 문자열 반환
}

// 폼 전송 전 유효성 검사
function validateForm() {
    const emailValid = document.getElementById("emailError").style.display === "none";
    const phoneValid = document.getElementById("phoneError").style.display === "none";
    const passwordValid = document.getElementById("passwordError").style.display === "none";

    // 이메일, 전화번호, 비밀번호 모두 유효하면 true를 반환, 그렇지 않으면 false
    if (emailValid && phoneValid && passwordValid) {
        // 전화번호를 결합하여 employeeDTO.phone에 할당
        const phone = getPhoneNumber();
        if (phone) {
            // 실제 서버로 제출할 때 phone 값을 employeeDTO.phone에 추가
            document.getElementById('phone').value = phone;  // hidden input에 결합된 전화번호 할당
        }

        return true;  // 폼 제출을 허용
    } else {
        return false;  // 폼 제출을 방지
    }
}

// 폼 제출 시 전화번호 결합
document.querySelector("form").addEventListener("submit", function(event) {
    // 전화번호를 결합하여 hidden input에 저장
    const phone = getPhoneNumber();
    if (phone) {
        document.getElementById("phone").value = phone; // hidden input에 전화번호 값을 설정
    }
});