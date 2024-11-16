// 체크박스 상태 변경 시 상태 배지 업데이트
function updateStatus(checkbox) {
    const statusBadge = checkbox.closest('.consent-item').querySelector('.consent-status');
    if (checkbox.checked) {
        statusBadge.textContent = '동의완료';
        statusBadge.classList.remove('status-pending');
        statusBadge.classList.add('status-agreed');
    } else {
        statusBadge.textContent = '미동의';
        statusBadge.classList.remove('status-agreed');
        statusBadge.classList.add('status-pending');
    }
}

// URL 파라미터에서 동의 상태 확인 및 체크박스 업데이트
function initializeConsents() {
    const urlParams = new URLSearchParams(window.location.search);

    if (urlParams.get('agreed') === 'true') {
        const termsCheckbox = document.querySelector('input[name="termsConsent"]');
        if (termsCheckbox) {
            termsCheckbox.checked = true;
            updateStatus(termsCheckbox);
        }
    }

    if (urlParams.get('communityAgreed') === 'true') {
        const communityCheckbox = document.querySelector('input[name="communityConsent"]');
        if (communityCheckbox) {
            communityCheckbox.checked = true;
            updateStatus(communityCheckbox);
        }
    }

    if (urlParams.get('privacyAgreed') === 'true') {
        const privacyCheckbox = document.querySelector('input[name="privateConsent"]');
        if (privacyCheckbox) {
            privacyCheckbox.checked = true;
            updateStatus(privacyCheckbox);
        }
    }

    if (urlParams.get('photoAgreed') === 'true'){
        const photoCheckbox = document.querySelector('input[name="photoConsent"]');
        if (photoCheckbox) {
            photoCheckbox.checked = true;
            updateStatus(photoCheckbox);
        }
    }

    if (urlParams.get('medicalAgreed') === 'true') {
        const medicalCheckbox = document.querySelector('input[name="medicalConsent"]');
        if (medicalCheckbox) {
            medicalCheckbox.checked = true;
            updateStatus(medicalCheckbox);
        }
    }

    if (urlParams.get('emergencyAgreed') === 'true') {
        const emergencyCheckbox = document.querySelector('input[name="emergencyConsent"]');
        if (emergencyCheckbox) {
            emergencyCheckbox.checked = true;
            updateStatus(emergencyCheckbox);
        }
    }
}

// 전체 동의 체크박스 이벤트 처리
function handleAllConsent() {
    const allConsentCheckbox = document.getElementById('allConsent');
    if (!allConsentCheckbox) return;

    const individualCheckboxes = document.querySelectorAll('input[type="checkbox"]:not(#allConsent)');

    allConsentCheckbox.addEventListener('change', function() {
        const isChecked = this.checked;
        individualCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
            updateStatus(checkbox);
        });
    });

    individualCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const allChecked = Array.from(individualCheckboxes).every(cb => cb.checked);
            allConsentCheckbox.checked = allChecked;
            updateStatus(this);
        });
    });
}

// 개별 동의 처리 함수
function agreeToItem(itemName) {
    const checkbox = document.querySelector(`input[name="${itemName}"]`);
    if (checkbox) {
        checkbox.checked = true;
        updateStatus(checkbox);
        updateAllConsentCheckbox();
    }
}

function disagreeToItem(itemName) {
    const checkbox = document.querySelector(`input[name="${itemName}"]`);
    if (checkbox) {
        checkbox.checked = false;
        updateStatus(checkbox);
        updateAllConsentCheckbox();
    }
}

// 모든 체크박스 상태에 따라 '모두 동의' 체크박스 상태 업데이트
function updateAllConsentCheckbox() {
    const allConsentCheckbox = document.getElementById('allConsent');
    if (!allConsentCheckbox) return;

    const individualCheckboxes = document.querySelectorAll('input[type="checkbox"]:not(#allConsent)');
    const allChecked = Array.from(individualCheckboxes).every(cb => cb.checked);
    allConsentCheckbox.checked = allChecked;
}

// 다음 단계로 진행하기 전 체크
function checkAndProceed() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]:not(#allConsent)');
    const allChecked = Array.from(checkboxes).every(checkbox => checkbox.checked);

    if (allChecked) {
        location.href = '/consent/second';
    } else {
        alert('모든 필수 항목에 동의해주셔야 다음 단계로 진행할 수 있습니다.');
    }
}

// 각 약관 페이지에서 동의 처리
function agreeAndReturn() {
    const urlParams = new URLSearchParams(window.location.search);
    const agreed = urlParams.get('agreed');
    const communityAgreed = urlParams.get('communityAgreed');
    const privacyAgreed = urlParams.get('privacyAgreed');
    const photoAgreed = urlParams.get('photoAgreed');
    const medicalAgreed = urlParams.get('medicalAgreed');
    const emergencyAgreed = urlParams.get('emergencyAgreed');
    const pagePath = window.location.pathname;

    let redirectUrl = '/consent?';

    if (pagePath.includes('terms')) {
        redirectUrl += 'agreed=true';
        if (communityAgreed === 'true') redirectUrl += '&communityAgreed=true';
        if (privacyAgreed === 'true') redirectUrl += '&privacyAgreed=true';
    } else if (pagePath.includes('community')) {
        redirectUrl += 'communityAgreed=true';
        if (agreed === 'true') redirectUrl += '&agreed=true';
        if (privacyAgreed === 'true') redirectUrl += '&privacyAgreed=true';
    } else if (pagePath.includes('privacy')) {
        redirectUrl += 'privacyAgreed=true';
        if (agreed === 'true') redirectUrl += '&agreed=true';
        if (communityAgreed === 'true') redirectUrl += '&communityAgreed=true';
    } else if (pagePath.includes('photo')) {
        redirectUrl = '/consent/second?photoAgreed=true';
        if (agreed === 'true') redirectUrl += '&agreed=true';
        if (communityAgreed === 'true') redirectUrl += '&communityAgreed=true';
        if (privacyAgreed === 'true') redirectUrl += '&privacyAgreed=true';
        if (medicalAgreed === 'true') redirectUrl += '&medicalAgreed=true';
    } else if (pagePath.includes('medical')) {
        redirectUrl = '/consent/second?medicalAgreed=true';
        if (agreed === 'true') redirectUrl += '&agreed=true';
        if (communityAgreed === 'true') redirectUrl += '&communityAgreed=true';
        if (privacyAgreed === 'true') redirectUrl += '&privacyAgreed=true';
    } else if (pagePath.includes('emergency')) {
        redirectUrl = '/consent/second?emergencyAgreed=true';
        if (agreed === 'true') redirectUrl += '&agreed=true';
        if (communityAgreed === 'true') redirectUrl += '&communityAgreed=true';
        if (privacyAgreed === 'true') redirectUrl += '&privacyAgreed=true';
        if (photoAgreed === 'true') redirectUrl += '&photoAgreed=true';
        if (medicalAgreed === 'true') redirectUrl += '&medicalAgreed=true';
    }

    location.href = redirectUrl;
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeConsents();
    handleAllConsent();
});

