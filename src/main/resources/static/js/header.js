// 1. ul li 클릭 시 'active' 클래스 토글
document.querySelectorAll('ul li').forEach(function(item) {
    item.addEventListener('click', function() {
        // 모든 li에서 'active' 클래스를 제거
        document.querySelectorAll('li').forEach(function(li) {
            li.classList.remove('active');
        });
        // 클릭된 li에 'active' 클래스를 추가
        this.classList.add('active');
    });
});

// 2. .icon-button 클릭 시 'active' 클래스 토글
document.querySelectorAll('.icon-button').forEach(function(button) {
    button.addEventListener('click', function(e) {
        e.stopPropagation(); // 클릭 이벤트가 부모로 전파되는 것을 방지
        this.classList.toggle('active'); // 'active' 클래스를 토글
    });
});

// 3. 문서 클릭 시 모든 .icon-button에서 'active' 클래스 제거
document.addEventListener('click', function() {
    document.querySelectorAll('.icon-button').forEach(function(button) {
        button.classList.remove('active');
    });
});

// 4. 드롭다운 메뉴
document.getElementById("profileSection").addEventListener("click", function() {
    var profileSection = document.getElementById("profileSection");
    var dropdownMenu = document.querySelector(".dropdown-menu");

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

// 5. 세션 타이머
let sessionTimeLeft = /*[[${#session.maxInactiveInterval}]]*/ 1800; // 서버의 세션 시간을 가져옴
let timerInterval;
const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");


function updateTimer() {
    if (sessionTimeLeft > 0) {
        const minutes = Math.floor(sessionTimeLeft / 60);
        const seconds = sessionTimeLeft % 60;
        document.getElementById('sessionTimer').textContent =
            `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

        // 5분(300초) 남았을 때 경고
        if (sessionTimeLeft === 300) {
            if (confirm('세션이 5분 후 만료됩니다. 연장하시겠습니까?')) {
                extendSession();
            }
        }

        sessionTimeLeft--;
    } else {
        clearInterval(timerInterval);
        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
        window.location.href = '/main/login';
    }
}

function extendSession() {
    fetch('/main/api/extend-session', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                sessionTimeLeft = 1800; // 30분으로 리셋
                alert('세션이 연장되었습니다.');
            }
        })
        .catch(error => {
            console.error('세션 연장 실패:', error);
            alert('세션 연장에 실패했습니다.');
        });
}

// 페이지 로드시 타이머 시작
document.addEventListener('DOMContentLoaded', function() {
    // 1초마다 타이머 업데이트
    timerInterval = setInterval(updateTimer, 1000);
});

// 페이지 활성화 감지
document.addEventListener('visibilitychange', function() {
    if (!document.hidden) {
        // 페이지가 활성화되면 서버에 현재 세션 상태 확인
        fetch('/main/api/check-session')
            .then(response => response.json())
            .then(data => {
                if (data.timeLeft) {
                    sessionTimeLeft = data.timeLeft;
                }
            });
    }
});

// 주기적으로 세션 상태 확인 (1분마다)
setInterval(() => {
    if (!document.hidden) {
        fetch('/main/api/check-session')
            .then(response => response.json())
            .then(data => {
                if (data.timeLeft) {
                    sessionTimeLeft = data.timeLeft;
                }
            });
    }
}, 60000);