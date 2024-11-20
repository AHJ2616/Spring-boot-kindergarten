let stompClient = null;
let notificationCount = 0;
let sessionTimeLeft = localStorage.getItem('sessionTimeLeft') ? parseInt(localStorage.getItem('sessionTimeLeft')) : 1800; // 기본 세션 시간 30분
let timerInterval;
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

document.addEventListener('DOMContentLoaded', function() {
    // 알림 버튼 클릭 이벤트 처리
    const notificationButton = document.getElementById('notificationButton');
    const notificationDropdown = document.querySelector('.notification-dropdown');
    var profileSection = document.getElementById("profileSection");
    var dropdownMenu = document.querySelector(".dropdown-menu");

    // 알림 버튼 클릭 시 알림 드롭다운 토글
    if (notificationButton && notificationDropdown) {
        notificationButton.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationDropdown.style.display = notificationDropdown.style.display === 'none' ? 'block' : 'none';
        });

        // 문서 클릭 시 알림 드롭다운 닫기
        document.addEventListener('click', function(e) {
            if (!notificationDropdown.contains(e.target) && !notificationButton.contains(e.target)) {
                notificationDropdown.style.display = 'none';
            }
        });
    }

    // 프로필 클릭 시 드롭다운 메뉴 토글
    if (profileSection && dropdownMenu) {
        profileSection.addEventListener("click", function() {
            // 프로필 클릭 시 드롭다운 메뉴 토글
            if (profileSection.classList.contains("active")) {
                profileSection.classList.remove("active");
                dropdownMenu.style.display = "none";  // 메뉴 숨기기
                dropdownMenu.style.maxHeight = "0";  // 메뉴 닫기
            } else {
                profileSection.classList.add("active");
                dropdownMenu.style.display = "block";  // 메뉴 보이기
                dropdownMenu.style.maxHeight = dropdownMenu.scrollHeight + "px";  // 메뉴가 완전히 펼쳐지도록
            }
        });
    }

    connect(); // WebSocket 연결

    // 타이머 시작 (1초마다 타이머 업데이트)
    timerInterval = setInterval(updateTimer, 1000);
    checkSessionStatus(); // 페이지 로드시 세션 상태 확인
    updateUnreadChatCount(); // 초기 채팅 카운트 업데이트
});

// CSRF 토큰 처리 함수
function getCSRFToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCSRFHeaderToken() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

function addCSRFHeader(headers = {}) {
    const csrfToken = getCSRFToken();
    const csrfHeader = getCSRFHeaderToken();
    return {
        ...headers,
        [csrfHeader]: csrfToken
    };
}

// WebSocket 연결
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({ 'X-CSRF-TOKEN': csrfToken }, function(frame) {
        console.log('Connected: ' + frame);

        // 알림 구독
        stompClient.subscribe('/user/queue/notifications', function(notification) {
            const notificationData = JSON.parse(notification.body);
            showNotification(notificationData);
            updateNotificationCount(1); // 알림 수 증가
        });

        // 채팅 메시지 수 업데이트
        stompClient.subscribe('/user/queue/chat', function() {
            updateUnreadChatCount(); // 새 메시지 오면 카운트 업데이트
        });

        loadUnreadNotifications(); // 초기 알림 수 로드
    }, function(error) {
        console.error('STOMP error:', error);
        setTimeout(connect, 5000); // 연결 실패 시 재시도
    });
}

// 알림 수 업데이트
function updateNotificationCount(count) {
    notificationCount += count;
    const badge = document.querySelector('.notification-badge');
    if (badge) {
        badge.textContent = notificationCount;
        badge.style.display = notificationCount > 0 ? 'block' : 'none'; // 배지 표시 여부 설정
    }
}

// 초기 알림 수 로드
function loadUnreadNotifications() {
    fetch('/api/notifications/unread', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken }
    })
        .then(response => response.json())
        .then(notifications => {
            const notificationList = document.querySelector('.notification-list');
            if (notificationList) {
                notificationList.innerHTML = ''; // 기존 알림 초기화
                notifications.forEach(notification => showNotification(notification));
                updateNotificationCount(notifications.length); // 초기 알림 수 설정
            }
        })
        .catch(error => console.error('Failed to load notifications:', error));
}

// 알림 표시 함수
function showNotification(notification) {
    const notificationList = document.querySelector('.notification-list');
    const notificationItem = document.createElement('div');
    notificationItem.className = 'notification-item' + (notification.read ? ' read' : '');
    notificationItem.innerHTML = `
        <div class="notification-title">${notification.title}</div>
        <div class="notification-content">${notification.content}</div>
        <div class="notification-time">${formatDate(notification.createdAt)}</div>
    `;
    notificationItem.onclick = () => {
        markAsRead(notification.id);
        window.location.href = notification.url;
    };
    notificationList.insertBefore(notificationItem, notificationList.firstChild);
}

// 알림 읽음 처리
function markAsRead(notificationId) {
    fetch(`/api/notifications/${notificationId}/read`, {
        method: 'POST',
        headers: addCSRFHeader({ 'Content-Type': 'application/json' })
    }).then(() => loadUnreadNotifications());
}

function markAllAsRead() {

    const csrfToken = getCSRFToken();
    const csrfHeader = getCSRFHeaderToken()

    fetch('/api/notifications/read-all', {
        method: 'POST',
        headers: addCSRFHeader({
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        })
    }).then(() => {
        loadUnreadNotifications();
    });
}

// 날짜 포맷팅 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
}

// 채팅 수 업데이트
function updateUnreadChatCount() {

    const userIdElement = document.getElementById('userId');
    if (!userIdElement) {
        console.warn('userId element not found');
        return; // userId 엘리먼트가 없으면 함수 종료
    }

    const userId = userIdElement.getAttribute('data-user-id');
    if (!userId) {
        console.warn('userId not found');
        return; // userId 값이 없으면 함수 종료
    }

    fetch(`/api/chat/unread-count/${userId}`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken }
    })
        .then(response => response.json())
        .then(count => {
            const badge = document.getElementById('unreadChatCount');
            if (badge) {
                badge.textContent = count;
                badge.style.display = count > 0 ? 'inline-block' : 'none';
            }
        })
        .catch(error => console.error('Error fetching unread chat count:', error));
}

// 세션 타이머 업데이트
function updateTimer() {
    if (sessionTimeLeft > 0) {
        const minutes = Math.floor(sessionTimeLeft / 60);
        const seconds = sessionTimeLeft % 60;
        document.getElementById('sessionTimer').textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

        if (sessionTimeLeft === 300) {
            if (confirm('세션이 5분 후 만료됩니다. 연장하시겠습니까?')) {
                extendSession(); // 세션 연장 요청
            }
        }

        sessionTimeLeft--;
        localStorage.setItem('sessionTimeLeft', sessionTimeLeft); // 세션 시간 저장
    } else {
        clearInterval(timerInterval);
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        window.location.href = '/main/login';
    }
}

// 세션 연장 요청
function extendSession() {
    fetch('/main/api/extend-session', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'X-CSRF-TOKEN': csrfToken }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                sessionTimeLeft = data.timeLeft; // 연장된 시간으로 갱신
                alert('세션이 연장되었습니다.');
                localStorage.setItem('sessionTimeLeft', sessionTimeLeft);
            } else {
                alert('세션 연장에 실패했습니다: ' + data.message);
            }
        })
        .catch(error => {
            console.error('세션 연장 실패:', error);
            alert('세션 연장에 실패했습니다. 네트워크 오류가 발생했습니다.');
        });
}

// 세션 상태 확인
function checkSessionStatus() {
    fetch('/main/api/check-session')
        .then(response => response.json())
        .then(data => {
            if (data.timeLeft) {
                sessionTimeLeft = data.timeLeft;
                localStorage.setItem('sessionTimeLeft', sessionTimeLeft);
            }
        })
        .catch(error => console.error('세션 상태 확인 실패:', error));
}