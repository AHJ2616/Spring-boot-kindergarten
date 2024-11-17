// 동의 상태 관리 클래스
class ConsentManager {
    constructor() {
        this.STORAGE_KEY = 'consentStates';
        this.CONSENT_TYPES = {
            FIRST_STEP: ['termsConsent', 'communityConsent', 'privateConsent'],
            SECOND_STEP: ['photoConsent', 'medicalInfoConsent', 'emergencyInfoConsent']
        };
    }

    // 모든 동의 상태 가져오기
    getAllConsents() {
        const states = sessionStorage.getItem(this.STORAGE_KEY);
        return states ? JSON.parse(states) : {};
    }

    // 특정 동의 상태 가져오기
    getConsent(type) {
        const states = this.getAllConsents();
        return states[type] || false;
    }

    // 동의 상태 저장
    setConsent(type, value) {
        const states = this.getAllConsents();
        states[type] = value;
        sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(states));
    }

    // 모든 동의 상태 초기화
    clearConsents() {
        sessionStorage.removeItem(this.STORAGE_KEY);
    }

    // URL 파라미터로부터 동의 상태 초기화
    initFromUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const states = {};

        [...this.CONSENT_TYPES.FIRST_STEP, ...this.CONSENT_TYPES.SECOND_STEP].forEach(type => {
            if (urlParams.get(type) === 'true') {
                states[type] = true;
            }
        });

        sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(states));
    }
}

// 동의서 UI 관리 클래스
class ConsentUI {
    constructor(consentManager) {
        this.manager = consentManager;
    }

    // 체크박스 상태 업데이트
    updateCheckbox(name, value) {
        const checkbox = document.querySelector(`input[name="${name}"]`);
        if (checkbox) {
            checkbox.checked = value;
            this.updateStatus(checkbox);
        }
    }

    // 상태 배지 업데이트
    updateStatus(checkbox) {
        const statusBadge = checkbox.closest('.consent-item').querySelector('.consent-status');
        if (statusBadge) {
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
    }

    // 모든 체크박스 상태 업데이트
    updateAllCheckboxes() {
        const states = this.manager.getAllConsents();
        Object.entries(states).forEach(([type, value]) => {
            this.updateCheckbox(type, value);
        });
    }

    // 전체 동의 체크박스 상태 업데이트
    updateAllConsentCheckbox() {
        const allConsentCheckbox = document.getElementById('allConsent');
        if (!allConsentCheckbox) return;

        const checkboxes = document.querySelectorAll('input[type="checkbox"]:not(#allConsent)');
        const allChecked = Array.from(checkboxes).every(cb => cb.checked);
        allConsentCheckbox.checked = allChecked;
    }
}

// 전역 인스턴스 생성
const consentManager = new ConsentManager();
const consentUI = new ConsentUI(consentManager);

// 이벤트 핸들러
window.agreeAndReturn = function() {
    const pagePath = window.location.pathname;

    // 현재 동의 추가
    const consentType = getConsentTypeFromPath(pagePath);
    if (consentType) {
        consentManager.setConsent(consentType, true);
    }

    // URL 파라미터 생성 (모든 동의 상태 포함)
    const params = getConsentParams();

    // 리다이렉트 URL 결정
    const redirectUrl = isSecondStepPath(pagePath) ? '/consent/second' : '/consent';

    window.location.href = `${redirectUrl}?${params.toString()}`;
}

// 동의 상태를 URL 파라미터로 변환하는 유틸리티 함수
function getConsentParams() {
    const states = consentManager.getAllConsents();
    const params = new URLSearchParams();
    Object.entries(states).forEach(([key, value]) => {
        if (value) params.set(key, 'true');
    });
    return params;
}


// 개별 동의 처리
window.agreeToItem = function(itemName) {
    // 현재 동의 상태 저장
    consentManager.setConsent(itemName, true);

    // UI 업데이트
    const states = consentManager.getAllConsents();
    Object.entries(states).forEach(([key, value]) => {
        if (value) {
            consentUI.updateCheckbox(key, true);
        }
    });

    consentUI.updateAllConsentCheckbox();

    // URL 업데이트
    const params = getConsentParams();
    const newUrl = `${window.location.pathname}?${params.toString()}`;
    window.history.replaceState({}, '', newUrl);
}

// 동의 취소 처리 함수도 같은 방식으로 수정
window.disagreeToItem = function(itemName) {
    const states = consentManager.getAllConsents();
    states[itemName] = false;

    Object.entries(states).forEach(([key, value]) => {
        consentManager.setConsent(key, value);
        consentUI.updateCheckbox(key, value);
    });

    consentUI.updateAllConsentCheckbox();

    // URL 파라미터 업데이트
    const params = new URLSearchParams();
    Object.entries(states).forEach(([key, value]) => {
        if (value) params.set(key, 'true');
    });

    const newUrl = `${window.location.pathname}?${params.toString()}`;
    window.history.replaceState({}, '', newUrl);
}

window.handleAllConsent = function() {
    const allConsentCheckbox = document.getElementById('allConsent');
    if (!allConsentCheckbox) return;

    const checkboxes = document.querySelectorAll('input[type="checkbox"]:not(#allConsent)');
    const isChecked = allConsentCheckbox.checked;

    // 모든 체크박스 상태 변경
    checkboxes.forEach(checkbox => {
        checkbox.checked = isChecked;
        consentManager.setConsent(checkbox.name, isChecked);
        consentUI.updateStatus(checkbox);
    });

    // URL 파라미터 업데이트
    const params = getConsentParams();
    const newUrl = `${window.location.pathname}?${params.toString()}`;
    window.history.replaceState({}, '', newUrl);
}

// 페이지 로드 시 초기화 수정
document.addEventListener('DOMContentLoaded', function() {
    // URL 파라미터에서 동의 상태 초기화
    const urlParams = new URLSearchParams(window.location.search);

    // 모든 가능한 동의 타입에 대해 체크
    ['termsConsent', 'communityConsent', 'privateConsent',
        'photoConsent', 'medicalInfoConsent', 'emergencyInfoConsent'].forEach(param => {
        if (urlParams.get(param) === 'true') {
            consentManager.setConsent(param, true);
        }
    });

    // UI 업데이트
    const states = consentManager.getAllConsents();
    Object.entries(states).forEach(([type, value]) => {
        if (value) {
            const checkbox = document.querySelector(`input[name="${type}"]`);
            if (checkbox) {
                checkbox.checked = true;
                consentUI.updateStatus(checkbox);
            }
        }
    });

    // 전체 동의 체크박스 이벤트 리스너 추가
    const allConsentCheckbox = document.getElementById('allConsent');
    if (allConsentCheckbox) {
        allConsentCheckbox.addEventListener('change', handleAllConsent);
    }

    consentUI.updateAllConsentCheckbox();
});

// 경로에서 동의 타입 추출
function getConsentTypeFromPath(path) {
    const pathMap = {
        '/consent/photo': 'photoConsent',
        '/consent/medical': 'medicalInfoConsent',
        '/consent/emergency': 'emergencyInfoConsent',
        '/consent/terms': 'termsConsent',
        '/consent/community': 'communityConsent',
        '/consent/privacy': 'privateConsent'
    };
    return pathMap[path];
}

function isSecondStepPath(path) {
    return path.includes('/consent/second') ||
        path.includes('/consent/photo') ||
        path.includes('/consent/medical') ||
        path.includes('/consent/emergency');
}

// checkAndProceed 함수 추가
window.checkAndProceed = function() {
    const requiredConsents = ['termsConsent', 'communityConsent', 'privateConsent'];
    const allConsented = requiredConsents.every(consent =>
        consentManager.getConsent(consent)
    );

    if (allConsented) {
        // 현재 동의 상태를 URL 파라미터로 변환
        const params = new URLSearchParams();
        const states = consentManager.getAllConsents();
        Object.entries(states).forEach(([key, value]) => {
            if (value) params.set(key, 'true');
        });
        window.location.href = `/consent/second?${params.toString()}`;
    } else {
        alert('모든 필수 항목에 동의해주셔야 다음 단계로 진행할 수 있습니다.');
    }
}