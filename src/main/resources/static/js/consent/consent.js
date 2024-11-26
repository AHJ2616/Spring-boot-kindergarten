// 동의 상태 관리 클래스
class ConsentManager {
    constructor() {
        this.STORAGE_KEY = 'consentStates';
        this.CONSENT_TYPES = {
            FIRST_STEP: ['termsConsent', 'communityConsent', 'privateConsent'],
            SECOND_STEP: ['photoConsent', 'medicalInfoConsent', 'emergencyInfoConsent']
        };
    }

    getAllConsents() {
        const states = sessionStorage.getItem(this.STORAGE_KEY);
        return states ? JSON.parse(states) : {};
    }

    getConsent(type) {
        const states = this.getAllConsents();
        return states[type] || false;
    }

    setConsent(type, value) {
        const states = this.getAllConsents();
        states[type] = value;
        sessionStorage.setItem(this.STORAGE_KEY, JSON.stringify(states));
        localStorage.setItem(type, value);

        // hidden input과 checkbox 모두 업데이트
        const hiddenInput = document.getElementById(type);
        const checkbox = document.querySelector(`input[type="checkbox"][name="${type}"]`);

        if (hiddenInput) hiddenInput.value = value;
        if (checkbox) checkbox.checked = value;
    }
}

// 동의서 UI 관리 클래스
class ConsentUI {
    constructor(consentManager) {
        this.manager = consentManager;
    }

    updateCheckbox(name, value) {
        // checkbox와 hidden input 업데이트
        const checkbox = document.querySelector(`input[type="checkbox"][name="${name}"]`);
        const hiddenInput = document.getElementById(name);

        if (checkbox) checkbox.checked = value;
        if (hiddenInput) hiddenInput.value = value;

        // 상위 consent-item 찾기
        const consentItem = checkbox?.closest('.consent-item') ||
            hiddenInput?.closest('.consent-item');

        if (!consentItem) {
            console.warn(`Consent item container not found for: ${name}`);
            return;
        }

        // 상태 표시 요소 업데이트
        const statusElement = consentItem.querySelector('.consent-status');
        if (statusElement) {
            statusElement.textContent = value ? '동의완료' : '미동의';
            statusElement.classList.remove(value ? 'status-pending' : 'status-agreed');
            statusElement.classList.add(value ? 'status-agreed' : 'status-pending');
        }

        // 버튼 상태 업데이트
        const agreeBtn = consentItem.querySelector('.btn-outline-success');
        const disagreeBtn = consentItem.querySelector('.btn-outline-danger');
        if (agreeBtn && disagreeBtn) {
            if (value) {
                agreeBtn.classList.add('active');
                disagreeBtn.classList.remove('active');
            } else {
                agreeBtn.classList.remove('active');
                disagreeBtn.classList.add('active');
            }
        }
    }

    updateAllConsentCheckbox() {
        const allConsentCheckbox = document.getElementById('allConsent');
        if (!allConsentCheckbox) return;

        const currentPage = window.location.pathname;
        const consentTypes = currentPage.includes('second') ?
            this.manager.CONSENT_TYPES.SECOND_STEP :
            this.manager.CONSENT_TYPES.FIRST_STEP;

        const allChecked = consentTypes.every(type => this.manager.getConsent(type));
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
window.agreeToItem = function(consentType) {
    consentManager.setConsent(consentType, true);
    consentUI.updateCheckbox(consentType, true);
    consentUI.updateAllConsentCheckbox();
}

// 미동의 처리 함수
window.disagreeToItem = function(consentType) {
    consentManager.setConsent(consentType, false);
    consentUI.updateCheckbox(consentType, false);
    consentUI.updateAllConsentCheckbox();
}

window.handleAllConsent = function() {
    const allConsentCheckbox = document.getElementById('allConsent');
    if (!allConsentCheckbox) return;

    const isChecked = allConsentCheckbox.checked;
    const currentPage = window.location.pathname;
    const consentTypes = currentPage.includes('second') ?
        consentManager.CONSENT_TYPES.SECOND_STEP :
        consentManager.CONSENT_TYPES.FIRST_STEP;

    consentTypes.forEach(type => {
        consentManager.setConsent(type, isChecked);
        consentUI.updateCheckbox(type, isChecked);
    });
}

// 페이지 로드 시 초기화 수정
document.addEventListener('DOMContentLoaded', function() {
    const currentPage = window.location.pathname;
    const consentTypes = currentPage.includes('second') ?
        consentManager.CONSENT_TYPES.SECOND_STEP :
        consentManager.CONSENT_TYPES.FIRST_STEP;

    consentTypes.forEach(type => {
        const savedValue = localStorage.getItem(type) === 'true';
        consentManager.setConsent(type, savedValue);
        consentUI.updateCheckbox(type, savedValue);
    });

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
    const requiredConsents = consentManager.CONSENT_TYPES.FIRST_STEP;
    const allConsented = requiredConsents.every(consent => {
        const isConsented = consentManager.getConsent(consent);
        if (!isConsented) {
            console.log(`${consent} is not consented`);
        }
        return isConsented;
    });

    if (allConsented) {
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

window.prepareAndSubmit = function() {
    // hidden input들의 값을 설정
    const states = consentManager.getAllConsents();
    Object.entries(states).forEach(([key, value]) => {
        const hiddenInput = document.querySelector(`input[type="hidden"][name="${key}"]`);
        if (hiddenInput) {
            hiddenInput.value = value;
        }
    });

    // 모든 필수 동의가 완료되었는지 확인
    const currentPage = window.location.pathname;
    const requiredConsents = currentPage.includes('second') ?
        consentManager.CONSENT_TYPES.SECOND_STEP :
        consentManager.CONSENT_TYPES.FIRST_STEP;

    const allConsented = requiredConsents.every(type => {
        const isConsented = states[type] === true;
        if (!isConsented) {
            console.log(`${type} is not consented`);
        }
        return isConsented;
    });

    if (!allConsented) {
        alert('모든 필수 항목에 동의해주셔야 다음 단계로 진행할 수 있습니다.');
        return false;
    }

    return true;
}